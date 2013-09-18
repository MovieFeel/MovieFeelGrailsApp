package com.movie.feel

import com.movie.feel.helpers.CurrentUserData
import com.movie.feel.pipeline.MovieRetrieverStage
import com.movie.feel.pipeline.ReviewProcessingStage
import com.movie.feel.pipeline.ReviewRetrieverStage
import com.movie.feel.services.GateService
import weka.classifiers.trees.J48

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertTrue;

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
        MovieRetrieverStage mrs = new MovieRetrieverStage()
        mrs.startStage("tt0435761")
        ReviewRetrieverStage rrs = new ReviewRetrieverStage()
        rrs.startStage()
        ReviewProcessingStage rps = new ReviewProcessingStage()
        rps.startStage()
        def outputGate = CurrentUserData.output
        render view: "index", model: [ inputGate: inputGate, outputGate: outputGate]
    }
}
