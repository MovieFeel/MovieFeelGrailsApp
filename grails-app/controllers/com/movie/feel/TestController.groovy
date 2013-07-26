package com.movie.feel

import grails.converters.JSON

class TestController {

    def retrievalService

    def index() {

        retrievalService.searchForMovie("Toy Story 3")
        redirect(action: "simpleSearch")
        // render retrievalService.getReviewsForMovie("Toy Story 3") as JSON

    }

    def simpleSearch() {


        render(view: "SimpleSearch")
    }

}
