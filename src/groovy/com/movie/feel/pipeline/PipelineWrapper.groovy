package com.movie.feel.pipeline

import com.movie.feel.Movie
import com.movie.feel.Review
import com.movie.feel.helpers.Constants
import com.movie.feel.helpers.CurrentUserData
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

    PipelineWrapper() {
        this.state =  Constants.STATE_IDLE
    }

    private static HashMap<String, AbstractStage> stages
    static {
        stages = new HashMap<String, AbstractStage>()
        stages[Constants.STAGE_SEARCH] = new SearchStage()
        stages[Constants.STAGE_MOVIE_RET] = new MovieRetrieverStage()
        stages[Constants.STAGE_REVIEW_RET] = new ReviewRetrieverStage()
        stages[Constants.STAGE_REVIEW_PROC] = new ReviewProcessingStage()
        stages[Constants.STAGE_REVIEW_WRITE] = new ReviewWritingStage()
        stages[Constants.STAGE_REVIEW_RESPONSE] = new ReviewResponseStage()
    }

    String state
    String status
    def notificationService

    String title


    public void startPipeline(String title) {
        if (this.state.contentEquals(Constants.STATE_IDLE)) {
            this.state = Constants.STATE_INIT
            this.title = Extras.formatTitle(title)
            // initializer for the observer
            Iterator<Map.Entry<String, AbstractStage>> stageIterator = stages.iterator()
            while (stageIterator.hasNext()) {
                AbstractStage stage = stageIterator.next().getValue()
                stage.addObserver(this)
            }
        }
    }


    public void executeState() {
        switch (state) {
            case Constants.STATE_IDLE:

                setState(Constants.STATE_INIT)

                break;

            case Constants.STATE_INIT:

                setState(Constants.STATE_SEARCH)
                ((SearchStage) stages[Constants.STAGE_SEARCH]).searchForMovieByTitle(title)

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
                stages[Constants.STAGE_REVIEW_WRITE].doSomething()

                break;

            case Constants.STATE_REVIEW_WRITE:

                sendNotificationForStatus()
                resetStatus()
                setState(Constants.STATE_REVIEW_RESPONSE)
                break;

            case Constants.STAGE_REVIEW_RESPONSE:

                sendNotificationForStatus()
                resetStatus()
                setState(Constants.STATE_IDLE)
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

    @Override
    void update(Subject_I o) {
        status = o.getStatus()
        println("Status changed to: " + status)

        // TODO: do something! a stage has signaled its completion

        if (status == Constants.STATUS_SEARCH_STAGE_PROCESS_MOVIE) {
            state = Constants.STAGE_MOVIE_RET
            ((MovieRetrieverStage) stages[Constants.STAGE_MOVIE_RET]).startStage(title)

        } else if (status == Constants.STATUS_SEARCH_STAGE_FOUND_MOVIE) {

            state = Constants.STATE_IDLE
            // exit pipeline, notify frontend

        } else if (status == Constants.STATUS_SEARCH_STAGE_GETTING_MOVIE_TITLES) {
            //Get titles
            status = Constants.STATUS_SEARCH_STAGE_PROCESS_MOVIE
            state = Constants.STATE_IDLE
            // exit pipeline, notify frontend

        }
    }


}
