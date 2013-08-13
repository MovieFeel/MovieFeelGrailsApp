import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi

// Place your Spring DSL code here
beans = {

    retrievalService(com.movie.feel.RetrievalService) {

        rottenTomatoesApi = RottenTomatoesApi.getInstance("apikey=hwwmxfuufhtukd3y8s4vwncu", 50, 50)
        imdbApi = ImdbApi.getInstance(10, "full")

    }

}
