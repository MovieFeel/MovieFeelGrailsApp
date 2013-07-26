package com.movie.feel.interfaces

import com.movie.feel.Movie

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 7/24/13
 * Time: 9:23 PM
 * To change this template use File | Settings | File Templates.
 */
interface MovieSitesApi_I {

    List<Movie> searchForMovieByTitle(String title)

}
