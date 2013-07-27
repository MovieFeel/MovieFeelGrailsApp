package com.movie.feel

import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I

class RetrievalService {

    MovieSitesApi_I rottenTomatoesApi
    MovieSitesApi_I someOtherApi

    List<Movie> searchForMovie(String title) {

        def urlTitle = Extras.formatTitle(title)

        def rottenTomatoMovies = rottenTomatoesApi.searchForMovieByTitle(urlTitle)

        return rottenTomatoMovies
    }

    List<Review> getReviewsForMovie(String title) {

        List<Movie> movies = Movie.findAllByTitleLike(title);

        // by convention, choose first in list, maybe implements something else to let the user decide which one to choose.

        List<Review> rottenTomatoReviews = null;

        // the search should be done with some auto-completion involved
        // in order to facilitate database search
        if (movies.size() > 0) {

            // Todo: find some logic here
            if (movies.get(0).reviews?.size() > 0) {
                return movies.get(0).reviews.asList()
            } else
                rottenTomatoReviews = rottenTomatoesApi.getReviewsForMovie(movies.get(0))

        }
        // no movies found with that title in the database
        else {
            def freshMovies = searchForMovie(title)
            if (freshMovies.size() > 0)
            // Todo: if we automatically get the reviews when getting the movie we may want to do check if it already has reviews and return them.
                rottenTomatoReviews = rottenTomatoesApi.getReviewsForMovie(freshMovies.get(0))
        }


        return rottenTomatoReviews
    }
}