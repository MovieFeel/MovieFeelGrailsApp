package com.movie.feel.apis

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.MovieSitesApi_I
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

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
