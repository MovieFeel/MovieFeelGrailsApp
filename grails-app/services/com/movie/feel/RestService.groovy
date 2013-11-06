package com.movie.feel

import com.movie.feel.helpers.Extras
import com.movie.feel.pipeline.PipelineWrapper
import com.movie.feel.responses.InitialMovieDetails
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 10/4/13
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
class RestService {

    def retrievalService
    def fileExportService

    List<String> getAllMovieTitles() {
        def movieTitles = new ArrayList<String>()
        Movie.list().each { movieTitles.add(it.title + " (" + it.year + ")") }
        return movieTitles
    }

    InitialMovieDetails getInitialMovieDetailsForTitle(String title) {
        def movie = Movie.findByTitle(title)
        if (movie != null) {
            return getDetailsForMovie(movie)
        } else {
            def movies = retrievalService.searchForImdbMovie(title)
            if (!movies.isEmpty()) {
                movie = movies.get(0)
                return getDetailsForMovie(movie)
            } else {
                return new InitialMovieDetails()
            }
        }
    }

    private InitialMovieDetails getDetailsForMovie(Movie movie) {
        def initialMovieDetails = new InitialMovieDetails()
        initialMovieDetails.synopsis = movie.synopsis
        initialMovieDetails.critics_consensus = movie.critics_consensus
        initialMovieDetails.mpaa_rating = movie.mpaa_rating
        initialMovieDetails.ratings = new JSONObject(Extras.formatJSON(movie.ratings))
        initialMovieDetails.posters = new JSONObject(Extras.formatJSON(movie.posters))
        return initialMovieDetails
    }

    String getMovieRating(String title) {
        def movie = Movie.findByTitle(title)
        if (movie != null) {
            PipelineWrapper p = new PipelineWrapper()
            p.auxGetReviewsForAllMovies(movie)
        } else {

        }
    }
}
