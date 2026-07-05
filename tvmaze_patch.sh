#!/bin/bash
cat app/src/main/java/com/example/data/MediaSearchService.kt | grep -n "suspend fun search" -B 2 -A 30
