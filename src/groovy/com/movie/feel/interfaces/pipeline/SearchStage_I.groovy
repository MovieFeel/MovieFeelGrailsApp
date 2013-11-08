package com.movie.feel.interfaces.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/28/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SearchStage_I {

    void searchForMovieByTitle(String title)

    void searchForReviewsByMovie(Movie movie)

}