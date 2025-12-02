package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for MovieService search functionality.
 * Arrr! These tests ensure our movie treasure hunting methods work ship-shape!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should load movies from movies.json
        assertTrue(movies.size() > 0);
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
        assertEquals("The Prison Escape", movie.get().getMovieName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNull() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdZero() {
        Optional<Movie> movie = movieService.getMovieById(0L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNegative() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Check that genres are sorted and unique
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i).compareTo(genres.get(i-1)) >= 0, 
                "Genres should be sorted alphabetically");
        }
        
        // Should contain expected genres from movies.json
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action/Crime"));
    }

    // Search functionality tests

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertNotNull(results);
        assertTrue(results.size() > 1); // Should find multiple movies starting with "The"
        
        // All results should contain "The" in the name
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 2L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getId());
        assertEquals("The Family Boss", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // All results should have Drama in their genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("crime"));
        }
    }

    @Test
    public void testSearchMoviesCombinedCriteria() {
        // Search for movies with "The" in name AND Drama genre
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        assertNotNull(results);
        
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesAllCriteria() {
        // Search with all three criteria
        List<Movie> results = movieService.searchMovies("Prison", 1L, "Drama");
        assertNotNull(results);
        assertEquals(1, results.size());
        
        Movie movie = results.get(0);
        assertEquals(1L, movie.getId());
        assertTrue(movie.getMovieName().toLowerCase().contains("prison"));
        assertTrue(movie.getGenre().toLowerCase().contains("drama"));
    }

    @Test
    public void testSearchMoviesNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesEmptyParameters() {
        // All parameters null or empty should return empty list
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
        
        results = movieService.searchMovies("", null, "");
        assertNotNull(results);
        assertTrue(results.isEmpty());
        
        results = movieService.searchMovies("   ", null, "   ");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesWhitespaceHandling() {
        // Test that leading/trailing whitespace is handled correctly
        List<Movie> results1 = movieService.searchMovies("Prison", null, null);
        List<Movie> results2 = movieService.searchMovies("  Prison  ", null, null);
        
        assertEquals(results1.size(), results2.size());
        if (!results1.isEmpty()) {
            assertEquals(results1.get(0).getId(), results2.get(0).getId());
        }
    }

    @Test
    public void testSearchMoviesInvalidId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesInvalidGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesConflictingCriteria() {
        // Search for a movie with ID 1 but name that doesn't match
        List<Movie> results = movieService.searchMovies("Family Boss", 1L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should be empty because ID 1 is "Prison Escape", not "Family Boss"
    }

    @Test
    public void testSearchMoviesMultipleResults() {
        // Search for movies with "The" in the name - should return multiple results
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertNotNull(results);
        assertTrue(results.size() > 1);
        
        // Verify all results contain "The"
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesSpecialCharacters() {
        // Test searching with special characters that might be in movie names
        List<Movie> results = movieService.searchMovies(":", null, null);
        assertNotNull(results);
        // Should find movies with colons in their names like "Space Wars: The Beginning"
        
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().contains(":"));
        }
    }
}