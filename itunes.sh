#!/bin/bash
curl -s "https://itunes.apple.com/search?term=inception&media=movie&limit=1" | grep artworkUrl
