package com.movie.feel

import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I

class RetrievalService {

    MovieSitesApi_I rottenTomatoesApi
    MovieSitesApi_I imdbApi

    List<String> getAllAvailableMovieTitles() {
        List<Movie> allMovies = Movie.list().toList()
        List<String> allMovieTitles = new ArrayList<String>()

        for (Movie m : allMovies) {
            allMovieTitles.add(m.title)
        }

        return allMovieTitles
    }

    List<Movie> searchForMovie(String title) {

        def urlTitle = Extras.formatTitle(title)

        def rottenTomatoMovies = rottenTomatoesApi.searchForMoviesByTitle(urlTitle)

        return rottenTomatoMovies
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
}