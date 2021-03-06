package com.movie.feel

import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I

class RetrievalService {

    MovieSitesApi_I rottenTomatoesApi
    MovieSitesApi_I imdbApi

    List<Movie> searchForMovie(String title) {

        def urlTitle = Extras.formatTitle(title)

        def rottenTomatoMovies = rottenTomatoesApi.searchForMoviesByTitle(urlTitle)
        if (rottenTomatoMovies.isEmpty()) {
            rottenTomatoMovies = imdbApi.searchForMoviesByTitle(urlTitle)
        } else {
            if (rottenTomatoMovies.get(0).imdbId == null) {
                // TODO: nu stiu sigur ce sa facem aici.
            }
        }
        return rottenTomatoMovies
    }

    void dummyMovies() {
        rottenTomatoesApi.saveAllMoviesWithTitleLike("Toy Story")
        rottenTomatoesApi.saveAllMoviesWithTitleLike("Lord of the Rings")
        rottenTomatoesApi.saveAllMoviesWithTitleLike("Rocky")
        rottenTomatoesApi.saveAllMoviesWithTitleLike("Star Wars")
    }

    List<Review> getReviewsForMovie(String title) {
        List<Review> rottenTomatoReviews = null

        //The (?i) makes the match ignore case.
        List<Movie> movies = Movie.list().findAll {
            it.title ==~ /(?i).*${title}.*/
        }
        // by convention, choose first in list, maybe implements something else to let the user decide which one to choose.
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

    // TEMPORARY
    List<Movie> searchForImdbMovie(String title) {
        def urlTitle = Extras.formatTitle(title)

        def imdbMovies = imdbApi.searchForMoviesByTitle(urlTitle)

        return imdbMovies

    }

    //TEMPORARY
    List<Review> getImdbMovieReviews(Movie movie) {
        return imdbApi.getReviewsForMovie(movie)
    }

    List<Review> getReviewsForMovieImdb(String title) {
        List<Review> ImdbReviews = null

        //The (?i) makes the match ignore case.
        List<Movie> movies = Movie.list().findAll {
            it.title ==~ /(?i).*${title}.*/
        }
        // by convention, choose first in list, maybe implements something else to let the user decide which one to choose.
        // the search should be done with some auto-completion involved
        // in order to facilitate database search
        if (movies.size() > 0) {
            // Todo: find some logic here
            if (movies.get(0).reviews?.size() > 0) {
                return movies.get(0).reviews.asList()
            } else
                ImdbReviews = imdbApi.getReviewsForMovie(movies.get(0))
        }
        // no movies found with that title in the database
        else {
            def freshMovies = searchForImdbMovie(title)
            if (freshMovies.size() > 0)
            // Todo: if we automatically get the reviews when getting the movie we may want to do check if it already has reviews and return them.
                ImdbReviews = imdbApi.getReviewsForMovie(freshMovies.get(0))
        }

        return ImdbReviews
    }

    List<Review> getReviewsForMovieImdbById(String id) {
        List<Review> ImdbReviews = null

        Movie movie = Movie.list().find {
            it.imdbId == /(?i).*${id}.*/
        }

        // by convention, choose first in list, maybe implements something else to let the user decide which one to choose.
        // the search should be done with some auto-completion involved
        // in order to facilitate database search
        if (movie != null) {
            // Todo: find some logic here
            if (movie.reviews?.size() > 0) {
                return movie.reviews.asList()
            } else
                ImdbReviews = imdbApi.getReviewsForMovie(movie)
        }
        // no movies found with that title in the database
        else {
            def freshMovies = searchForImdbMovie(id)
            if (freshMovies.size() > 0)
            // Todo: if we automatically get the reviews when getting the movie we may want to do check if it already has reviews and return them.
                ImdbReviews = imdbApi.getReviewsForMovie(freshMovies.get(0))
        }

        return ImdbReviews
    }
}