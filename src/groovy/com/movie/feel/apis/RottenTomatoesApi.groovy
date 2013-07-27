package com.movie.feel.apis

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.threads.rottentomatoes.MovieParser
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 7/24/13
 * Time: 9:25 PM
 * To change this template use File | Settings | File Templates.
 */
class RottenTomatoesApi implements MovieSitesApi_I {

    private static RottenTomatoesApi rottenTomatoesApi
    final String key
    final int moviePageLimit
    final int reviewPageLimit
    DefaultHttpClient httpclient

    private RottenTomatoesApi(String key, int moviePageLimit, int reviewPageLimit) {
        this.key = key
        this.moviePageLimit = moviePageLimit
        this.reviewPageLimit = reviewPageLimit
        this.httpclient = new DefaultHttpClient()
    }


    static RottenTomatoesApi getInstance(String key, int moviePageLimit, int reviewPageLimit) {
        if (rottenTomatoesApi == null)
            rottenTomatoesApi = new RottenTomatoesApi(key, moviePageLimit, reviewPageLimit)


        return rottenTomatoesApi
    }


    @Override
    List<Movie> searchForMovieByTitle(String title) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?" + key + "&q=" + title + "&page_limit=" + moviePageLimit

        List<Movie> movies = new ArrayList<Movie>()

        def jsonResponse = doRequest(URL)

        def jsonMovies = jsonResponse.movies

        long initTime = System.currentTimeMillis()
        List<Movie> moviez
        if (jsonMovies.size() > 5) {
            // lets try with 2 threads... split in half
            // Todo: dynamically create threads with regards to the "moviePageLimit" paramenter, split them and test for values: 10,15,20,25,30,35,40,45,50 <-- movies/request
            CountDownLatch latch = new CountDownLatch(2);
            moviez = Collections.synchronizedList(new ArrayList<Movie>());

            def a = jsonMovies.collate(5).get(0)
            def b = jsonMovies.collate(5).get(1)

            Thread ta = new MovieParser(a, latch, moviez)
            Thread tb = new MovieParser(b, latch, moviez)

            ta.run()
            tb.run()

            // wait for all the threads to latch down
            // (semaphore)
            latch.await()
        }
        println("Threads time: ")
        println(System.currentTimeMillis() - initTime)

        initTime = System.currentTimeMillis()
        JSONObject jsonMovie;
        for (int i = 0; i < jsonMovies.size(); i++) {
            jsonMovie = jsonMovies.getJSONObject(i)

            // automatically finds and fills the fields by their names
            // hint: will have to construct map out of it for IMDB, for example,
            // if the fields are not the same <=> normalize
            Movie movie = new Movie(jsonMovie)
            movie.rottenTomatoId = jsonMovie?.get("id")
            // these have to be explicit for some reason

            movie.release_dates = Extras.formatHashMap(jsonMovie?.get("release_dates")?.toString())
            movie.posters = Extras.formatHashMap(jsonMovie?.get("posters")?.toString())
            movie.ratings = Extras.formatHashMap(jsonMovie?.get("ratings")?.toString())

            // Todo: should we automatically fetch them? I think we should
            // automatically adds them to the movie as well
            // getReviewsForMovie(movie)

            movie.validate()
            if (!movie.hasErrors()) {
                movie.save()
                movies.add(movie)
            }
        }

        println("No threads time: ")
        println(System.currentTimeMillis() - initTime)

        return movies
    }

    /**
     * Retrieves a movie by it's chosen attribute, depending on the external Api.
     * Automatically saves the reviews to the given movie.
     * @param movie
     * @return the list of all reviews found
     */
    @Override
    List<Review> getReviewsForMovie(Movie movie) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + movie.rottenTomatoId + "/reviews.json?" + key + "&page_limit=" + reviewPageLimit

        List<Review> reviews = new ArrayList<Review>()

        def jsonResponse = doRequest(URL)

        def jsonReviews = jsonResponse.reviews
        JSONObject jsonReview;
        for (int i = 0; i < jsonReviews.size(); i++) {
            jsonReview = jsonReviews.getJSONObject(i)

            Review review = new Review(jsonReview)
            // date is initially String, have to parse it to Date
            // review.date = Extras.formatDate(jsonReview?.get("date"), Constants.RottenTomatoes)
            // JUST WON'T WORK
            // TODO: fix the DATE PROBLEM
            review.movie = movie
            review.links = Extras.formatHashMap(jsonReview?.get("links")?.toString())
            review.source = Constants.RottenTomatoes

            review.validate()
            if (!review.hasErrors()) {

                movie.addToReviews(review)
                movie.validate()
                movie.save(flush: true)
                reviews.add(review)
            }
        }
        return reviews
    }

    JSONElement doRequest(String url) {
        HttpGet httpGet = new HttpGet(url)
        HttpResponse response = httpclient.execute(httpGet)
        def entity = response.getEntity()
        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()))
        def jsonResponse = JSON.parse(entity.getContent(), "UTF-8")
        return jsonResponse
    }
}
