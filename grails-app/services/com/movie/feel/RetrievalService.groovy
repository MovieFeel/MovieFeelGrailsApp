package com.movie.feel

import com.google.gson.GsonBuilder
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.interfaces.MovieSitesApi_I
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import com.google.gson.Gson
import grails.converters.*;

class RetrievalService {

    MovieSitesApi_I rottenTomatoesApi
    MovieSitesApi_I someOtherApi

    List<Movie> searchForMovie(String title) {

        String[] titles = title.split(' ')
        String urlTitle = ""
        for (int i = 0; i < titles.length - 1; i++) {
            urlTitle += titles[i] + "+"
        }
        urlTitle += titles[titles.length - 1]

        def rottenTomatoMovie = rottenTomatoesApi.searchForMovieByTitle(urlTitle)

        return rottenTomatoMovie
    }
}