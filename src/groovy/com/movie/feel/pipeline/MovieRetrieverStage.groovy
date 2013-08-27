package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.helpers.Constants
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.interfaces.pipeline.MovieRetrieverStage_I

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/27/13
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
class MovieRetrieverStage implements MovieRetrieverStage_I {

    MovieSitesApi_I rottenTomatoesApi
    MovieSitesApi_I imdbApi

    @Override
    List<Movie> startStage(String title) {

        def imdbMovies = new ArrayList<Movie>()
        def rottenTomatoesMovies = new ArrayList<Movie>()
        def latch = new CountDownLatch(Constants.NUMBER_OF_APIS)

        latch.await()

        Thread imdbThread = new Thread() {
            public void run() {
                getImdbMoviesByTitle(title, latch, imdbMovies)
            }
        }

        Thread rottenTomatoesThread = new Thread() {

            public void run() {
                getRottenTomatoesMoviesByTitle(title, latch, rottenTomatoesMovies)
            }
        }

        imdbThread.start()
        rottenTomatoesThread.start()

        latch.await()
        // we should now have both lists of movies


        return synchronizeResults(imdbMovies, rottenTomatoesMovies)  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<Movie> synchronizeResults(List<Movie>... movies) {
        //TODO: somehow synchronize the lists

        return null
    }

    // TODO: should these 2 be overriden, or non-existent in the interface?

    @Override
    void getImdbMoviesByTitle(String title, CountDownLatch latch, List<Movie> outputList) {
        outputList = imdbApi.searchForMovieByTitle(title)
        latch.countDown()
    }

    @Override
    void getRottenTomatoesMoviesByTitle(String title, CountDownLatch latch, List<Movie> outputList) {
        outputList = rottenTomatoesApi.searchForMovieByTitle(title)
        latch.countDown()
    }


}
