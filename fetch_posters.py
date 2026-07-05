import urllib.request
import json
import urllib.parse
import re

titles = [
    "Légion", "Le secret des Marrowbone", "Le prestige", "Le nouveau testament",
    "Inception", "A la croisée des mondes", "A knight of the seven kingdoms",
    "American Gods", "Barry", "Black list", "Common side effects", "Halo",
    "His dark materials", "La palma", "Le prophète", "Lost", "Monarch",
    "Pamela rose", "Plaine Orientale", "The great", "The librarians", "The witcher",
    "Tom clancy's Jack Ryan", "Tulsa King", "West World", "Démon Slave",
    "Full métal alchimiste", "Nukitashi"
]

poster_map = {}

for title in titles:
    try:
        url = "https://api.tvmaze.com/search/shows?q=" + urllib.parse.quote(title)
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            if len(data) > 0 and data[0].get('show', {}).get('image'):
                image_url = data[0]['show']['image'].get('original') or data[0]['show']['image'].get('medium')
                if image_url:
                    poster_map[title] = image_url
    except Exception as e:
        pass

print(json.dumps(poster_map, indent=2))
