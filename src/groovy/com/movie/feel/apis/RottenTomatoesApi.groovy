package com.movie.feel.apis

import com.google.gson.GsonBuilder
import org.codehaus.groovy.grails.web.json.JSONObject
import com.movie.feel.Movie
import com.movie.feel.interfaces.MovieSitesApi_I
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

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
    final int pageLimit
    DefaultHttpClient httpclient

    private RottenTomatoesApi(String key, int pageLimit) {
        this.key = key
        this.pageLimit = pageLimit
        this.httpclient = new DefaultHttpClient()
    }


    static RottenTomatoesApi getInstance(String key, int pageLimit) {
        if (rottenTomatoesApi == null)
            rottenTomatoesApi = new RottenTomatoesApi(key, pageLimit)


        return rottenTomatoesApi
    }


    @Override
    List<Movie> searchForMovieByTitle(String title) {
        String URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?" + key + "&q=" + title + "&page_limit=" + pageLimit

        List<Movie> movies = new ArrayList<Movie>()

        HttpGet httpGet = new HttpGet(URL)
        HttpResponse response = httpclient.execute(httpGet)
        def entity = response.getEntity()
        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()))
        def jsonResponse = JSON.parse(entity.getContent(), "UTF-8")

        def jsonMovies = jsonResponse.movies
        JSONObject jsonMovie;
        for (int i = 0; i < jsonMovies.size(); i++) {
            jsonMovie = jsonMovies.getJSONObject(i)

            // automatically finds and fills the fields by their names
            // hint: will have to construct map out of it for IMDB, for example,
            // if the fields are not the same <=> normalize
            Movie movie = new Movie(jsonMovie)
            // these have to be explicit for some reason
            movie.release_dates = jsonMovie?.get("release_dates")
            movie.posters = jsonMovie?.get("posters")
            movie.ratings = jsonMovie?.get("ratings")
            movie.rottenTomatoId = jsonMovie?.get("id")

            movie.validate()
            if (!movie.hasErrors()) {
                movie.save()
                movies.add(movie)
            }
        }

        return movies  //To change body of implemented methods use File | Settings | File Templates.
    }

}
