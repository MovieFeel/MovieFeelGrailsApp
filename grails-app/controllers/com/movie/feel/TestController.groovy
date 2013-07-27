package com.movie.feel

class TestController {

    def retrievalService

    def index() {

        render view: "index", model: [results: null, searchValue: ""]

    }

    def simpleSearch(String searchValue) {

        def results = retrievalService.getReviewsForMovie(searchValue)

        render view: "index", model: [results: results, searchValue: searchValue]
    }

}
