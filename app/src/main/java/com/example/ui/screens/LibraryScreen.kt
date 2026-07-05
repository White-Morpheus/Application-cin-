package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.MediaItem
import com.example.ui.MediaViewModel
import com.example.data.CineTrackCatalog
import com.example.data.CatalogItem
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.BorderStroke
import kotlinx.coroutines.launch
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val savedItems by viewModel.filteredMediaItems.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val filterGenre by viewModel.filterGenre.collectAsState()
    val availableGenres by viewModel.availableGenres.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val filteredSavedItems = remember(savedItems, searchQuery) {
        if (searchQuery.isBlank()) {
            savedItems
        } else {
            savedItems.filter { item ->
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.genre.contains(searchQuery, ignoreCase = true) ||
                (item.customNotes ?: "").contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val FAKE_ID = -1
    
    var showEditDialogFor by remember { mutableStateOf<MediaItem?>(null) }
    var selectedItemForDetail by remember { mutableStateOf<MediaItem?>(null) }
    val currentDetailItem = selectedItemForDetail?.let { current ->
        if (current.id == FAKE_ID) {
            current
        } else {
            savedItems.find { it.id == current.id } ?: current
        }
    }
    var showSortMenu by remember { mutableStateOf(false) }

    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()

    var randomSuggestion by remember { mutableStateOf<CatalogItem?>(null) }
    LaunchedEffect(Unit) {
        randomSuggestion = CineTrackCatalog.items.randomOrNull()
    }

    LaunchedEffect(sortBy, filterType) {
        if (filteredSavedItems.isNotEmpty()) {
            gridState.scrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and Sorting Tabs Row mimicking the screenshot exactly
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (filterType) {
                        "ALL" -> "Ma Collection"
                        "FILM" -> "Films"
                        "SERIE" -> "Séries"
                        "ANIME" -> "Animés"
                        else -> "Ma Collection"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                // Dark/Light Mode Switch Icon Button
                val isDarkMode by viewModel.isDarkMode.collectAsState()
                IconButton(
                    onClick = { viewModel.toggleDarkMode() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Changer le thème",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Horizontal sort choices: Nouveautés, A-Z, Par note
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sortChoices = listOf(
                    "DATE_ADDED" to "Nouveautés",
                    "ALPHABETICAL" to "A-Z",
                    "RATING" to "Par note"
                )
                sortChoices.forEach { (sortKey, label) ->
                    val isSelected = sortBy == sortKey
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .clickable { viewModel.setSortBy(sortKey) }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filters and Search Row
        var showFilters by remember { mutableStateOf(true) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant "Filtrer" rounded pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .clickable { showFilters = !showFilters }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtres",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Filtrer",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Media Type Filters Row
            AnimatedVisibility(visible = showFilters, modifier = Modifier.weight(1f)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        "ALL" to "Tout",
                        "FILM" to "Films",
                        "SERIE" to "Séries",
                        "ANIME" to "Animés"
                    )
                    items(filters) { (type, label) ->
                        val selected = filterType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                                .clickable { viewModel.setFilterType(type) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Genre Filters Row - Dropdown Menu style for complete overview
        AnimatedVisibility(visible = showFilters) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Genre",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    )

                    var expanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Surface(
                            onClick = { expanded = true },
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (filterGenre == "ALL") "Tous les genres" else filterGenre,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Changer de genre",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (filterGenre == "ALL") {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Sélectionné",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                        Text("Tous les genres", style = MaterialTheme.typography.bodyMedium)
                                    }
                                },
                                onClick = {
                                    viewModel.setFilterGenre("ALL")
                                    expanded = false
                                }
                            )

                            availableGenres.forEach { genre ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (filterGenre == genre) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Sélectionné",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.width(16.dp))
                                            }
                                            Text(genre, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setFilterGenre(genre)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar for usability
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher dans ma collection...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Effacer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag("library_search_input"),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Random Suggestion Banner
        AnimatedVisibility(visible = searchQuery.isBlank() && randomSuggestion != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable {
                        randomSuggestion?.let { suggestion ->
                            val existing = savedItems.find { it.title == suggestion.title && it.mediaType == suggestion.mediaType }
                            if (existing != null) {
                                selectedItemForDetail = existing
                            } else {
                                selectedItemForDetail = MediaItem(
                                    id = FAKE_ID,
                                    title = suggestion.title,
                                    overview = suggestion.overview,
                                    posterUrl = null,
                                    genre = suggestion.genre,
                                    mediaType = suggestion.mediaType,
                                    rating = suggestion.rating,
                                    customNotes = "Suggestion aléatoire",
                                    isWatched = false
                                )
                            }
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Suggestion",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Suggestion aléatoire",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = randomSuggestion?.title ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${randomSuggestion?.mediaType ?: ""} • ${randomSuggestion?.genre ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { randomSuggestion = CineTrackCatalog.items.randomOrNull() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Autre suggestion",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Saved Items List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (savedItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Votre liste est vide !",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ajoutez vos œuvres depuis l'onglet Ajout manuel.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else if (filteredSavedItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aucun résultat trouvé !",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucun élément de votre liste ne correspond à \"$searchQuery\".",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(filteredSavedItems, key = { it.id }) { item ->
                        CinematicGridItemCard(
                            item = item,
                            onClick = { selectedItemForDetail = item },
                            onWatchedToggle = {
                                viewModel.updateMediaItem(item.copy(isWatched = !item.isWatched))
                            }
                        )
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    }

    // Edit Media Dialog
    if (showEditDialogFor != null) {
        val item = showEditDialogFor!!
        EditMediaDialog(
            item = item,
            onDismiss = { showEditDialogFor = null },
            onSave = { updatedItem ->
                viewModel.updateMediaItem(updatedItem)
                Toast.makeText(context, "'${item.title}' mis à jour !", Toast.LENGTH_SHORT).show()
                showEditDialogFor = null
            },
            onDelete = {
                viewModel.deleteMediaItem(item)
                Toast.makeText(context, "'${item.title}' retiré de la liste !", Toast.LENGTH_SHORT).show()
                showEditDialogFor = null
                selectedItemForDetail = null
            }
        )
    }

    // Cinematic Detail Screen Overlay (Left Screen of Screenshot)
    if (selectedItemForDetail != null && currentDetailItem != null) {
        CinematicDetailScreen(
            item = currentDetailItem,
            onDismiss = { selectedItemForDetail = null },
            onWatchedToggle = {
                if (currentDetailItem.id != FAKE_ID) {
                    viewModel.updateMediaItem(currentDetailItem.copy(isWatched = !currentDetailItem.isWatched))
                }
            },
            onEditClick = {
                if (currentDetailItem.id != FAKE_ID) {
                    showEditDialogFor = currentDetailItem
                }
            },
            onDeleteClick = {
                if (currentDetailItem.id != FAKE_ID) {
                    viewModel.deleteMediaItem(currentDetailItem)
                    selectedItemForDetail = null
                    Toast.makeText(context, "'${currentDetailItem.title}' retiré de la liste !", Toast.LENGTH_SHORT).show()
                }
            },
            onAddClick = if (currentDetailItem.id == FAKE_ID) {
                {
                    viewModel.addMediaItem(
                        title = currentDetailItem.title,
                        overview = currentDetailItem.overview,
                        posterUrl = currentDetailItem.posterUrl,
                        genre = currentDetailItem.genre,
                        mediaType = currentDetailItem.mediaType,
                        rating = currentDetailItem.rating,
                        customNotes = "Suggestion aléatoire",
                        isWatched = false
                    )
                    Toast.makeText(context, "'${currentDetailItem.title}' ajouté à la collection !", Toast.LENGTH_SHORT).show()
                    selectedItemForDetail = null
                    randomSuggestion = CineTrackCatalog.items.randomOrNull()
                }
            } else null
        )
    }
}

@Composable
fun CinematicGridItemCard(
    item: MediaItem,
    onClick: () -> Unit,
    onWatchedToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("media_grid_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column {
            // Poster portion with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f) // Standard movie poster ratio
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val posterModel: Any = if (!item.posterUrl.isNullOrBlank() && item.posterUrl != "null") item.posterUrl!! else R.drawable.generic_poster
                AsyncImage(
                    model = posterModel,
                    contentDescription = "Affiche de ${item.title}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay from bottom to top for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Top-Left: Rating Badge with exact style from user's screen (green background, white text)
                val ratingValue = if (item.rating <= 5) item.rating * 2 else item.rating
                val ratingColor = when {
                    ratingValue >= 7 -> Color(0xFF2E7D32) // Nice green
                    ratingValue >= 5 -> Color(0xFFE65100) // Amber/Orange
                    else -> Color(0xFFC62828) // Red
                }
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ratingColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = String.format("%.1f", ratingValue.toFloat()), // Map 1-5 stars to standard 1-10 rating like in screenshot (e.g. 8.2)
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Top-Right: Bookmark Icon (Translucent container, Bookmark fill/outline)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { onWatchedToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isWatched) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Status vu/à voir",
                        tint = if (item.isWatched) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Bottom Overlay: Media Type badge (Film / Série)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart)
                ) {
                    val badgeColor = when (item.mediaType.uppercase()) {
                        "FILM" -> Color(0xFFF5C518)
                        "SERIE" -> Color(0xFF00E5FF)
                        "ANIME" -> Color(0xFFFF4081)
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = item.mediaType.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                fontSize = 8.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title underneath poster
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Subtitle: Genre
            Text(
                text = item.genre,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun EditMediaDialog(
    item: MediaItem,
    onDismiss: () -> Unit,
    onSave: (MediaItem) -> Unit,
    onDelete: () -> Unit
) {
    var userRating by remember { mutableStateOf(item.rating) }
    var userNotes by remember { mutableStateOf(item.customNotes) }
    var userOverview by remember { mutableStateOf(item.overview) }
    var userIsWatched by remember { mutableStateOf(item.isWatched) }
    var userPosterUrl by remember { mutableStateOf(item.posterUrl) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!showDeleteConfirm) {
                    Text(
                        text = "Modifier l'œuvre",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Rating Slider (1-10)
                    Text(
                        text = "Votre appréciation :",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Slider(
                            value = userRating.toFloat(),
                            onValueChange = { userRating = it.toInt() },
                            valueRange = 1f..10f,
                            steps = 8,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$userRating/10",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Overview (Synopsis)
                    OutlinedTextField(
                        value = userOverview,
                        onValueChange = { userOverview = it },
                        label = { Text("Synopsis développé") },
                        placeholder = { Text("Développez le synopsis de cette œuvre...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notes
                    OutlinedTextField(
                        value = userNotes,
                        onValueChange = { userNotes = it },
                        label = { Text("Notes personnelles") },
                        placeholder = { Text("Ajoutez un avis ou commentaire...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gestion de l'affiche
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                val hasPoster = !userPosterUrl.isNullOrBlank() && userPosterUrl != "null"
                                if (hasPoster) {
                                    AsyncImage(
                                        model = userPosterUrl,
                                        contentDescription = "Affiche miniature",
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Affiche de l'œuvre",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (hasPoster) "Affiche TMDB active" else "Aucune affiche (par défaut)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (hasPoster) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            val hasPoster = !userPosterUrl.isNullOrBlank() && userPosterUrl != "null"
                            if (hasPoster) {
                                TextButton(
                                    onClick = { userPosterUrl = null },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Retirer l'affiche",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retirer", style = MaterialTheme.typography.labelMedium)
                                }
                            } else if (!item.posterUrl.isNullOrBlank() && item.posterUrl != "null") {
                                TextButton(
                                    onClick = { userPosterUrl = item.posterUrl },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Rétablir l'affiche",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rétablir", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Watched status checkbox row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { userIsWatched = !userIsWatched }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = userIsWatched,
                            onCheckedChange = { userIsWatched = it },
                            modifier = Modifier.testTag("edit_is_watched_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Déjà vu (film/série visionné)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete Button
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                        }

                        Row {
                            TextButton(onClick = onDismiss) {
                                Text("Annuler")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onSave(item.copy(rating = userRating, customNotes = userNotes, overview = userOverview, isWatched = userIsWatched, posterUrl = userPosterUrl))
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Enregistrer", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Delete confirmation screen
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Confirmer la suppression ?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Voulez-vous vraiment retirer '${item.title}' de votre collection ? Cette action est irréversible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Annuler")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Supprimer", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CinematicDetailScreen(
    item: MediaItem,
    onDismiss: () -> Unit,
    onWatchedToggle: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddClick: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val computedRatingValue = if (item.rating <= 5) item.rating.toFloat() * 2f else item.rating.toFloat()
    
    var additionalDetails by remember { mutableStateOf<com.example.data.AdditionalMediaDetails?>(null) }
    var activeTrailerUrl by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(item.title, item.mediaType) {
        additionalDetails = com.example.data.MediaSearchService.getAdditionalDetails(item.title, item.mediaType)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F1012) // Dark graphite cinematic background matching the screenshot
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Scrollable details content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Movie backdrop image area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        val detailPoster: Any = if (!item.posterUrl.isNullOrBlank() && item.posterUrl != "null") item.posterUrl!! else R.drawable.generic_poster
                        AsyncImage(
                            model = detailPoster,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Transparent and dark gradient overlays to match visual styling
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.5f),
                                            Color.Transparent,
                                            Color(0xFF0F1012)
                                        )
                                    )
                                )
                        )
                        
                        // Play/Trailer action icon in the center
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .align(Alignment.Center)
                                .clickable {
                                    val trailer = additionalDetails?.trailerUrl
                                    if (trailer != null) {
                                        activeTrailerUrl = trailer
                                    } else {
                                        Toast.makeText(context, "Bande-annonce non disponible", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play trailer",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    
                    // Metadata & Text information
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Original work subtitle
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9EA3AE),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Main headline translated/French title
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF4F5F6)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Badges Row (IMDb Score + User Score + Rating Age tag)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Yellow IMDb rating pill matching screenshot
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF5C518))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "IMDb",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = String.format(" %.1f", computedRatingValue - 0.1f),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            
                            // User Score pill in nice green
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF2E7D32))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = String.format("%.1f", computedRatingValue),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            
                            // 18+ age restriction badge
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF9EA3AE), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "18+",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9EA3AE),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            
                            // Media Type tag
                            val badgeColor = when (item.mediaType.uppercase()) {
                                "FILM" -> Color(0xFFF5C518)
                                "SERIE" -> Color(0xFF00E5FF)
                                "ANIME" -> Color(0xFFFF4081)
                                else -> MaterialTheme.colorScheme.primary
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeColor.copy(alpha = 0.2f))
                                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.mediaType.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Solid Yellow CTA Action Button (Regarder/Lancer)
                        Button(
                            onClick = {
                                val trailer = additionalDetails?.trailerUrl
                                if (trailer != null) {
                                    activeTrailerUrl = trailer
                                } else {
                                    Toast.makeText(context, "Bande-annonce non disponible", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF5C518),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lancer la lecture",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Duration & Quality line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val durationText = additionalDetails?.durationMin?.let { "${it} min" } ?: if (item.mediaType.uppercase() == "FILM") "110 min" else "Saison 1"
                            Text(
                                text = "$durationText • HD Qualité",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9EA3AE)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Custom tabs (À propos, Critiques, Bandes-annonces)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val tabs = listOf("À propos", "Critiques", "Bandes-annonces")
                            tabs.forEachIndexed { index, title ->
                                val active = selectedTab == index
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedTab = index }
                                        .padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                            color = if (active) Color(0xFFF4F5F6) else Color(0xFF9EA3AE)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(if (active) Color(0xFFF5C518) else Color.Transparent)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Tab Contents
                        when (selectedTab) {
                            0 -> {
                                // Synopsis/Description
                                Text(
                                    text = item.overview.ifBlank { "Aucun synopsis disponible." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFD1D4DB),
                                    lineHeight = 20.sp
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                // Production Specs
                                Text(
                                    text = "Production",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF4F5F6)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val yearText = additionalDetails?.releaseDate?.take(4) ?: "2017"
                                val prodFields = listOf(
                                    "Année" to yearText,
                                    "Genre" to item.genre,
                                    "Statut" to if (item.isWatched) "Déjà visionné" else "À voir",
                                    "Notes" to (item.customNotes?.takeIf { it.isNotBlank() } ?: "Aucune note personnalisée")
                                )
                                prodFields.forEach { (label, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9EA3AE),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = value,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFF4F5F6),
                                            modifier = Modifier.weight(2f),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Actors ("Acteurs et créateurs" row from screenshot)
                                Text(
                                    text = "Acteurs et créateurs",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF4F5F6)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val defaultActors = listOf(
                                    "Frances McDormand" to "Mildred",
                                    "Woody Harrelson" to "Willoughby",
                                    "Sam Rockwell" to "Dixon",
                                    "Peter Dinklage" to "James",
                                    "John Hawkes" to "Charlie"
                                )
                                val actors = additionalDetails?.actors?.takeIf { it.isNotEmpty() } ?: defaultActors
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(actors) { (name, role) ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.width(80.dp)
                                        ) {
                                            val initials = name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2)
                                            Box(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(
                                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                                            colors = listOf(Color(0xFF3A3D46), Color(0xFF1E2024))
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = initials,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = Color(0xFFF5C518)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = name,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFF4F5F6),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Text(
                                                text = role,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF9EA3AE),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Awards and detailed progress breakdown from screenshot
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF181A1E)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = Color(0xFFF5C518),
                                                modifier = Modifier.size(44.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "CineTrack Awards",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFF5C518),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                            Text(
                                                text = "Recommandé",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF9EA3AE),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                        
                                        // Detail category bars matching the screenshot's design (e.g. 8.2 breakdown)
                                        Column(
                                            modifier = Modifier.weight(2f),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val subscores = listOf(
                                                "Réalisation" to 8.5f,
                                                "Scénario" to 9.0f,
                                                "Visuels" to 8.8f,
                                                "Distribution" to 8.2f
                                            )
                                            subscores.forEach { (title, score) ->
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = title,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = Color(0xFF9EA3AE)
                                                        )
                                                        Text(
                                                            text = String.format("%.1f/10", score),
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = Color(0xFFF4F5F6)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    LinearProgressIndicator(
                                                        progress = { score / 10f },
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(4.dp)
                                                            .clip(RoundedCornerShape(2.dp)),
                                                        color = Color(0xFFF5C518),
                                                        trackColor = Color(0xFF3A3D46)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                // Reviews Tab
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF181A1E)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(Color(0xFFF5C518)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("U", color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text("Votre avis", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFF4F5F6))
                                                Text("Note attribuée : $computedRatingValue/10", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF5C518))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = item.customNotes?.ifBlank { "Aucune critique écrite pour le moment. Cliquez sur le stylo en haut à droite pour ajouter vos notes !" }
                                                ?: "Aucune critique écrite pour le moment. Cliquez sur le stylo en haut à droite pour ajouter vos notes !",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFD1D4DB)
                                        )
                                    }
                                }
                            }
                            2 -> {
                                // Trailers Tab
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF181A1E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFF5C518), modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Bande-annonce officielle (1080p)", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFF4F5F6))
                                        Text("2 min 15 s", style = MaterialTheme.typography.labelSmall, color = Color(0xFF9EA3AE))
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(100.dp))
                }
                
                // Translucent Overlay Action Bar at Top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                    
                    // Floating Actions: Watchlist/Bookmark, Edit, Delete
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Watchlist / Bookmark Status
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { onWatchedToggle() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.isWatched) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = if (item.isWatched) Color(0xFFF5C518) else Color.White
                            )
                        }
                        
                        if (onAddClick != null) {
                            // Add button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFFF5C518))
                                    .clickable { onAddClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ajouter à la collection",
                                    tint = Color.Black
                                )
                            }
                        } else {
                            // Edit button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable { onEditClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Modifier",
                                    tint = Color.White
                                )
                            }
                            
                            // Delete button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable { onDeleteClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = Color(0xFFE53935)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (activeTrailerUrl != null) {
        InAppTrailerPlayer(
            url = activeTrailerUrl!!,
            onDismiss = { activeTrailerUrl = null }
        )
    }
}

@Composable
fun InAppTrailerPlayer(
    url: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val embedUrl = remember(url) {
        if (url.contains("youtube.com/watch?v=")) {
            val videoId = url.substringAfter("watch?v=").substringBefore("&")
            "https://www.youtube.com/embed/$videoId?autoplay=1&fs=1"
        } else if (url.contains("youtu.be/")) {
            val videoId = url.substringAfter("youtu.be/").substringBefore("?")
            "https://www.youtube.com/embed/$videoId?autoplay=1&fs=1"
        } else {
            url
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                var isLoading by remember { mutableStateOf(true) }

                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }
                            }
                            webChromeClient = WebChromeClient()
                            settings.apply {
                                javaScriptEnabled = true
                                mediaPlaybackRequiresUserGesture = false
                                domStorageEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                            }
                            loadUrl(embedUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFF5C518)
                    )
                }

                // Close Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(44.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
