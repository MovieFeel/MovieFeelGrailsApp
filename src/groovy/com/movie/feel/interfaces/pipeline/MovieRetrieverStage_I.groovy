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

    void startStage(String title)

    void synchronizeResults(List<Movie>... movies)
}