package com.movie.feel

import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.Utils;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

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
