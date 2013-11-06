package com.movie.feel.apis

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.interfaces.MovieSitesApi_I
import com.movie.feel.threads.imdb.MovieScrapper
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element
import java.util.*;
import java.text.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/13/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
class ImdbApi implements MovieSitesApi_I {

    private static ImdbApi imdbApi
    DefaultHttpClient httpclient
    int pageLimit
    String plotType

    private ImdbApi(int pageLimit, String plotType) {
        this.pageLimit = pageLimit
        this.plotType = plotType
        this.httpclient = new DefaultHttpClient()
    }

    public static ImdbApi getInstance(int pageLimit, String plotType) {
        if (imdbApi == null)
            imdbApi = new ImdbApi(pageLimit, plotType)
        return imdbApi
    }

    public static ImdbApi getInstance() {
        if (imdbApi == null)
            imdbApi = new ImdbApi(Constants.RottenTomatoesMoviePageLimit, "full")
        return imdbApi
    }

    @Override
    HashMap<String, String> getAllMoviesTitlesLike(String title) {
        String URL = "http://mymovieapi.com/?title=" + title + "&plot=" + plotType + "&limit=" + pageLimit
        HashMap<String, String> movies = new HashMap<String, String>()

        def jsonMovies = (JSONArray) doRequest(URL)

        JSONObject jsonMovie;
        for (int i = 0; i < jsonMovies.size(); i++) {
            jsonMovie = jsonMovies.getJSONObject(i)
            String mTitle = jsonMovie.title
            movies.put(mTitle, jsonMovie.id)
        }
        return movies
    }

    @Override
    void saveAllMoviesWithTitleLike(String title) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<Movie> searchForMoviesByTitle(String title) {

        String URL = "http://mymovieapi.com/?title=" + title + "&plot=" + plotType + "&limit=" + pageLimit
        List<Movie> movies = new ArrayList<Movie>()

        // this will contain an array list of json elements
        def jsonMovies = (JSONArray) doRequest(URL)

        JSONObject jsonMovie;
        for (int i = 0; i < jsonMovies.size(); i++) {
            jsonMovie = jsonMovies.getJSONObject(i)

            String mTitle = jsonMovie.title
            Movie m = Movie.findByTitle(mTitle)

            // movie already exists
            if (m != null) {

                // just grab the id
                m.imdbId = jsonMovie.imdb_id

            } else {
                // set the "alreay the same name" variables
                m = new Movie(jsonMovie)
                def map = constructMap()
                for (def jsonElement : jsonMovie)
                    if (map.containsKey(jsonElement.getKey())) {
                        if (!jsonElement.getKey().equals("rating") && !jsonElement.getKey().equals("runtime"))
                            m.setProperty(map.get(jsonElement.getKey()), jsonElement.getValue())
                        else {
                            // treat the rating in a special way
                            HashMap<String, String> ratings = new HashMap<String, String>()
                            ratings.put("critics_rating", "")
                            ratings.put("critics_score", "")
                            ratings.put("audience_rating", "")
                            ratings.put("audience_score", (Double.parseDouble(jsonElement.getValue().toString()) * 10).toString())
                            m.ratings = ratings.toString()

                        }
                    }
            }

            // can someone explain why I have to do this?
            m.validate()
            if (m.validate()) {
                m.save()
            movies.add(m)
            }
        }

        return movies
    }

    @Override
    Movie searchForMovieById(String id) {

        String URL = "http://mymovieapi.com/?ids=" + id + "&type=json"
        def jsonMovie = (JSONObject) doRequest(URL)
        String mTitle = jsonMovie.title
        Movie movie = Movie.findByTitle(mTitle)

        // movie already exists
        if (movie != null) {

            // just grab the id
            movie.imdbId = jsonMovie.imdb_id

        } else {
            // set the "already the same name" variables
            movie = new Movie(jsonMovie)
            def map = constructMap()
            for (def jsonElement : jsonMovie)
                if (map.containsKey(jsonElement.getKey())) {
                    if (!jsonElement.getKey().equals("rating") && !jsonElement.getKey().equals("runtime"))
                        movie.setProperty(map.get(jsonElement.getKey()), jsonElement.getValue())
                    else {
                        // treat the rating in a special way
                        HashMap<String, String> ratings = new HashMap<String, String>()
                        ratings.put("critics_rating", "")
                        ratings.put("critics_score", "")
                        ratings.put("audience_rating", "")
                        ratings.put("audience_score", (Double.parseDouble(jsonElement.getValue().toString()) * 10).toString())
                        movie.ratings = ratings.toString()

                    }
                }

           movie.validate()
            if (movie.validate())
                movie.save()
        }

        return movie
    }


    //TODO Find the optimal number of threads(Darius)

    List<Review> getReviewsForMovie(Movie movie) {
        def reviewsNumber = getReviewsNumberUsingScrapping(movie)
        if(reviewsNumber > 1000)
            reviewsNumber = 1000
        List<Review> reviews = Collections.synchronizedList(new ArrayList<Review>())
        if (reviewsNumber > 0)
        {
            int numberOfThreads = 2
            int halfReviews = reviewsNumber / 2

            long initTime = System.currentTimeMillis()


            CountDownLatch latch = new CountDownLatch(numberOfThreads)

            for (int i = 0; i < reviewsNumber; i += halfReviews) {
                Thread currentThread = new MovieScrapper(latch, movie, i, halfReviews,reviews)
                currentThread.run()
            }

            latch.await()

            println("Imdb Review Retrieval Threads time: ")
            println(System.currentTimeMillis() - initTime)
        }

        return reviews
    }

    //TODO: Make the dude include it in the api
    int getReviewsNumberUsingScrapping(Movie movie) {
        println movie.imdbId
        String url = "http://www.imdb.com/title/" + movie.imdbId
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla").timeout(600000).get()

        Element reviewsNumberElement = doc.select(".star-box-details > br + a > span").first()
        if(reviewsNumberElement != null)
        {
            def reviewsNumberString = reviewsNumberElement.ownText().split()[0]
            NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
            Long parsedReviewsNumber = (Long) format.parse(reviewsNumberString);
            return parsedReviewsNumber.toInteger()
        }
        else return 0

    }

    JSONElement doRequest(String url) {
        HttpGet httpGet = new HttpGet(url)
        HttpResponse response = httpclient.execute(httpGet)
        def entity = response.getEntity()
        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()))
        def jsonResponse = JSON.parse(entity.getContent(), "UTF-8")
        return jsonResponse
    }

    HashMap<String, String> constructMap() {
        HashMap<String, String> map = new HashMap<String, String>()

        map.put("imdb_id", "imdbId")
        map.put("rated", "mpaa_rating")
        map.put("rating", "ratings")
        map.put("plot", "synopsis")
        map.put("runtime", "extendedRuntimes")

        return map
    }


}
