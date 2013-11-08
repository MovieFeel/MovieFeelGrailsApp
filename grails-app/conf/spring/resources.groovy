import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Constants

// Place your Spring DSL code here
beans = {

    retrievalService(com.movie.feel.RetrievalService) {
        rottenTomatoesApi = RottenTomatoesApi.getInstance(Constants.RottenTomatoesApiKey, Constants.RottenTomatoesMoviePageLimit, Constants.RottenTomatoesReviewPageLimit)
        imdbApi = ImdbApi.getInstance(Constants.RottenTomatoesMoviePageLimit, Constants.IMDB_PLOT_TYPE)

    }

    gateService(com.movie.feel.services.GateService) {
        grailsApplication = ref('grailsApplication')
        fileExportService = ref('fileExportService')
        // or use 'autowire'
    }

    reviewRetrieverStage(com.movie.feel.pipeline.ReviewRetrieverStage){
        fileExportService = ref('fileExportService')
    }

    pipelineWrapper(com.movie.feel.pipeline.PipelineWrapper) {
        fileExportService = ref('fileExportService')
    }

}
