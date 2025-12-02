package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Ahoy! Fetching all movie treasures for the crew");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("genres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * Search for movies based on query parameters.
     * Arrr! This endpoint helps ye find specific movie treasures, matey!
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional)
     * @param genre Genre to filter by (optional)
     * @param format Response format: "json" for JSON response, anything else for HTML
     * @param model Spring model for HTML rendering
     * @return JSON response or HTML template
     */
    @GetMapping("/movies/search")
    public Object searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "format", required = false, defaultValue = "html") String format,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Searching for movie treasures with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Return JSON response if requested
            if ("json".equalsIgnoreCase(format)) {
                return ResponseEntity.ok(createSearchResponse(searchResults, name, id, genre));
            }
            
            // Return HTML response
            model.addAttribute("movies", searchResults);
            model.addAttribute("genres", movieService.getAllGenres());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name != null ? name : "");
            model.addAttribute("searchId", id != null ? id.toString() : "");
            model.addAttribute("searchGenre", genre != null ? genre : "");
            model.addAttribute("resultCount", searchResults.size());
            
            if (searchResults.isEmpty()) {
                model.addAttribute("noResults", true);
                model.addAttribute("searchMessage", "Arrr! No movie treasures found matching yer search criteria, matey!");
            } else {
                model.addAttribute("searchMessage", 
                    String.format("Ahoy! Found %d movie treasure%s matching yer search!", 
                        searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            return "movies";
            
        } catch (Exception e) {
            logger.error("Scurvy bug encountered while searching for movies: {}", e.getMessage(), e);
            model.addAttribute("title", "Search Error");
            model.addAttribute("message", "Arrr! A scurvy bug prevented us from searching the treasure chest. Try again, matey!");
            return "error";
        }
    }

    /**
     * JSON-only search endpoint for API consumers.
     * Perfect for those landlubbers who prefer their data in JSON format!
     */
    @GetMapping("/movies/search/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesJson(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("JSON search request - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            return ResponseEntity.ok(createSearchResponse(searchResults, name, id, genre));
        } catch (Exception e) {
            logger.error("Error in JSON search: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Arrr! A scurvy bug prevented the search operation");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private Map<String, Object> createSearchResponse(List<Movie> results, String name, Long id, String genre) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("results", results);
        response.put("count", results.size());
        
        Map<String, Object> searchCriteria = new HashMap<>();
        searchCriteria.put("name", name);
        searchCriteria.put("id", id);
        searchCriteria.put("genre", genre);
        response.put("searchCriteria", searchCriteria);
        
        if (results.isEmpty()) {
            response.put("message", "Arrr! No movie treasures found matching yer search criteria, matey!");
        } else {
            response.put("message", 
                String.format("Ahoy! Found %d movie treasure%s matching yer search!", 
                    results.size(), results.size() == 1 ? "" : "s"));
        }
        
        return response;
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}