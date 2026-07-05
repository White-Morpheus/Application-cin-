package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

// Model used in UI & state representing a search result
data class SearchResult(
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val genre: String,
    val mediaType: String, // "FILM", "SERIE", "ANIME"
    val rating: Int // 1 to 5
)

data class AdditionalMediaDetails(
    val releaseDate: String?,
    val durationMin: Int?,
    val trailerUrl: String?,
    val actors: List<Pair<String, String>>
)

object MediaSearchService {
    private const val TAG = "MediaSearchService"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Search media items by query.
     * Uses TMDB if a valid API key is present, otherwise falls back to Gemini API.
     */
    private val fallbackPosters = mapOf(
        "légion" to "https://upload.wikimedia.org/wikipedia/en/7/77/Legion_Poster.jpg",
        "le secret des marrowbone" to "https://upload.wikimedia.org/wikipedia/en/9/91/Marrowbone_%28film%29.png",
        "le prestige" to "https://upload.wikimedia.org/wikipedia/en/8/8a/The_Prestige_poster.jpg",
        "le nouveau testament" to "https://upload.wikimedia.org/wikipedia/en/2/23/The_Brand_New_Testament.jpg",
        "inception" to "https://upload.wikimedia.org/wikipedia/en/2/2e/Inception_%282010%29_theatrical_poster.jpg",
        "a la croisée des mondes" to "https://static.tvmaze.com/uploads/images/original_untouched/445/1113926.jpg",
        "a knight of the seven kingdoms" to "https://static.tvmaze.com/uploads/images/original_untouched/608/1521912.jpg",
        "american gods" to "https://static.tvmaze.com/uploads/images/original_untouched/289/722622.jpg",
        "barry" to "https://static.tvmaze.com/uploads/images/original_untouched/455/1138151.jpg",
        "black list" to "https://static.tvmaze.com/uploads/images/original_untouched/208/522372.jpg",
        "common side effects" to "https://static.tvmaze.com/uploads/images/original_untouched/553/1384654.jpg",
        "halo" to "https://static.tvmaze.com/uploads/images/original_untouched/502/1256105.jpg",
        "his dark materials" to "https://static.tvmaze.com/uploads/images/original_untouched/445/1113926.jpg",
        "la palma" to "https://static.tvmaze.com/uploads/images/original_untouched/545/1364327.jpg",
        "le prophète" to "https://static.tvmaze.com/uploads/images/original_untouched/224/562050.jpg",
        "lost" to "https://static.tvmaze.com/uploads/images/original_untouched/0/1389.jpg",
        "monarch" to "https://static.tvmaze.com/uploads/images/original_untouched/423/1059888.jpg",
        "pamela rose" to "https://static.tvmaze.com/uploads/images/original_untouched/487/1219908.jpg",
        "plaine orientale" to "https://static.tvmaze.com/uploads/images/original_untouched/567/1418683.jpg",
        "the great" to "https://static.tvmaze.com/uploads/images/original_untouched/460/1151199.jpg",
        "the librarians" to "https://static.tvmaze.com/uploads/images/original_untouched/135/338167.jpg",
        "the witcher" to "https://static.tvmaze.com/uploads/images/original_untouched/594/1486674.jpg",
        "tom clancy's jack ryan" to "https://static.tvmaze.com/uploads/images/original_untouched/486/1215704.jpg",
        "tulsa king" to "https://static.tvmaze.com/uploads/images/original_untouched/588/1471782.jpg",
        "west world" to "https://static.tvmaze.com/uploads/images/original_untouched/385/964908.jpg",
        "démon slave" to "https://upload.wikimedia.org/wikipedia/en/9/90/Chained_Soldier_manga_volume_1_cover.jpg",
        "full métal alchimiste" to "https://upload.wikimedia.org/wikipedia/en/9/9d/Fullmetal_Alchemist_Brotherhood_key_visual.png",
        "nukitashi" to "https://static.tvmaze.com/uploads/images/original_untouched/608/1521382.jpg"
    )

