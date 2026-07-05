package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val genre: String,
    val mediaType: String, // "FILM", "SERIE", "ANIME"
    val rating: Int, // 1 to 5 stars
    val customNotes: String = "",
    val isWatched: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
