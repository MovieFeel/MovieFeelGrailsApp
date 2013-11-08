package com.movie.feel.responses

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 10/4/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
class InitialMovieDetails {

    String title
    JSONObject ratings
    String mpaa_rating
    String synopsis
    JSONObject posters
    JSONArray actors
    JSONArray genres
    JSONArray directors

}
