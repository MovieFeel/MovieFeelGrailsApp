package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Constants
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.interfaces.observer.Observer_I
import com.movie.feel.interfaces.observer.Subject_I
import com.movie.feel.interfaces.pipeline.MovieRetrieverStage_I

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/27/13
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
class MovieRetrieverStage extends AbstractStage implements MovieRetrieverStage_I {

    @Override
    void startStage(String title) {

        Movie movie = Movie.findByTitle(title)

        if (movie != null) {
            notifyExistingMovie(movie)
            return
        }

        def imdbMovies = new ArrayList<Movie>()
        def rottenTomatoesMovies = new ArrayList<Movie>()
        def latch = new CountDownLatch(Constants.NUMBER_OF_APIS)

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


        synchronizeResults(imdbMovies, rottenTomatoesMovies)
    }

    void notifyExistingMovie(Movie movie) {
        status = Constants.STATUS_SEARCH_STAGE_FOUND_MOVIE
        notifyObserversWithCurrentStatus()
    }

    @Override
    void synchronizeResults(List<Movie>... movies) {
        //TODO: somehow synchronize the lists
        status = Constants.STATUS_RETRIEVER_STAGE_SUCCESS
        notifyObserversWithCurrentStatus()
    }

    // TODO: should these 2 be overriden, or non-existent in the interface?
    @Override
    void getImdbMoviesByTitle(String title, CountDownLatch latch, List<Movie> outputList) {
        //  outputList = imdbApi.searchForMovieByTitle(title)
        latch.countDown()
    }

    @Override
    void getRottenTomatoesMoviesByTitle(String title, CountDownLatch latch, List<Movie> outputList) {
        // outputList = rottenTomatoesApi.searchForMovieByTitle(title)
        latch.countDown()
    }

}
