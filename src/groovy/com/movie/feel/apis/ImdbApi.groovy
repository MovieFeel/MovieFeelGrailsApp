package com.movie.feel.apis

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.interfaces.MovieSitesApi_I
import grails.converters.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

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

    public static ImdbApi getInstance(){
        if(imdbApi==null)
            imdbApi = new ImdbApi(Constants.RottenTomatoesMoviePageLimit, "full")
        return imdbApi
    }

    @Override
    List<Movie> searchForMovieByTitle(String title) {

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
                        else if (jsonElement.getKey().equals("runtime")) {
                            ArrayList<String> runtimes = (ArrayList<String>) jsonElement.getValue()

                            m.extendedRuntimes = runtimes
                            String firstRutime = m.extendedRuntimes.get(0).split(" ")[0]
                            m.runtime = firstRutime.toString()
                        } else {
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
