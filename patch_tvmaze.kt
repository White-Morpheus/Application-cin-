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
                    val posterUrl = imageObj?.optString("original") ?: imageObj?.optString("medium")
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
