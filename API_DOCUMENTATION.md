# Movie Search API Documentation 🏴‍☠️

Ahoy matey! This document provides comprehensive technical documentation for the Movie Search and Filtering API endpoints. Perfect for developers who want to integrate with our pirate-themed movie treasure hunting system!

## Base URL

```
http://localhost:8080
```

## Authentication

No authentication required - this be a free treasure chest for all!

## Content Types

- **HTML Responses**: `text/html`
- **JSON Responses**: `application/json`

## API Endpoints

### 1. Search Movies

**Endpoint:** `GET /movies/search`

**Description:** Search for movies using various criteria. Returns HTML by default, JSON if requested.

#### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | No | Movie name to search for (partial match, case-insensitive) |
| `id` | Long | No | Specific movie ID to find (exact match) |
| `genre` | String | No | Genre to filter by (partial match, case-insensitive) |
| `format` | String | No | Response format: "json" for JSON, default is HTML |

#### Request Examples

```bash
# Search by movie name
curl "http://localhost:8080/movies/search?name=Prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=Drama"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=The&genre=Action"

# Get JSON response
curl "http://localhost:8080/movies/search?name=Prison&format=json"
```

#### HTML Response

Returns the movies.html template with search results and form populated with search criteria.

#### JSON Response Format

```json
{
  "success": true,
  "results": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "count": 1,
  "searchCriteria": {
    "name": "Prison",
    "id": null,
    "genre": null
  },
  "message": "Ahoy! Found 1 movie treasure matching yer search!"
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `success` | Boolean | Always true for successful requests |
| `results` | Array | Array of movie objects matching search criteria |
| `count` | Integer | Number of movies found |
| `searchCriteria` | Object | Echo of the search parameters used |
| `message` | String | Pirate-themed message about the search results |

### 2. JSON-Only Search

**Endpoint:** `GET /movies/search/json`

**Description:** Search for movies and return only JSON response. Optimized for API consumers.

#### Request Parameters

Same as `/movies/search` but without the `format` parameter.

#### Request Examples

```bash
# Search by name
curl "http://localhost:8080/movies/search/json?name=Action"

# Search by genre
curl "http://localhost:8080/movies/search/json?genre=Comedy"

