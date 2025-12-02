package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search for movies based on provided criteria.
     * Ahoy! This method helps ye find yer treasure movies by various criteria, matey!
     * 
     * @param name Movie name to search for (partial match, case-insensitive)
     * @param id Specific movie ID to find
     * @param genre Genre to filter by (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Ahoy! Searching for movie treasures with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        // If all parameters are null or empty, return empty list to avoid returning all movies
        if ((name == null || name.trim().isEmpty()) && 
            id == null && 
            (genre == null || genre.trim().isEmpty())) {
            logger.info("No search criteria provided, returning empty treasure chest");
            return results;
        }
        
        for (Movie movie : movies) {
            boolean matches = true;
            
            // Check name criteria (partial match, case-insensitive)
            if (name != null && !name.trim().isEmpty()) {
                matches = matches && movie.getMovieName().toLowerCase()
                    .contains(name.trim().toLowerCase());
            }
            
            // Check ID criteria (exact match)
            if (id != null) {
                matches = matches && movie.getId() == id.longValue();
            }
            
            // Check genre criteria (partial match, case-insensitive)
            if (genre != null && !genre.trim().isEmpty()) {
                matches = matches && movie.getGenre().toLowerCase()
                    .contains(genre.trim().toLowerCase());
            }
            
            if (matches) {
                results.add(movie);
            }
        }
        
        logger.info("Found {} movie treasures matching the search criteria", results.size());
        return results;
    }

    /**
     * Get all available genres from the movie collection.
     * Useful for populating search form dropdowns, ye savvy pirate!
     * 
     * @return List of unique genres
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
