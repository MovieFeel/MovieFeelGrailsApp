package com.movie.feel

import grails.converters.JSON

class RestController {

    def restService
    def retrievalService
    /**
     * Returns all the titles of the movies currently stored in the database with the format "Title + (Year)"
     */
    def getAllMovieTitles() {
        if (Movie.count() < 1)
            retrievalService.dummyMovies()
        render restService.getAllMovieTitles() as JSON
    }

    def getInitialMovieDetailsForTitle(String title) {
        render restService.getInitialMovieDetailsForTitle(title) as JSON
    }

    def getMovieRating(String title) {
        render restService.getMovieRating(title) as JSON
    }
}
