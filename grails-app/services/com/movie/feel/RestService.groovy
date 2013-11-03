package com.movie.feel

import com.movie.feel.helpers.Extras
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

    List<String> getAllMovieTitles() {
        def movieTitles = new ArrayList<String>()
        Movie.list().each { movieTitles.add(it.title + " (" + it.year + ")") }
        return movieTitles
    }

    InitialMovieDetails getInitialMovieDetailsForTitle(String title) {
        def movie = Movie.findByTitle(title)
        if (movie != null) {
            def initialMovieDetails = new InitialMovieDetails()
            initialMovieDetails.synopsis = movie.synopsis
            initialMovieDetails.critics_consensus = movie.critics_consensus
            initialMovieDetails.mpaa_rating = movie.mpaa_rating
            initialMovieDetails.ratings = new JSONObject(Extras.formatJSON(movie.ratings))
            initialMovieDetails.posters = new JSONObject(Extras.formatJSON(movie.posters))
            return initialMovieDetails
        }
        return new InitialMovieDetails()
    }
}