    suspend fun getPosterForTitle(title: String, expectedType: String? = null): String? = withContext(Dispatchers.IO) {
        val tmdbKey = BuildConfig.TMDB_API_KEY
        val hasTmdbKey = tmdbKey.isNotEmpty() && tmdbKey != "MY_TMDB_API_KEY" && tmdbKey != "placeholder"
        if (hasTmdbKey) {
            try {
                val results = searchTMDB(title, tmdbKey)
                val bestMatch = if (expectedType != null) {
                    results.firstOrNull { it.mediaType == expectedType && it.title.equals(title, ignoreCase = true) }
                        ?: results.firstOrNull { it.mediaType == expectedType }
                        ?: results.firstOrNull()
                } else {
                    results.firstOrNull { it.title.equals(title, ignoreCase = true) } ?: results.firstOrNull()
                }
                val tmdbUrl = bestMatch?.posterUrl
                if (tmdbUrl != null) return@withContext tmdbUrl
            } catch (e: Exception) {
                Log.e(TAG, "TMDB Search failed in getPosterForTitle", e)
            }
        }
        return@withContext null
    }

    suspend fun getAdditionalDetails(title: String, mediaType: String): AdditionalMediaDetails? = withContext(Dispatchers.IO) {
        val tmdbKey = BuildConfig.TMDB_API_KEY
        if (tmdbKey.isEmpty() || tmdbKey == "MY_TMDB_API_KEY" || tmdbKey == "placeholder") return@withContext null

        try {
            val encodedQuery = URLEncoder.encode(title, "UTF-8")
            val searchUrl = "https://api.themoviedb.org/3/search/multi?api_key=$tmdbKey&query=$encodedQuery&language=fr-FR"
            val searchRequest = Request.Builder().url(searchUrl).build()
            
            var tmdbId: Int? = null
            var actualMediaType = ""
            
            okHttpClient.newCall(searchRequest).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyString = response.body?.string() ?: return@withContext null
                val jsonArray = JSONObject(bodyString).optJSONArray("results") ?: return@withContext null
                
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val mType = item.optString("media_type", "")
                    if (mediaType == "FILM" && mType == "movie") {
                        tmdbId = item.optInt("id")
                        actualMediaType = "movie"
                        break
                    } else if ((mediaType == "SERIE" || mediaType == "ANIME") && mType == "tv") {
                        tmdbId = item.optInt("id")
                        actualMediaType = "tv"
                        break
                    }
                }
            }
            
            if (tmdbId == null) return@withContext null
            
            val detailsUrl = "https://api.themoviedb.org/3/$actualMediaType/$tmdbId?api_key=$tmdbKey&language=fr-FR&append_to_response=videos,credits"
            val detailsRequest = Request.Builder().url(detailsUrl).build()
            
            okHttpClient.newCall(detailsRequest).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyString = response.body?.string() ?: return@withContext null
                val json = JSONObject(bodyString)
                
                val releaseDate = if (actualMediaType == "movie") json.optString("release_date") else json.optString("first_air_date")
                val durationMin = if (actualMediaType == "movie") {
                    if (json.has("runtime") && !json.isNull("runtime")) json.optInt("runtime") else null
                } else {
                    val runtimes = json.optJSONArray("episode_run_time")
                    if (runtimes != null && runtimes.length() > 0) runtimes.optInt(0) else null
                }
                
                var trailerUrl: String? = null
                val videosResult = json.optJSONObject("videos")?.optJSONArray("results")
                if (videosResult != null) {
                    for (i in 0 until videosResult.length()) {
                        val video = videosResult.getJSONObject(i)
                        if (video.optString("site") == "YouTube" && video.optString("type") == "Trailer") {
                            trailerUrl = "https://www.youtube.com/watch?v=" + video.optString("key")
                            break
                        }
                    }
                    if (trailerUrl == null && videosResult.length() > 0) {
                         val video = videosResult.getJSONObject(0)
                         if (video.optString("site") == "YouTube") {
                             trailerUrl = "https://www.youtube.com/watch?v=" + video.optString("key")
                         }
                    }
                }
                
                val actors = mutableListOf<Pair<String, String>>()
                val castArray = json.optJSONObject("credits")?.optJSONArray("cast")
                if (castArray != null) {
                    for (i in 0 until minOf(castArray.length(), 6)) {
                        val castMember = castArray.getJSONObject(i)
                        actors.add(Pair(castMember.optString("name", ""), castMember.optString("character", "")))
                    }
                }
                
