import re

with open("app/src/main/java/com/example/data/MediaSearchService.kt", "r") as f:
    lines = f.readlines()

for i in range(255, 305):
    if i < len(lines):
        print(f"{i+1}: {lines[i].rstrip()}")
