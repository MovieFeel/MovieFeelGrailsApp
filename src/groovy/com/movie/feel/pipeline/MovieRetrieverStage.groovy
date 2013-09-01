package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.interfaces.observer.Observer_I
import com.movie.feel.interfaces.observer.Subject_I
import com.movie.feel.interfaces.pipeline.MovieRetrieverStage_I

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/27/13
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
class MovieRetrieverStage extends AbstractStage implements MovieRetrieverStage_I {

    @Override
    void startStage(String imdbId) {
        Movie movie = Movie.findByImdbId(imdbId)
        if (movie != null) {
            notifyExistingMovie(movie)
            return
        }

        movie = imdbApi.searchForMovieById(imdbId)
        rottenTomatoesApi.saveAllMoviesWithTitleLike(movie.title)
    }

    void notifyExistingMovie(Movie movie) {
        status = Constants.STATUS_SEARCH_STAGE_FOUND_MOVIE
        notifyObserversWithCurrentStatus()
    }
}
