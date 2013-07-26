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
        if (movies.size() > 0)
            rottenTomatoReviews = rottenTomatoesApi.getReviewsForMovie(movies.get(0))
        // no movies found with that title in the database
        else {
            def freshMovies = searchForMovie(title)
            if (movies.size() > 0)
                rottenTomatoReviews = rottenTomatoesApi.getReviewsForMovie(movies.get(0))
        }


        return rottenTomatoReviews
    }
}