import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Constants

// Place your Spring DSL code here
beans = {

    retrievalService(com.movie.feel.RetrievalService) {

        rottenTomatoesApi = RottenTomatoesApi.getInstance("apikey=hwwmxfuufhtukd3y8s4vwncu", 50, 50)
        imdbApi = ImdbApi.getInstance(Constants.RottenTomatoesMoviePageLimit, "full")

    }

    MovieRetrieverStage(com.movie.feel.pipeline.MovieRetrieverStage){
        rottenTomatoesApi = RottenTomatoesApi.getInstance("apikey=hwwmxfuufhtukd3y8s4vwncu", 50, 50)
        imdbApi = ImdbApi.getInstance(Constants.RottenTomatoesMoviePageLimit, "full")
    }

}
