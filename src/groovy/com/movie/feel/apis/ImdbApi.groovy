package com.movie.feel.apis

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.interfaces.MovieSitesApi_I
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONElement

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
    }

    public static ImdbApi getInstance(int pageLimit, String plotType) {
        if (imdbApi == null)
            imdbApi = new ImdbApi(pageLimit, plotType)
        return imdbApi
    }

    @Override
    List<Movie> searchForMovieByTitle(String title) {

        String URL = "http://mymovieapi.com/?title=" + title + "&plot=" + plotType + "&limit=" + pageLimit
        List<Movie> movies = new ArrayList<Movie>()

        // this will contain an array list of json elements
        def jsonResponse = doRequest(URL)



        return null
    }

    @Override
    List<Review> getReviewsForMovie(Movie movie) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
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
