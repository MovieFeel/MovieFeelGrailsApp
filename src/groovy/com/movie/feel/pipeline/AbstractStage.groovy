package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Constants
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.interfaces.observer.Observer_I
import com.movie.feel.interfaces.observer.Subject_I

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/28/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
class AbstractStage implements Subject_I {

    // in our case it is only 1 observer, but leaving it like this just in case we'll need it like this in the future.
    List<Observer_I> observers
    MovieSitesApi_I rottenTomatoesApi
    MovieSitesApi_I imdbApi
    String status

    public AbstractStage() {
        status = Constants.STATUS_EMPTY
        rottenTomatoesApi = RottenTomatoesApi.getInstance(Constants.RottenTomatoesApiKey, Constants.RottenTomatoesMoviePageLimit, Constants.RottenTomatoesReviewPageLimit)
        imdbApi = ImdbApi.getInstance(Constants.RottenTomatoesMoviePageLimit, Constants.IMDB_PLOT_TYPE)
        observers = new ArrayList<Observer_I>()
    }

    // delete this after implementing some concrete shit
    void doSomething() {

    }

    @Override
    void addObserver(Observer_I o) {
        observers.add(o)
    }

    @Override
    void removeObserver(Observer_I o) {
        observers.remove(o)
    }

    @Override
    void notifyObserversWithCurrentStatus() {
        for (Observer_I o : observers) {
            o.update(this)
        }
    }
}

