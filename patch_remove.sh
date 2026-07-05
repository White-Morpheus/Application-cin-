#!/bin/bash
sed -i '/fun getUnsplashFallback/,+14d' app/src/main/java/com/example/data/MediaSearchService.kt
sed -i '/fun getThemedFallbackPoster/,+32d' app/src/main/java/com/example/data/MediaSearchService.kt
