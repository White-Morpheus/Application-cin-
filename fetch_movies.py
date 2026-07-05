import urllib.request
import urllib.parse
import json

movies = ["Legion 2010 film", "Marrowbone film", "The Prestige film", "The Brand New Testament", "Inception"]
poster_map = {}

for movie in movies:
    try:
        url = "https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrsearch=" + urllib.parse.quote(movie) + "&gsrlimit=1&prop=pageimages&format=json&pithumbsize=500"
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            pages = data['query']['pages']
            for page_id in pages:
                if 'thumbnail' in pages[page_id]:
                    poster_map[movie] = pages[page_id]['thumbnail']['source']
    except Exception as e:
        pass

print(json.dumps(poster_map, indent=2))
