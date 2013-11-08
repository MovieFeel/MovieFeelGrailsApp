package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.CurrentUserData
import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.pipeline.ReviewRetrieverStage_I

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/27/13
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
class ReviewRetrieverStage extends AbstractStage implements ReviewRetrieverStage_I {

    def static fileExportService

    @Override
    void startStage() {
        Movie movie = CurrentUserData.movie

        def imdbReviews = imdbApi.getReviewsForMovie(movie)
        def rottenTomatoesReviews = rottenTomatoesApi.getReviewsForMovie(movie)

        CurrentUserData.movie.reviews = Extras.synchronizeLists(imdbReviews, rottenTomatoesReviews)

        //Export the movie reviews to a file
        fileExportService.exportReviewsToFiles(movie)
    }


}
