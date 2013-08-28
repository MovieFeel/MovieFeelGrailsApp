package com.movie.feel.helpers

/**
 * Created with IntelliJ IDEA.
 * User: alexcosma
 * Date: 26.07.13
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
class Constants {

    public static final String RottenTomatoes = "Rotten Tomatoes"
    public static final String IMDB = "IMDB"

    public static final int RottenTomatoesMoviePageLimit = 50
    public static final int RottenTomatoesReviewPageLimit = 50
    public static final int RottenTomatoesThreadSplitFactor = 5
    public static final String RottenTomatoesApiKey = "apikey=hwwmxfuufhtukd3y8s4vwncu"

    public static final String IMDB_PLOT_TYPE = "full"


    public static final int NUMBER_OF_APIS = 2;

    // Pipeline Stages Status
    public static final String STATUS_EMPTY = "PIPELINE_STAGE_STATUS_EMPTY"
    public static final String STATUS_RETRIEVER_STAGE_SUCCESS = "PIPELINE_RETRIEVER_STAGE_SUCESS"
    public static final String STATUS_RETRIEVER_STAGE_FAIL = "PIPELINE_RETRIEVER_STAGE_FAIL"
    // etc

    // Pipeline stages
    public static final String STAGE_SEARCH = "PIPELINE_STAGE_SEARCH"
    public static final String STAGE_MOVIE_RET = "PIPELINE_STAGE_MOVIE_RET"
    public static final String STAGE_REVIEW_RET = "PIPELINE_STAGE_REVIEW_RET"
    public static final String STAGE_REVIEW_PROC = "PIPELINE_STAGE_REVIEW_PROC"

    // Pipeline states
    public static final String STATE_IDLE = "PIPELINE_STATE_IDLE"
    public static final String STATE_SEARCH = "PIPELINE_STATE_SEARCH"
    public static final String STATE_MOVIE_RET = "PIPELINE_STATE_MOVIE_RET"
    public static final String STATE_REVIEW_RET = "PIPELINE_STATE_REVIEW_RET"
    public static final String STATE_REVIEW_PROC = "PIPELINE_STATE_REVIEW_PROC"

}
