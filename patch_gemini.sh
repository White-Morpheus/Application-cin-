#!/bin/bash
sed -i 's/return getFallbackMockResults(query)/return searchTVMaze(query).ifEmpty { getFallbackMockResults(query) }/g' app/src/main/java/com/example/data/MediaSearchService.kt
sed -i 's/return@withContext getLocalFallbackDetect(title)/return@withContext searchTVMaze(title).firstOrNull() ?: getLocalFallbackDetect(title)/g' app/src/main/java/com/example/data/MediaSearchService.kt
