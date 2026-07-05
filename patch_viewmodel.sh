#!/bin/bash
sed -i 's/posterUrl = null,/posterUrl = MediaSearchService.getPosterForTitle(catalogItem.title),/' app/src/main/java/com/example/ui/MediaViewModel.kt
