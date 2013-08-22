package com.movie.feel

import com.movie.feel.services.GateService;

class TestController {

    def retrievalService

    def index() {

        render view: "index", model: [results: null, searchValue: "", inputGate: "", outputGate: ""]

    }

    def simpleSearch(String searchValue) {

        def results = retrievalService.getReviewsForMovie(searchValue)

        render view: "index", model: [results: results, searchValue: searchValue]
    }

    def gateTest(String inputGate) {
        def outputGate = new GateService().testGate(inputGate);

        render view: "index", model: [ inputGate: inputGate, outputGate: outputGate]
    }
}
