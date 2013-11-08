package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.CurrentUserData
import com.movie.feel.interfaces.observer.Observer_I
import com.movie.feel.interfaces.observer.Subject_I
import com.movie.feel.interfaces.pipeline.SearchStage_I
import com.movie.feel.helpers.Constants

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/28/13
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchStage extends AbstractStage implements SearchStage_I {


    @Override
    void searchForMovieByTitle(String title) {

        // tries to find the movie matching that exact title
        Movie movie = Movie.findByTitle(title)

        // found a movie
        if (movie != null) {

            CurrentUserData.movie = movie
            status = Constants.STATUS_SEARCH_STAGE_FOUND_MOVIE
            notifyObserversWithCurrentStatus()

        }
        // no movies found, return list of probable movies
        else {
            // searching only for imdb. If not found in imdb, movie does not exist or will never be searched for.
            CurrentUserData.movieTitles = imdbApi.getAllMoviesTitlesLike(title)
            status = Constants.STATUS_SEARCH_STAGE_GETTING_MOVIE_TITLES
            notifyObserversWithCurrentStatus()

        }


    }

    @Override
    void searchForReviewsByMovie(Movie movie) {
    }
}
