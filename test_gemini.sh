#!/bin/bash
curl -s -H "Content-Type: application/json" \
  -d '{"contents":[{"parts":[{"text":"Donne moi le path de l'\''affiche TMDB pour Inception. (juste le path, ex: /8066...jpg)"}]}]}' \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${GEMINI_API_KEY}"
