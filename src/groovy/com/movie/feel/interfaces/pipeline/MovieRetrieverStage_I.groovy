package com.movie.feel.interfaces.pipeline

import com.movie.feel.Movie

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/27/13
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MovieRetrieverStage_I {

    List<Movie> startStage(String title)

    void getImdbMoviesByTitle(String title, CountDownLatch latch, List<Movie> outputList)

    void getRottenTomatoesMoviesByTitle(String title, CountDownLatch latch, List<Movie> outputList)

    List<Movie> synchronizeResults(List<Movie>... movies)

}