# Combined search
curl "http://localhost:8080/movies/search/json?name=The&id=1"
```

#### Response Format

Same JSON format as `/movies/search?format=json`

### 3. Get All Movies

**Endpoint:** `GET /movies`

**Description:** Get all movies with search form interface.

#### Response

Returns HTML page with all movies and search form.

### 4. Get Movie Details

**Endpoint:** `GET /movies/{id}/details`

**Description:** Get detailed information about a specific movie.

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | Long | Yes | Movie ID (1-12) |

## Movie Object Schema

```json
{
  "id": 1,
  "movieName": "The Prison Escape",
  "director": "John Director",
  "year": 1994,
  "genre": "Drama",
  "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
  "duration": 142,
  "imdbRating": 5.0,
  "icon": "🎬"
}
```

### Field Descriptions

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique movie identifier |
| `movieName` | String | Full movie title |
| `director` | String | Movie director name |
| `year` | Integer | Release year |
| `genre` | String | Movie genre(s), may contain multiple genres separated by "/" |
| `description` | String | Movie plot description |
| `duration` | Integer | Movie duration in minutes |
| `imdbRating` | Double | Rating out of 5.0 |
| `icon` | String | Emoji icon for the movie |

## Search Behavior

### Name Search
- **Case-insensitive**: "prison" matches "The Prison Escape"
- **Partial matching**: "The" matches all movies starting with "The"
- **Whitespace handling**: Leading/trailing spaces are trimmed

### ID Search
- **Exact matching**: Only returns movie with exact ID
- **Invalid IDs**: Return empty results (no error)

### Genre Search
- **Case-insensitive**: "drama" matches "Drama"
- **Partial matching**: "Crime" matches "Crime/Drama"
- **Multi-genre support**: Searches within compound genres like "Action/Crime"

### Combined Search
- **AND logic**: All specified criteria must match
- **Empty parameters**: Ignored in search logic
- **Conflicting criteria**: May return empty results

## Error Handling

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 500 | Internal server error |

### Error Response Format

```json
{
  "success": false,
  "error": "Arrr! A scurvy bug prevented the search operation",
  "message": "Detailed error message"
}
```

### Common Error Scenarios

1. **Empty Search Parameters**: Returns empty results (not an error)
2. **Invalid ID**: Returns empty results
3. **Server Error**: Returns 500 with error message
4. **Malformed Parameters**: Handled gracefully, invalid values ignored

## Rate Limiting

No rate limiting currently implemented - this be a friendly pirate ship!

## Examples

### Search for Action Movies

**Request:**
```bash
curl "http://localhost:8080/movies/search/json?genre=Action"
```

**Response:**
```json
{
  "success": true,
  "results": [
    {
      "id": 3,
      "movieName": "The Masked Hero",
      "director": "Chris Moviemaker",
      "year": 2008,
      "genre": "Action/Crime",
      "description": "When a menacing villain wreaks havoc...",
      "duration": 152,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "count": 1,
  "searchCriteria": {
    "name": null,
    "id": null,
    "genre": "Action"
  },
  "message": "Ahoy! Found 1 movie treasure matching yer search!"
}
```

### Search with No Results

**Request:**
```bash
curl "http://localhost:8080/movies/search/json?name=NonExistent"
```

**Response:**
```json
{
  "success": true,
  "results": [],
  "count": 0,
  "searchCriteria": {
    "name": "NonExistent",
    "id": null,
    "genre": null
  },
  "message": "Arrr! No movie treasures found matching yer search criteria, matey!"
}
```

### Combined Search

**Request:**
```bash
curl "http://localhost:8080/movies/search/json?name=The&genre=Drama"
```

**Response:**
```json
{
  "success": true,
  "results": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "count": 1,
  "searchCriteria": {
    "name": "The",
    "id": null,
    "genre": "Drama"
  },
  "message": "Ahoy! Found 1 movie treasure matching yer search!"
}
```

## Available Genres

The following genres are available in the current movie dataset:

- Action/Crime
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

## Integration Tips

1. **URL Encoding**: Always URL-encode search parameters, especially those with spaces or special characters
2. **Empty Results**: Check the `count` field to determine if results were found
3. **Case Sensitivity**: All text searches are case-insensitive, so normalize your queries as needed
4. **Partial Matching**: Use partial strings for broader search results
5. **Error Handling**: Always check the `success` field in JSON responses
6. **Performance**: The JSON endpoint (`/movies/search/json`) is optimized for API usage

## SDK Examples

### JavaScript/Node.js

```javascript
const axios = require('axios');

async function searchMovies(name, id, genre) {
  try {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (id) params.append('id', id);
    if (genre) params.append('genre', genre);
    
    const response = await axios.get(
      `http://localhost:8080/movies/search/json?${params}`
    );
    
    return response.data;
  } catch (error) {
    console.error('Search failed:', error.message);
    throw error;
  }
}

// Usage
searchMovies('The', null, 'Drama')
  .then(results => console.log(results))
  .catch(error => console.error(error));
```

### Python

```python
import requests
from urllib.parse import urlencode

def search_movies(name=None, movie_id=None, genre=None):
    params = {}
    if name:
        params['name'] = name
    if movie_id:
        params['id'] = movie_id
    if genre:
        params['genre'] = genre
    
    url = f"http://localhost:8080/movies/search/json?{urlencode(params)}"
    
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.RequestException as e:
        print(f"Search failed: {e}")
        raise

# Usage
results = search_movies(name="Prison")
print(f"Found {results['count']} movies")
```

### Java

```java
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MovieSearchClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    
    public SearchResponse searchMovies(String name, Long id, String genre) 
            throws Exception {
        StringBuilder url = new StringBuilder("http://localhost:8080/movies/search/json?");
        
        if (name != null) url.append("name=").append(name).append("&");
        if (id != null) url.append("id=").append(id).append("&");
        if (genre != null) url.append("genre=").append(genre).append("&");
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .GET()
            .build();
            
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
            
        return mapper.readValue(response.body(), SearchResponse.class);
    }
}
```

---

🏴‍☠️ **Happy treasure hunting, ye savvy developers!** May yer API calls always return bountiful results! **Arrr!** 🏴‍☠️