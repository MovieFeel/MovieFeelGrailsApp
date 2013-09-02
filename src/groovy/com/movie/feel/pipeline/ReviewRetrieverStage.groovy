package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.helpers.CurrentUserData
import com.movie.feel.interfaces.pipeline.ReviewRetrieverStage_I

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/27/13
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
class ReviewRetrieverStage extends AbstractStage implements ReviewRetrieverStage_I {

    @Override
    void startStage() {
        Movie movie = CurrentUserData.movie

        def imdbReviews = imdbApi.getReviewsForMovie(movie)
        def rottenTomatoesReviews = rottenTomatoesApi.getReviewsForMovie(movie)

        sincronizeList() {

        }

    }


}
