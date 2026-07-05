    private fun searchWikipediaPoster(title: String, mediaType: String): String? {
        return try {
            val query = if (mediaType == "FILM") "$title film" else "$title series"
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrsearch=$encodedQuery&gsrlimit=1&prop=pageimages&format=json&pithumbsize=500"
            val request = okhttp3.Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val bodyString = response.body?.string() ?: return null
                val jsonObject = org.json.JSONObject(bodyString)
                val queryObj = jsonObject.optJSONObject("query") ?: return null
                val pagesObj = queryObj.optJSONObject("pages") ?: return null
                val keys = pagesObj.keys()
                if (keys.hasNext()) {
                    val firstPage = pagesObj.getJSONObject(keys.next())
                    val thumbnail = firstPage.optJSONObject("thumbnail")
                    return thumbnail?.optString("source")
                }
                null
            }
        } catch (e: Exception) {
            null
        }
    }
