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

    static RottenTomatoesApi getInstance() {
        if (rottenTomatoesApi == null)
            RottenTomatoesApi.getInstance("apikey=hwwmxfuufhtukd3y8s4vwncu", 50, 50)

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
        if(jsonMovies)
        {
            int numberOfThreads = jsonMovies.size()/Constants.RottenTomatoesThreadSplitFactor
            CountDownLatch latch = new CountDownLatch(numberOfThreads)
            moviez = Collections.synchronizedList(new ArrayList<Movie>())

            for(int i=0;i<numberOfThreads;i++)
            {
                def currentCollection = jsonMovies.collate(Constants.RottenTomatoesThreadSplitFactor).get(i)
                Thread currentThread = new MovieParser(currentCollection, latch, moviez)
                currentThread.run()
            }

            latch.await()

            println("Moviez size")
            println(jsonMovies.size())

            println("Number of threads:")
            println(numberOfThreads)

            println("Threads time: ")
            println(System.currentTimeMillis() - initTime)
        }
        else println("No movies found")
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
        if(jsonReviews)
        {
            for (int i = 0; i < jsonReviews.size(); i++) {
                jsonReview = jsonReviews.getJSONObject(i)

                Review review = new Review(jsonReview)
                review.date = Extras.formatDate(jsonReview?.get("date"), Constants.RottenTomatoes)
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
