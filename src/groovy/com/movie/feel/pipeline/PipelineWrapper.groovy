package com.movie.feel.pipeline

import com.movie.feel.helpers.Constants
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

    String state
    String status
    def notificationService

    public void startPipeline(String title) {
        this.state = Constants.STATE_IDLE
    }


    public void nextState() {
        switch (state) {
            case Constants.STATE_IDLE:

                setState(Constants.STATE_SEARCH)

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
        return this.state
    }

    @Override
    void update(Subject_I o) {
        status = o.getStatus()
        println("Status changed to: " + status)
    }
}
