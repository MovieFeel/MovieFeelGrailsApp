package com.movie.feel.apis

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.threads.imdb.MovieScrapper
import com.movie.feel.threads.rottentomatoes.MovieParser
import com.movie.feel.threads.rottentomatoes.ReviewParser
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONArray
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
    void saveAllMoviesWithTitleLike(String title) {

        def formattedTitle = Extras.formatTitle(title)
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?" + key + "&q=" + formattedTitle + "&page_limit=" + moviePageLimit
        def jsonResponse = doRequest(URL)
        def jsonMovies = (JSONArray) jsonResponse.movies

        JSONObject jsonMovie
        for (int i = 0; i < jsonMovies.size(); i++) {
            jsonMovie = jsonMovies.getJSONObject(i)

            Movie movie = Movie.findByTitle(title)

            if(movie == null)
            {
                movie = new Movie(jsonMovie)
            }

            movie.rottenTomatoId = jsonMovie?.get("id")
            // these have to be explicit for some reason

            movie.release_dates = Extras.formatHashMap(jsonMovie?.get("release_dates")?.toString())
            movie.posters = Extras.formatHashMap(jsonMovie?.get("posters")?.toString())
            movie.ratings = Extras.formatHashMap(jsonMovie?.get("ratings")?.toString())

            movie.validate()
            if(movie.validate()) {
                movie.save()
            }
        }
    }

    @Override
    HashMap<String, String> getAllMoviesTitlesLike(String title) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<Movie> searchForMoviesByTitle(String title) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?" + key + "&q=" + title + "&page_limit=" + moviePageLimit

        def jsonResponse = doRequest(URL)

        def jsonMovies = jsonResponse.movies

        long initTime = System.currentTimeMillis()
        List<Movie> movies = Collections.synchronizedList(new ArrayList<Movie>())
        if (jsonMovies) {
            int numberOfThreads, collateSize

            if (jsonMovies.size() < Constants.RottenTomatoesThreadSplitFactor*2) {
                numberOfThreads = 1
                collateSize = jsonMovies.size()
            } else {
                numberOfThreads = jsonMovies.size() / Constants.RottenTomatoesThreadSplitFactor
                collateSize = Constants.RottenTomatoesThreadSplitFactor
            }
            CountDownLatch latch = new CountDownLatch(numberOfThreads)

            for (int i = 0; i < numberOfThreads; i++) {
                def currentCollection = jsonMovies.collate(collateSize).get(i)
                Thread currentThread = new MovieParser(currentCollection, latch, movies)
                currentThread.run()
            }

            latch.await()

            println("Threads time: ")
            println(System.currentTimeMillis() - initTime)
        } else println("No movies found")
        return movies
    }

    @Override
    Movie searchForMovieById(String id) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/"+ id +  ".json?apikey=" + key

        def jsonResponse = doRequest(URL)

        def jsonMovie = jsonResponse.movie

        // automatically finds and fills the fields by their names
        // hint: will have to construct map out of it for IMDB, for example,
        // if the fields are not the same <=> normalize
        Movie movie = new Movie(jsonMovie)
        movie.rottenTomatoId = jsonMovie?.get("id")
        // these have to be explicit for some reason

        movie.release_dates = Extras.formatHashMap(jsonMovie?.get("release_dates")?.toString())
        movie.posters = Extras.formatHashMap(jsonMovie?.get("posters")?.toString())
        movie.ratings = Extras.formatHashMap(jsonMovie?.get("ratings")?.toString())

        movie.validate()
        if (movie.validate())
            movie.save()

        return movie
    }

    /**
     * Retrieves a movie by it's chosen attribute, depending on the external Api.
     * Automatically saves the reviews to the given movie.
     * @param movie
     * @return the list of all reviews found
     */
    @Override
    List<Review> getReviewsForMovie(Movie movie) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + movie.rottenTomatoId + "/reviews.json?" + key + "&page_limit=" + 1 +"&page=" + 1 +"&review_type=all"

        List<Review> reviews = Collections.synchronizedList(new ArrayList<Review>())

        def jsonResponse = doRequest(URL)

        int totalReviews = (int) jsonResponse.total

        int numberOfThreads = Math.ceil(totalReviews / reviewPageLimit)

        CountDownLatch latch = new CountDownLatch(numberOfThreads)

        for (int i = 1; i <= numberOfThreads; i += 1) {
            Thread currentThread = new ReviewParser(latch, movie, i, reviews)
            currentThread.run()
        }

        latch.await()

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
