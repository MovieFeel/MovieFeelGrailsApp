package com.movie.feel.threads.imdb

import com.movie.feel.Movie
import com.movie.feel.Review
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.util.concurrent.CountDownLatch

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/29/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class MovieScrapper extends Thread {

    CountDownLatch latch
    Movie movie
    int startPosition
    int maxReviews
    List<Review> reviews

    public MovieScrapper(CountDownLatch latch,Movie movie,int startPosition, int maxReviews, List<Review> reviews) {
        this.latch = latch
        this.movie = movie
        this.startPosition = startPosition
        this.maxReviews = maxReviews
        this.reviews = reviews
    }

    @Override
    void run() {
        // automatically adds them to the movie as well
        reviews.addAll(getReviewsForMovie(movie,startPosition,maxReviews))
        latch.countDown()
    }

    List<Review> getReviewsForMovie(Movie movie,int startPosition,int reviewsLimit) {
        String URL = " http://www.imdb.com/title/" + movie.imdbId + "/reviews?count=" + reviewsLimit + "&start=" + startPosition
        return getReviewsUsingScrapping(URL,movie)
    }

    List<Review> getReviewsUsingScrapping(String url,Movie movie) {
        List<Review> reviewsList = new ArrayList<Review>()
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla").timeout(600000).maxBodySize(Integer.MAX_VALUE).get()

        Elements reviews = doc.select("#tn15content > div >  b + a, #tn15content > div > br + small ,#tn15content > div + p, #tn15content > div > img")
        for(int index = 0; index < reviews.size()-3; index+=3)
        {
            def rating
            if(reviews[index].tagName() == "img")
            {
                rating  = reviews[index].attr("alt")
                if(rating != null && rating.size() > 1)
                    rating = rating.substring(0,1)
                index++
            }
            def critic =  reviews[index].ownText()
            def date = reviews[index + 1].ownText()
            def quote = reviews[index + 2].ownText()


            def review = new Review("IMDB",critic,date,quote,rating)

            review.movie = movie
            review.validate()
            if (!review.hasErrors()) {
                reviewsList.add(review)
            }

        }
        return reviewsList
    }

}