package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.interfaces.observer.Observer_I
import com.movie.feel.interfaces.observer.Subject_I
import com.movie.feel.interfaces.pipeline.SearchStage_I
import com.movie.feel.helpers.Constants

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/28/13
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchStage extends AbstractStage implements SearchStage_I {


    @Override
    void searchForMovieByTitle(String title) {

        List<Movie> availableMovies = Movie.list().findAll {
            it.title ==~ /(?i).*${title}.*/
        }

        if (availableMovies.size() > 0) {

            status = Constants.STATUS_SEARCH_STAGE_FOUND_MOVIES
            movieResults = availableMovies
            notifyObserversWithCurrentStatus()

        } else {

            status = Constants.STATUS_SEARCH_STAGE_NOT_FOUND_MOVIES
            notifyObserversWithCurrentStatus()

        }
    }

    @Override
    void searchForReviewsByMovie(Movie movie) {
    }
}
