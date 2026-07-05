with open("app/src/main/java/com/example/data/MediaSearchService.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    # lines are 0-indexed. Line 242 is index 241
    if 241 <= i <= 298:
        continue
    new_lines.append(line)

with open("app/src/main/java/com/example/data/MediaSearchService.kt", "w") as f:
    f.writelines(new_lines)
