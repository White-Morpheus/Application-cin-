#!/bin/bash
cat app/src/main/java/com/example/data/MediaSearchService.kt | grep -n "private fun searchTMDB" -A 50
