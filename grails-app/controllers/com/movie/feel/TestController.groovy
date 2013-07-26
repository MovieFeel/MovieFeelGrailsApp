package com.movie.feel

class TestController {

    def retrievalService

    def index() {

        retrievalService.searchForMovie("Toy Story 3")

    }
}