                return@withContext AdditionalMediaDetails(
                    releaseDate = releaseDate.takeIf { it.isNotBlank() },
                    durationMin = durationMin.takeIf { it != 0 },
                    trailerUrl = trailerUrl,
                    actors = actors
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get additional details", e)
            return@withContext null
        }
    }

    suspend fun search(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        val tmdbKey = BuildConfig.TMDB_API_KEY
        val hasTmdbKey = tmdbKey.isNotEmpty() && tmdbKey != "MY_TMDB_API_KEY" && tmdbKey != "placeholder"

        if (hasTmdbKey) {
            try {
                Log.d(TAG, "Attempting TMDB search for query: $query")
                return@withContext searchTMDB(query, tmdbKey)
            } catch (e: Exception) {
                Log.e(TAG, "TMDB Search failed, falling back to Gemini", e)
            }
        }

        // Default or Fallback to Gemini
        Log.d(TAG, "Using Gemini for search query: $query")
        return@withContext searchGemini(query).ifEmpty { searchTVMaze(query) }
    }

    private fun searchTMDB(query: String, apiKey: String): List<SearchResult> {
        val yearRegex = Regex("\\b(19|20)\\d{2}\\b")
        val match = yearRegex.find(query)
        val extractedYear = match?.value
        
        val cleanQuery = query.replace(yearRegex, "").replace(Regex("[()]"), "").trim()
        val queryToUse = if (cleanQuery.isNotBlank()) cleanQuery else query

        val encodedQuery = URLEncoder.encode(queryToUse, "UTF-8")
        val url = "https://api.themoviedb.org/3/search/multi?api_key=$apiKey&query=$encodedQuery&language=fr-FR"

        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("TMDB returned HTTP ${response.code}")
            val bodyString = response.body?.string() ?: return emptyList()
            
            val rawResults = mutableListOf<Pair<SearchResult, String>>()
            val jsonObject = JSONObject(bodyString)
            val jsonArray = jsonObject.optJSONArray("results") ?: return emptyList()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val mediaTypeRaw = item.optString("media_type")
                
                // We only care about movies (films) and tv shows (série / animé)
                if (mediaTypeRaw != "movie" && mediaTypeRaw != "tv") continue

                val title = item.optString("title").takeIf { it.isNotBlank() }
                    ?: item.optString("name").takeIf { it.isNotBlank() }
                    ?: item.optString("original_title").takeIf { it.isNotBlank() }
                    ?: item.optString("original_name")
                    ?: continue

                val overview = item.optString("overview", "Aucun synopsis disponible.")
                val posterPath = item.optString("poster_path").takeIf { it.isNotBlank() && it != "null" }
                val posterUrl = if (posterPath != null) "https://image.tmdb.org/t/p/w500$posterPath" else null

                val genreIds = item.optJSONArray("genre_ids")
                val genreId = if (genreIds != null && genreIds.length() > 0) genreIds.getInt(0) else 0
                val genre = getGenreName(genreId)

                // Try to infer if it is an Anime or a TV show
                val originalLanguage = item.optString("original_language")
                val isAnime = mediaTypeRaw == "tv" && (originalLanguage == "ja" || genre == "Animation")
                val mediaType = when {
                    mediaTypeRaw == "movie" -> "FILM"
                    isAnime -> "ANIME"
                    else -> "SERIE"
                }

                // Convert vote_average (0-10) directly to 1-10 rating
                val voteAverage = item.optDouble("vote_average", 6.0)
                val rating = voteAverage.coerceIn(1.0, 10.0).toInt()
                
                val releaseDate = item.optString("release_date").takeIf { it.isNotBlank() } ?: item.optString("first_air_date")
                val itemYear = if (releaseDate.isNotBlank() && releaseDate.length >= 4) releaseDate.substring(0, 4) else ""
                val titleWithYear = if (itemYear.isNotBlank()) "$title ($itemYear)" else title

                val searchResult = SearchResult(titleWithYear, overview, posterUrl, genre, mediaType, rating)
                rawResults.add(Pair(searchResult, itemYear))
            }
            
            if (extractedYear != null) {
                rawResults.sortByDescending { if (it.second == extractedYear) 1 else 0 }
            }
            return rawResults.map { it.first }
        }
    }

    private fun searchGemini(query: String): List<SearchResult> {
        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (geminiKey.isEmpty() || geminiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "No Gemini API key configured.")
            return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
        }

        // We use gemini-3.5-flash as the standard fast model
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$geminiKey"

        val prompt = """
            Recherche des films, séries ou animés correspondant à la requête suivante : "$query".
            Retourne exactement 3 à 5 résultats pertinents sous forme de tableau JSON.
            IMPORTANT : La réponse doit uniquement contenir le tableau JSON valide, sans balises markdown ```json ou ``` ni aucun autre texte explicatif.
            Chaque élément du tableau JSON doit avoir exactement cette structure :
            {
              "title": "Nom de l'œuvre (Année de sortie)",
              "overview": "Synopsis captivant et complet en français",
              "posterUrl": "URL de l'affiche TMDB réelle si connue sous la forme 'https://image.tmdb.org/t/p/w500/PATH_POSTER.jpg' ou l'URL Wikipédia de l'affiche. Renvoie null si inconnue. Ne renvoie jamais d'URL Unsplash ou d'URL inventée.",
              "genre": "Genre principal en français (ex: Drame, Action, Science-Fiction, Fantastique, Animation)",
              "mediaType": "FILM" ou "SERIE" ou "ANIME",
              "rating": Note estimée sur 10 (nombre entier entre 1 et 10)
            }
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini API error: ${response.code} - ${response.message}")
                    return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                }
                val bodyString = response.body?.string() ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                val firstCandidate = candidates.optJSONObject(0) ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                val contentObj = firstCandidate.optJSONObject("content") ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                val partsArray = contentObj.optJSONArray("parts") ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                val firstPart = partsArray.optJSONObject(0) ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
                val rawText = firstPart.optString("text") ?: return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }

                // Clean response in case the model ignored responseMimeType and returned markdown anyway
                val cleanJson = rawText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val listType = Types.newParameterizedType(List::class.java, SearchResultMoshi::class.java)
                val adapter = moshi.adapter<List<SearchResultMoshi>>(listType)
                val parsedList = adapter.fromJson(cleanJson)

                return parsedList?.map {
                    SearchResult(
                        title = it.title,
                        overview = it.overview,
                        posterUrl = it.posterUrl,
                        genre = it.genre,
                        mediaType = when (it.mediaType.uppercase()) {
                            "FILM" -> "FILM"
                            "SERIE" -> "SERIE"
                            "ANIME" -> "ANIME"
                            else -> "FILM"
                        },
                        rating = it.rating.coerceIn(1, 5)
                    )
                } ?: getFallbackMockResults(query)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling or parsing Gemini response", e)
            return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }
        }
    }

    private fun getGenreName(id: Int): String {
        return when (id) {
            28 -> "Action"
            12 -> "Aventure"
            16 -> "Animation"
            35 -> "Comédie"
            80 -> "Crime"
            99 -> "Documentaire"
            18 -> "Drame"
            10751 -> "Famille"
            14 -> "Fantastique"
            36 -> "Histoire"
            27 -> "Horreur"
            10402 -> "Musique"
            9648 -> "Mystère"
            10749 -> "Romance"
            878 -> "Science-Fiction"
            10770 -> "Téléfilm"
            53 -> "Thriller"
            10752 -> "Guerre"
            37 -> "Western"
            10759 -> "Action & Aventure"
            10762 -> "Enfants"
            10765 -> "Sci-Fi & Fantasy"
            10768 -> "Guerre & Politique"
            else -> "Autre"
        }
    }

    /**
     * Local offline mock generator when keys are completely unavailable or calls fail.
     */
    private fun searchTVMaze(query: String): List<SearchResult> {
        return try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "https://api.tvmaze.com/search/shows?q=$encodedQuery"
            val request = okhttp3.Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()
                
                val results = mutableListOf<SearchResult>()
                val jsonArray = org.json.JSONArray(bodyString)
                for (i in 0 until jsonArray.length()) {
                    val itemWrapper = jsonArray.getJSONObject(i)
                    val item = itemWrapper.optJSONObject("show") ?: continue
                    val title = item.optString("name").takeIf { it.isNotBlank() } ?: continue
                    var overview = item.optString("summary", "Aucun synopsis disponible.")
                    overview = overview.replace(Regex("<[^>]*>"), "") // Remove HTML tags
                    val imageObj = item.optJSONObject("image")
                    val posterUrl: String? = null // Ignored to avoid bad images
                    val genresArray = item.optJSONArray("genres")
                    val genres = mutableListOf<String>()
                    if (genresArray != null) {
                        for (j in 0 until genresArray.length()) {
                            genres.add(genresArray.getString(j))
                        }
                    }
                    val genreStr = if (genres.isNotEmpty()) genres.joinToString(" / ") else "Série"
                    val ratingObj = item.optJSONObject("rating")
                    val average = ratingObj?.optDouble("average")
                    val rating = if (average != null && !average.isNaN()) average.toInt() else 7
                    results.add(
                        SearchResult(
                            title = title,
                            overview = overview,
                            posterUrl = if (!posterUrl.isNullOrBlank() && posterUrl != "null") posterUrl else null,
                            genre = genreStr,
                            mediaType = "SERIE",
                            rating = rating
                        )
                    )
                }
                results
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    private fun searchWikipediaPoster(title: String, mediaType: String): String? {
        return try {
            val query = if (mediaType == "FILM") "$title film" else "$title series"
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrsearch=$encodedQuery&gsrlimit=1&prop=pageimages&format=json&pithumbsize=500"
            val request = okhttp3.Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val bodyString = response.body?.string() ?: return null
                val jsonObject = org.json.JSONObject(bodyString)
                val queryObj = jsonObject.optJSONObject("query") ?: return null
                val pagesObj = queryObj.optJSONObject("pages") ?: return null
                val keys = pagesObj.keys()
                if (keys.hasNext()) {
                    val firstPage = pagesObj.getJSONObject(keys.next())
                    val thumbnail = firstPage.optJSONObject("thumbnail")
                    return thumbnail?.optString("source")
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    private fun getFallbackMockResults(query: String): List<SearchResult> {
        val q = query.lowercase()
        return listOf(
            SearchResult(
                title = "Inception",
                overview = "Un voleur expérimenté dans l'art de l'extraction, qui s'approprie les secrets les plus précieux d'un individu pendant qu'il rêve.",
                posterUrl = "https://image.tmdb.org/t/p/w500/8066f1bfd8ea4f9ebf5223078a16db32.jpg",
                genre = "Science-Fiction",
                mediaType = "FILM",
                rating = 9
            ),
            SearchResult(
                title = "Breaking Bad",
                overview = "Un professeur de chimie de lycée atteint d'un cancer du poumon phase terminale se lance dans la fabrication de méthamphétamine avec un ancien élève.",
                posterUrl = null,
                genre = "Drame",
                mediaType = "SERIE",
                rating = 10
            ),
            SearchResult(
                title = "Death Note",
                overview = "Light Yagami ramasse un mystérieux carnet intitulé 'Death Note'.",
                posterUrl = null,
                genre = "Animation",
                mediaType = "ANIME",
                rating = 9
            )
        )
    }

    suspend fun detectDetails(title: String): SearchResult? = withContext(Dispatchers.IO) {
        if (title.isBlank()) return@withContext null

        val tmdbKey = BuildConfig.TMDB_API_KEY
        val hasTmdbKey = tmdbKey.isNotEmpty() && tmdbKey != "MY_TMDB_API_KEY" && tmdbKey != "placeholder"
        if (hasTmdbKey) {
            try {
                val tmdbResults = searchTMDB(title, tmdbKey)
                if (tmdbResults.isNotEmpty()) {
                    return@withContext tmdbResults.first()
                }
            } catch (e: Exception) {
                Log.e(TAG, "TMDB Search failed in detectDetails, falling back to Gemini", e)
            }
        }

        val geminiKey = BuildConfig.GEMINI_API_KEY
        if (geminiKey.isEmpty() || geminiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "No Gemini API key configured.")
            return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$geminiKey"

        val prompt = """
            Analyse et détecte les détails réels de l'œuvre suivante à partir de son titre : "$title".
            Retourne uniquement un unique objet JSON valide (pas un tableau) représentant cette œuvre avec exactement ces champs en français :
            {
              "title": "Le titre officiel correct de l'œuvre (Année de sortie)",
              "overview": "Un synopsis détaillé, complet et captivant en français de plusieurs phrases",
              "posterUrl": "URL de l'affiche TMDB réelle si connue sous la forme 'https://image.tmdb.org/t/p/w500/PATH_POSTER.jpg' ou l'URL Wikipédia de l'affiche. Renvoie null si inconnue. Ne renvoie jamais d'URL Unsplash ou d'URL inventée.",
              "genre": "Le ou les genres principaux séparés par des slashs (ex: Drame, Action, Science-Fiction / Thriller, Animation / Fantastique)",
              "mediaType": "FILM" ou "SERIE" ou "ANIME",
              "rating": Note estimée sur 10 (nombre entier entre 1 et 10)
            }
            IMPORTANT : La réponse doit uniquement contenir l'objet JSON valide, sans balises markdown ```json ou ``` ni aucun autre texte explicatif ou fioriture.
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini API error in detectDetails: ${response.code} - ${response.message}")
                    return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                }
                val bodyString = response.body?.string() ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                val firstCandidate = candidates.optJSONObject(0) ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                val contentObj = firstCandidate.optJSONObject("content") ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                val partsArray = contentObj.optJSONArray("parts") ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                val firstPart = partsArray.optJSONObject(0) ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
                val rawText = firstPart.optString("text") ?: return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)

                val cleanJson = rawText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val adapter = moshi.adapter(SearchResultMoshi::class.java)
                val parsed = adapter.fromJson(cleanJson)

                parsed?.let {
                    SearchResult(
                        title = it.title,
                        overview = it.overview,
                        posterUrl = it.posterUrl,
                        genre = it.genre,
                        mediaType = when (it.mediaType.uppercase()) {
                            "FILM" -> "FILM"
                            "SERIE" -> "SERIE"
                            "ANIME" -> "ANIME"
                            else -> "FILM"
                        },
                        rating = it.rating.coerceIn(1, 10)
                    )
                } ?: getLocalFallbackDetect(title)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling or parsing Gemini in detectDetails", e)
            return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)
        }
    }

    private fun getLocalFallbackDetect(title: String): SearchResult {
        val tLower = title.lowercase().trim()
        return when {
            tLower.contains("inception") -> SearchResult(
                title = "Inception",
                overview = "Dans un monde où la technologie permet d'entrer dans les rêves d'autrui, Dom Cobb est un voleur expérimenté, le meilleur dans l'art dangereux de l'extraction : s'approprier les secrets les plus précieux d'un individu pendant qu'il rêve. Son talent fait de lui un criminel recherché, mais lui offre aussi une chance de rédemption s'il parvient à réaliser une tâche impossible : l'inception, qui consiste à implanter une idée dans l'esprit d'un sujet.",
                posterUrl = "https://image.tmdb.org/t/p/w500/8066f1bfd8ea4f9ebf5223078a16db32.jpg",
                genre = "Science-Fiction / Thriller",
                mediaType = "FILM",
                rating = 9
            )
            tLower.contains("le jour d'après") || tLower.contains("le jour d apres") || tLower.contains("the day after tomorrow") -> SearchResult(
                title = "Le Jour d'après",
                overview = "Le climatologue Jack Hall avait prédit l'évènement, mais pas si tôt. Un dérèglement climatique brutal plonge la planète dans une nouvelle ère glaciaire. Alors que des tornades détruisent Los Angeles et qu'énormes tsunamis engloutissent New York, Jack se lance dans une course contre la montre pour sauver son fils, bloqué au cœur du gel mortel dans la bibliothèque de Manhattan.",
                posterUrl = "https://image.tmdb.org/t/p/w500/1Jj7Frjjbewb6Q6dl6YXhL3kuvL.jpg",
                genre = "Science-Fiction / Action / Catastrophe",
                mediaType = "FILM",
                rating = 8
            )
            tLower.contains("breaking bad") -> SearchResult(
                title = "Breaking Bad",
                overview = "Walter White est un professeur de chimie surqualifié dans un lycée d'Albuquerque, au Nouveau-Mexique. Sa vie bascule lorsqu'il apprend qu'il est atteint d'un cancer du poumon en phase terminale. Soucieux de mettre sa famille à l'abri du besoin après sa mort, il décide de s'associer avec un de ses anciens élèves, Jesse Pinkman, pour fabriquer et vendre de la méthamphétamine de pureté exceptionnelle.",
                posterUrl = null,
                genre = "Drame / Crime",
                mediaType = "SERIE",
                rating = 10
            )
            tLower.contains("death note") -> SearchResult(
                title = "Death Note",
                overview = "Light Yagami est un lycéen brillant qui considère le monde actuel comme corrompu et ennuyeux. Sa vie change radicalement le jour où il ramasse un mystérieux carnet intitulé 'Death Note'. Les instructions de ce carnet stipulent que toute personne dont le nom y est écrit meurt. Light décide d'utiliser ce pouvoir pour éliminer tous les criminels et créer un monde utopique débarrassé du mal, dont il serait le dieu.",
                posterUrl = null,
                genre = "Animation / Thriller / Fantastique",
                mediaType = "ANIME",
                rating = 9
            )
            else -> SearchResult(
                title = title,
                overview = "Synopsis généré automatiquement pour l'œuvre intitulée '$title'. C'est une œuvre fascinante explorant des thématiques universelles avec une mise en scène soignée et des personnages captivants.",
                posterUrl = null,
                genre = "Drame / Action",
                mediaType = "FILM",
                rating = 8
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class SearchResultMoshi(
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val genre: String,
    val mediaType: String,
    val rating: Int
)
