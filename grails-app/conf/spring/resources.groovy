import com.movie.feel.apis.ImdbApi
import com.movie.feel.apis.RottenTomatoesApi
import com.movie.feel.helpers.Constants

// Place your Spring DSL code here
beans = {

    retrievalService(com.movie.feel.RetrievalService) {

        rottenTomatoesApi = RottenTomatoesApi.getInstance(Constants.RottenTomatoesApiKey, Constants.RottenTomatoesMoviePageLimit, Constants.RottenTomatoesReviewPageLimit)
        imdbApi = ImdbApi.getInstance(Constants.RottenTomatoesMoviePageLimit, Constants.IMDB_PLOT_TYPE)

    }

}
