#!/bin/bash
sed -i 's/return@withContext searchGemini(query)/return@withContext searchGemini(query).ifEmpty { searchTVMaze(query) }/g' app/src/main/java/com/example/data/MediaSearchService.kt
