package com.movie.feel.threads.rottentomatoes

import com.movie.feel.Movie
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Extras
import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 27.07.13
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
class MovieParser extends Thread {

    ArrayList<JSONObject> jsonMovies
    CountDownLatch latch
    List<Movie> movies

    public MovieParser(ArrayList<JSONObject> jsonMovies, CountDownLatch latch, List<Movie> movies) {
        this.jsonMovies = jsonMovies
        this.latch = latch
        this.movies = movies
    }

    @Override
    void run() {
        // does exactly what the code below the Threads in RottenTomatoesApi does.
        JSONObject jsonMovie;
        for (int i = 0; i < jsonMovies.size(); i++) {
            jsonMovie = jsonMovies.get(i)

            // automatically finds and fills the fields by their names
            // hint: will have to construct map out of it for IMDB, for example,
            // if the fields are not the same <=> normalize
            Movie movie = new Movie(jsonMovie)
            movie.rottenTomatoId = jsonMovie?.get("id")
            // these have to be explicit for some reason

            movie.release_dates = Extras.formatHashMap(jsonMovie?.get("release_dates")?.toString())
            movie.posters = Extras.formatHashMap(jsonMovie?.get("posters")?.toString())
            movie.ratings = Extras.formatHashMap(jsonMovie?.get("ratings")?.toString())

            // automatically adds them to the movie as well
            RottenTomatoesApi.getInstance().getReviewsForMovie(movie)

            movie.validate()
            if (!movie.hasErrors()) {
                movie.save()
               movies.add(movie)
            }
        }
        latch.countDown();
    }
}
