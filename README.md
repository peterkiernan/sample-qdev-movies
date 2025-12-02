# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate-themed movie search and filtering system!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Search for movie treasures by name, ID, or genre with our pirate-themed interface
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **🏴‍☠️ Pirate Theme**: Ahoy! Navigate the movie treasure chest with pirate-themed language and styling

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for server-side templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🔍 Movie Search**: http://localhost:8080/movies/search (with query parameters)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Comprehensive service tests
            ├── MoviesControllerTest.java     # Controller tests with search
            └── MovieTest.java                # Model tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a search form.

### 🔍 Search Movies (NEW!)
```
GET /movies/search
```
Search for movie treasures using various criteria! Arrr!

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Specific movie ID to find (exact match)
- `genre` (optional): Genre to filter by (partial match, case-insensitive)
- `format` (optional): Response format - "json" for JSON response, default is HTML

**Examples:**
```bash
# Search by movie name
http://localhost:8080/movies/search?name=Prison

# Search by genre
http://localhost:8080/movies/search?genre=Drama

# Search by ID
http://localhost:8080/movies/search?id=1

# Combined search
http://localhost:8080/movies/search?name=The&genre=Action

# Get JSON response
http://localhost:8080/movies/search?name=Prison&format=json
```

### 🔍 JSON-Only Search Endpoint
```
GET /movies/search/json
```
Returns search results in JSON format only - perfect for API consumers!

**Query Parameters:** Same as `/movies/search`

**JSON Response Format:**
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
    "name": "Prison",
    "id": null,
    "genre": null
  },
  "message": "Ahoy! Found 1 movie treasure matching yer search!"
}
```

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🔍 Search Features

### Search Capabilities
- **Name Search**: Partial, case-insensitive matching on movie names
- **ID Search**: Exact matching on movie IDs
- **Genre Search**: Partial, case-insensitive matching on genres
- **Combined Search**: Use multiple criteria together (AND logic)
- **Empty Results Handling**: Friendly pirate-themed messages for no results
- **Input Validation**: Handles null, empty, and whitespace-only inputs

### Search Form Features
- **Pirate-Themed UI**: 🏴‍☠️ Styled with pirate colors and language
- **Genre Dropdown**: Populated with all available genres
- **Persistent Search**: Form remembers your search criteria
- **Clear Search**: Easy button to reset and view all movies
- **Responsive Design**: Works on all device sizes

### Edge Cases Handled
- Empty search parameters return no results (prevents showing all movies)
- Invalid IDs return empty results
- Whitespace-only inputs are treated as empty
- Case-insensitive searching for better user experience
- Conflicting criteria (e.g., wrong name for specific ID) return no results

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest

# Run with coverage
mvn test jacoco:report
```

### Test Coverage
- **MovieService**: 15+ test methods covering all search scenarios
- **MoviesController**: 12+ test methods covering HTML and JSON responses
- **Edge Cases**: Comprehensive testing of invalid inputs, empty results, and error conditions

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that movies.json is properly loaded
2. Verify search parameters are correctly formatted
3. Check application logs for any errors

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (year range, rating filters, etc.)
- Improve the responsive design
- Add more comprehensive error handling

### Adding New Movies

Edit `src/main/resources/movies.json` and add new movie objects:

```json
{
  "id": 13,
  "movieName": "New Pirate Adventure",
  "director": "Captain Director",
  "year": 2024,
  "genre": "Adventure/Comedy",
  "description": "A swashbuckling tale of treasure and friendship",
  "duration": 120,
  "imdbRating": 4.5
}
```

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

🏴‍☠️ **Ahoy matey!** May ye find all the movie treasures yer heart desires! **Arrr!** 🏴‍☠️
