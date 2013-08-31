package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.Extras
import com.movie.feel.interfaces.observer.Observer_I
import com.movie.feel.interfaces.observer.Subject_I

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/28/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
class PipelineWrapper implements Observer_I {

    private static HashMap<String, AbstractStage> stages
    static {
        stages = new HashMap<String, AbstractStage>()
        stages[Constants.STAGE_SEARCH] = new SearchStage()
        stages[Constants.STAGE_MOVIE_RET] = new MovieRetrieverStage()
        stages[Constants.STAGE_REVIEW_RET] = new ReviewRetrieverStage()
        stages[Constants.STAGE_REVIEW_PROC] = new ReviewProcessingStage()
    }

    // results which will be set from the stages
    List<Movie> movies;
    List<Review> reviews;

    String state
    String status
    def notificationService

    String title

    public void startPipeline(String title) {
        this.state = Constants.STATE_IDLE
        this.title = Extras.formatTitle(title)
        // initializer for the observer
        Iterator<Map.Entry<String, AbstractStage>> stageIterator = stages.iterator()
        while (stageIterator.hasNext()) {
            AbstractStage stage = stageIterator.next().getValue()
            stage.addObserver(this)
        }
    }


    public void nextState() {
        switch (state) {
            case Constants.STATE_IDLE:

                setState(Constants.STATE_SEARCH)
                ((SearchStage)stages[Constants.STAGE_SEARCH]).searchForMovieByTitle(title)

                break;

            case Constants.STATE_SEARCH:

                resetStatus()
                sendNotificationForStatus()
                setState(Constants.STATE_MOVIE_RET)
                stages[Constants.STAGE_SEARCH].doSomething()


                break;

            case Constants.STATE_MOVIE_RET:

                sendNotificationForStatus()
                resetStatus()
                setState(Constants.STATE_REVIEW_RET)
                stages[Constants.STAGE_MOVIE_RET].doSomething()

                ((MovieRetrieverStage) stages[Constants.STAGE_MOVIE_RET]).startStage("Lord+of+the+Rings")

                break;

            case Constants.STATE_REVIEW_RET:

                sendNotificationForStatus()
                resetStatus()
                setState(Constants.STATE_REVIEW_PROC)
                stages[Constants.STAGE_REVIEW_RET].doSomething()

                break;

            case Constants.STATE_REVIEW_PROC:

                sendNotificationForStatus()
                resetStatus()
                setState(Constants.STATE_IDLE)
                stages[Constants.STAGE_REVIEW_PROC].doSomething()

                break;
        }
    }

    void resetStatus() {
        status = ""
    }

    void sendNotificationForStatus() {
        // notificationService.notifyUser("mockId", this.state + this.state)
    }

    void setState(String state) {
        this.state = state
    }

    String getState() {
        return this.state
    }

    void setStatus(String status) {
        this.state = status
    }

    String getStatus() {
        return this.status
    }

    void setMovies(List<Movie> movies) {
        this.movies = movies
    }

    void setReviews(List<Review> reviews) {
        this.reviews = reviews
    }

    @Override
    void update(Subject_I o) {
        status = o.getStatus()
        println("Status changed to: " + status)

        // TODO: do something! a stage has signaled its completion

        if (status == Constants.STATUS_SEARCH_STAGE_FOUND_MOVIES) {

            setMovies(stages[Constants.STAGE_SEARCH].movieResults)

        } else if (status == Constants.STATUS_SEARCH_STAGE_NOT_FOUND_MOVIES) {

        }
    }


}
