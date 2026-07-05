package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MediaItem
import com.example.data.MediaRepository
import com.example.data.MediaSearchService
import com.example.data.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MediaRepository(database.mediaDao())
    private val attemptedPosterResolutions = java.util.Collections.synchronizedSet(mutableSetOf<Int>())

    // List of saved media items
    val allMediaItems: StateFlow<List<MediaItem>> = repository.allMediaItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Pre-populate database with CineTrackCatalog items if not already done
        val sharedPrefs = application.getSharedPreferences("cinetrack_prefs", android.content.Context.MODE_PRIVATE)
        val isFullCatalogImported = sharedPrefs.getBoolean("full_catalog_imported_v3", false)
        
        viewModelScope.launch {
            val isPurged = sharedPrefs.getBoolean("bad_posters_purged_v1", false)
            if (!isPurged) {
                try {
                    val existingItems = repository.getAllItemsList()
                    existingItems.forEach { item ->
                        if (item.posterUrl != null && !item.posterUrl!!.startsWith("https://image.tmdb.org/")) {
                            repository.update(item.copy(posterUrl = null))
                        }
                    }
                    sharedPrefs.edit().putBoolean("bad_posters_purged_v1", true).apply()
                } catch (e: Exception) {
                    android.util.Log.e("MediaViewModel", "Error purging bad posters", e)
                }
            }
        }

        if (!isFullCatalogImported) {
            viewModelScope.launch {
                try {
                    val existingItems = repository.getAllItemsList()
                    val existingTitles = existingItems.map { it.title.trim().lowercase() }.toSet()

                    com.example.data.CineTrackCatalog.items.forEach { catalogItem ->
                        val catalogTitleNormalized = catalogItem.title.trim().lowercase()
                        if (!existingTitles.contains(catalogTitleNormalized)) {
                            val mediaItem = MediaItem(
                                title = catalogItem.title,
                                overview = catalogItem.overview,
                                posterUrl = MediaSearchService.getPosterForTitle(catalogItem.title, catalogItem.mediaType),
                                genre = catalogItem.genre,
                                mediaType = catalogItem.mediaType,
                                rating = catalogItem.rating * 2, // Scale 1-5 catalog rating to 1-10 rating scale
                                customNotes = "Importé du catalogue de base",
                                isWatched = false
                            )
                            repository.insert(mediaItem)
                        }
                    }
                    sharedPrefs.edit().putBoolean("full_catalog_imported_v3", true).apply()
                } catch (e: Exception) {
                    android.util.Log.e("MediaViewModel", "Error pre-populating database", e)
                }
            }
        }

        // Auto-fetch missing posters for items in the collection sequentially to respect API rate limits
        viewModelScope.launch {
            allMediaItems.collect { items ->
                // Filter items that have fallback posters or no posters and haven't been attempted yet
                val pendingItems = items.filter { 
                    (it.posterUrl.isNullOrBlank() || it.posterUrl == "null" || it.posterUrl!!.startsWith("https://images.unsplash.com/") || it.posterUrl!!.contains("gemini")) &&
                    !attemptedPosterResolutions.contains(it.id)
                }
                
                for (item in pendingItems) {
                    attemptedPosterResolutions.add(item.id)
                    try {
                        // Search TMDB directly for official poster
                        var posterToUse = MediaSearchService.getPosterForTitle(item.title, item.mediaType)

                        if (posterToUse.isNullOrBlank()) {
                            val results = MediaSearchService.search(item.title)
                            val match = results.firstOrNull { it.mediaType == item.mediaType && it.title.equals(item.title, ignoreCase = true) } 
                                ?: results.firstOrNull { it.title.equals(item.title, ignoreCase = true) } 
                                ?: results.firstOrNull()
                            posterToUse = match?.posterUrl
                        }

                        if (posterToUse.isNullOrBlank()) {
                            posterToUse = MediaSearchService.detectDetails(item.title)?.posterUrl
                        }

                        if (posterToUse != null && !posterToUse.startsWith("https://image.tmdb.org/")) {
                            posterToUse = null
                        }

                        if (posterToUse != null && posterToUse.isNotBlank() && posterToUse != item.posterUrl) {
                            repository.update(item.copy(posterUrl = posterToUse))
                            // Respectful delay to avoid API rate limiting
                            kotlinx.coroutines.delay(1000)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MediaViewModel", "Error auto-fetching official poster for ${item.title}", e)
                    }
                }
            }
        }
    }

    // Filtering & Sorting
    private val _isDarkMode = MutableStateFlow(
        application.getSharedPreferences("cinetrack_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("is_dark_mode", true)
    )
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        getApplication<Application>()
            .getSharedPreferences("cinetrack_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putBoolean("is_dark_mode", newValue)
            .apply()
    }

    private val _filterType = MutableStateFlow("ALL") // "ALL", "FILM", "SERIE", "ANIME"
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _filterGenre = MutableStateFlow("ALL") // "ALL" or specific genre name
    val filterGenre: StateFlow<String> = _filterGenre.asStateFlow()

    private val _sortBy = MutableStateFlow("DATE_ADDED") // "DATE_ADDED", "ALPHABETICAL", "RATING"
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    // Expose dynamically computed available genres from saved items
    val availableGenres: StateFlow<List<String>> = allMediaItems.map { items ->
        items.flatMap { item ->
            item.genre.split("/").map { it.trim() }
        }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered and Sorted list for UI consumption
    val filteredMediaItems: StateFlow<List<MediaItem>> = combine(
        allMediaItems, _filterType, _filterGenre, _sortBy
    ) { items, filterTypeVal, filterGenreVal, sort ->
        var result = items

        // Filter by Type
        if (filterTypeVal != "ALL") {
            result = result.filter { it.mediaType.equals(filterTypeVal, ignoreCase = true) }
        }

        // Filter by Genre
        if (filterGenreVal != "ALL") {
            result = result.filter { item ->
                item.genre.split("/").map { it.trim().lowercase() }.contains(filterGenreVal.lowercase())
            }
        }

        // Sort
        result = when (sort) {
            "ALPHABETICAL" -> result.sortedBy { it.title.lowercase() }
            "RATING" -> result.sortedByDescending { it.rating }
            else -> result.sortedByDescending { it.addedAt } // DATE_ADDED
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(filter: String) {
        _filterType.value = filter
    }

    fun setFilterGenre(genre: String) {
        _filterGenre.value = genre
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    // Search action
    fun performSearch() {
        val query = _searchQuery.value
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            try {
                val results = MediaSearchService.search(query)
                _searchResults.value = results
                if (results.isEmpty()) {
                    _searchError.value = "Aucun résultat trouvé pour '$query'"
                }
            } catch (e: Exception) {
                _searchError.value = "Erreur de recherche : ${e.localizedMessage}"
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    suspend fun searchMediaSuggestions(title: String): List<SearchResult> {
        if (title.isBlank()) return emptyList()
        return MediaSearchService.search(title)
    }

    suspend fun autoDetectMediaDetails(title: String): SearchResult? {
        if (title.isBlank()) return null
        return MediaSearchService.detectDetails(title)
    }

    // Database operations
    fun addMediaItem(
        title: String,
        overview: String,
        posterUrl: String?,
        genre: String,
        mediaType: String,
        rating: Int,
        customNotes: String = "",
        isWatched: Boolean = false
    ) {
        viewModelScope.launch {
            val item = MediaItem(
                title = title,
                overview = overview,
                posterUrl = posterUrl,
                genre = genre,
                mediaType = mediaType,
                rating = rating,
                customNotes = customNotes,
                isWatched = isWatched
            )
            repository.insert(item)
        }
    }

    fun updateMediaItem(item: MediaItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun deleteMediaItem(item: MediaItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun deleteMediaItemById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun deleteMediaItemByTitle(title: String) {
        viewModelScope.launch {
            val itemToDelete = allMediaItems.value.find { it.title.equals(title, ignoreCase = true) }
            if (itemToDelete != null) {
                repository.delete(itemToDelete)
            }
        }
    }

    // Factory for simple instantiation in Compose
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MediaViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
