package com.movie.feel.threads.rottentomatoes

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.Extras
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 9/2/13
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
class ReviewParser extends Thread  {

    int currentPage
    String key = "apikey=hwwmxfuufhtukd3y8s4vwncu"
    int reviewPageLimit = 50
    Movie movie
    List<Review> reviews
    CountDownLatch latch
    DefaultHttpClient httpclient

    public ReviewParser(CountDownLatch latch, Movie movie, int currentPage,List<Review> reviews)
    {
        this.movie = movie
        this.currentPage = currentPage
        this.latch = latch
        this.reviews = reviews
        this.httpclient = new DefaultHttpClient()
    }

    public ReviewParser(CountDownLatch latch, Movie movie, int currentPage,String key, int reviewPageLimit,List<Review> reviews)
    {
        this.movie = movie
        this.currentPage = currentPage
        this.latch = latch
        this.key =key
        this.reviewPageLimit = reviewPageLimit
        this.reviews = reviews
        this.httpclient = new DefaultHttpClient()
    }

    @Override
    public void run() {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + movie.rottenTomatoId + "/reviews.json?" + key + "&page_limit=" + reviewPageLimit +"&page=" +  currentPage +"&review_type=all"

        def jsonResponse = doRequest(URL)

        def jsonReviews = jsonResponse.reviews
        JSONObject jsonReview;
        if (jsonReviews) {
            for (int i = 0; i < jsonReviews.size(); i++) {
                jsonReview = jsonReviews.getJSONObject(i)

                Review review = new Review(jsonReview)
                review.date = Extras.formatDate(jsonReview?.get("date").toString(), Constants.RottenTomatoes)
                //review.rating = jsonReview?.get("original_score").toString().substring(0,1)
                review.movie = movie
                review.links = Extras.formatHashMap(jsonReview?.get("links")?.toString())
                review.source = Constants.RottenTomatoes
                if(review.validate())
                    reviews.add(review)
            }
        }

        latch.countDown();
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
