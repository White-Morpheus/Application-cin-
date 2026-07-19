package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.ui.layout.ContentScale
import com.example.ui.MediaViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var title by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf("FILM") }
    var genre by remember { mutableStateOf("") }
    var overview by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(8) }
    var customNotes by remember { mutableStateOf("") }
    var isWatched by remember { mutableStateOf(false) }
    var posterUrl by remember { mutableStateOf<String?>(null) }
    
    var isDetecting by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    
    var suggestions by remember { mutableStateOf<List<com.example.data.SearchResult>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }
    var isTitleSelectedFromSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(title) {
        if (isTitleSelectedFromSuggestions) {
            isTitleSelectedFromSuggestions = false
            return@LaunchedEffect
        }
        if (title.length > 2) {
            delay(800)
            isDetecting = true
            try {
                val results = viewModel.searchMediaSuggestions(title)
                suggestions = results
                showSuggestions = results.isNotEmpty()
            } catch (e: Exception) {
                // Ignore
            } finally {
                isDetecting = false
            }
        } else {
            showSuggestions = false
            suggestions = emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ajouter manuellement",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5C518)
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre de l'œuvre *") },
            modifier = Modifier.fillMaxWidth().testTag("add_title_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            trailingIcon = {
                if (isDetecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFF5C518),
                        strokeWidth = 2.dp
                    )
                }
            }
        )
        
        val displaySuggestions = remember(suggestions, mediaType, genre) {
            suggestions.filter { result ->
                val typeMatch = result.mediaType.equals(mediaType, ignoreCase = true)
                val genreMatch = if (genre.isNotBlank()) {
                    result.genre.contains(genre, ignoreCase = true)
                } else {
                    true
                }
                typeMatch && genreMatch
            }
        }

        androidx.compose.animation.AnimatedVisibility(visible = showSuggestions && displaySuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    displaySuggestions.forEachIndexed { index, result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isTitleSelectedFromSuggestions = true
                                    title = result.title
                                    overview = result.overview
                                    genre = result.genre
                                    mediaType = result.mediaType
                                    posterUrl = result.posterUrl
                                    showSuggestions = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = if (!result.posterUrl.isNullOrBlank() && result.posterUrl != "null") result.posterUrl else R.drawable.generic_poster,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp, 60.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = result.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${result.mediaType} • ${result.genre.take(20)}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        if (index < displaySuggestions.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
        
        val posterModel: Any = if (!posterUrl.isNullOrBlank() && posterUrl != "null") posterUrl!! else R.drawable.generic_poster
        Spacer(modifier = Modifier.height(16.dp))
        AsyncImage(
            model = posterModel,
            contentDescription = "Aperçu de l'affiche",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(16.dp))
             // Media Type
        Text(
            text = "Type de média :",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val types = listOf(
                "FILM" to "Film",
                "SERIE" to "Série",
                "ANIME" to "Animé"
            )
            types.forEach { (code, label) ->
                val isSelected = mediaType == code
                FilterChip(
                    selected = isSelected,
                    onClick = { mediaType = code },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f).testTag("add_type_$code")
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Genre
        OutlinedTextField(
            value = genre,
            onValueChange = { genre = it },
            label = { Text("Genre(s)") },
            modifier = Modifier.fillMaxWidth().testTag("add_genre_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Rating Slider (1-10)
        Text(
            text = "Votre appréciation :",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start),
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            (1..5).forEach { starIndex ->
                val ratingValue = starIndex * 2
                Icon(
                    imageVector = if (userRating >= ratingValue) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { userRating = ratingValue }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Overview (Synopsis)
        OutlinedTextField(
            value = overview,
            onValueChange = { overview = it },
            label = { Text("Synopsis développé") },
            modifier = Modifier.fillMaxWidth().testTag("add_overview_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notes
        OutlinedTextField(
            value = customNotes,
            onValueChange = { customNotes = it },
            label = { Text("Notes personnelles") },
            modifier = Modifier.fillMaxWidth().testTag("add_notes_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Watched status checkbox row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { isWatched = !isWatched }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isWatched,
                onCheckedChange = { isWatched = it },
                modifier = Modifier.testTag("add_is_watched_checkbox"),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Déjà vu (film/série visionné)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // Dialog Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { 
                    title = ""
                    mediaType = "FILM"
                    genre = ""
                    overview = ""
                    userRating = 8
                    customNotes = ""
                    isWatched = false
                    posterUrl = null
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Annuler")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        isAdding = true
                        coroutineScope.launch {
                            try {
                                viewModel.addMediaItem(
                                    title = title,
                                    overview = overview,
                                    posterUrl = posterUrl,
                                    genre = genre,
                                    mediaType = mediaType,
                                    rating = userRating,
                                    customNotes = customNotes,
                                    isWatched = isWatched
                                )
                                Toast.makeText(context, "'${title}' ajouté à la collection !", Toast.LENGTH_SHORT).show()
                                title = ""
                                mediaType = "FILM"
                                genre = ""
                                overview = ""
                                userRating = 8
                                customNotes = ""
                                isWatched = false
                                posterUrl = null
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                            } finally {
                                isAdding = false
                            }
                        }
                    }
                },
                enabled = title.isNotBlank() && !isAdding,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("add_save_button")
            ) {
                if (isAdding) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Ajouter", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
