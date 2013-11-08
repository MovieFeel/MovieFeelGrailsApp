package com.movie.feel.interfaces

import com.movie.feel.Movie
import com.movie.feel.Review

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 7/24/13
 * Time: 9:23 PM
 * To change this template use File | Settings | File Templates.
 */
interface MovieSitesApi_I {

    HashMap<String, String> getAllMoviesTitlesLike(String title)

    void saveAllMoviesWithTitleLike(String title)

    List<Movie> searchForMoviesByTitle(String title)

    Movie searchForMovieById(String id)

    List<Review> getReviewsForMovie(Movie movie)
}
