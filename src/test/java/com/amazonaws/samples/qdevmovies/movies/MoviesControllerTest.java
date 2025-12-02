package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                // If all parameters are null or empty, return empty list
                if ((name == null || name.trim().isEmpty()) && 
                    id == null && 
                    (genre == null || genre.trim().isEmpty())) {
                    return results;
                }
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && movie.getMovieName().toLowerCase()
                            .contains(name.trim().toLowerCase());
                    }
                    
                    if (id != null) {
                        matches = matches && movie.getId() == id.longValue();
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matches = matches && movie.getGenre().toLowerCase()
                            .contains(genre.trim().toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Comedy", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("genres"));
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    // New tests for search functionality

    @Test
    public void testSearchMoviesByName() {
        String result = (String) moviesController.searchMovies("Test", null, null, "html", model);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesById() {
        String result = (String) moviesController.searchMovies(null, 2L, null, "html", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        String result = (String) moviesController.searchMovies(null, null, "Comedy", "html", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Comedy Film", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = (String) moviesController.searchMovies("NonExistent", null, null, "html", model);
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("noResults"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testSearchMoviesEmptyParameters() {
        String result = (String) moviesController.searchMovies("", null, "", "html", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testSearchMoviesJsonFormat() {
        ResponseEntity<Map<String, Object>> result = 
            (ResponseEntity<Map<String, Object>>) moviesController.searchMovies("Test", null, null, "json", model);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("results");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesJsonEndpoint() {
        ResponseEntity<Map<String, Object>> result = 
            moviesController.searchMoviesJson("Action", null, null);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
    }

    @Test
    public void testSearchMoviesJsonNoResults() {
        ResponseEntity<Map<String, Object>> result = 
            moviesController.searchMoviesJson("NonExistent", null, null);
        
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        
        Map<String, Object> body = result.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
        assertTrue(body.get("message").toString().contains("No movie treasures found"));
    }

    @Test
    public void testSearchMoviesCombinedCriteria() {
        String result = (String) moviesController.searchMovies("Movie", 1L, "Drama", "html", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesCaseInsensitive() {
        String result = (String) moviesController.searchMovies("test", null, null, "html", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }
}
