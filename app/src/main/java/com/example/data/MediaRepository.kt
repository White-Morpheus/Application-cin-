package com.example.data

import kotlinx.coroutines.flow.Flow

class MediaRepository(private val mediaDao: MediaDao) {
    val allMediaItems: Flow<List<MediaItem>> = mediaDao.getAllMediaItems()

    suspend fun getAllItemsList(): List<MediaItem> {
        return mediaDao.getMediaItemsList()
    }

    suspend fun insert(mediaItem: MediaItem): Long {
        return mediaDao.insertMediaItem(mediaItem)
    }

    suspend fun update(mediaItem: MediaItem) {
        mediaDao.updateMediaItem(mediaItem)
    }

    suspend fun delete(mediaItem: MediaItem) {
        mediaDao.deleteMediaItem(mediaItem)
    }

    suspend fun deleteById(id: Int) {
        mediaDao.deleteMediaItemById(id)
    }
}
