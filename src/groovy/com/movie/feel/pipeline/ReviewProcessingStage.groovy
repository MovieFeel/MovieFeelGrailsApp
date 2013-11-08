package com.movie.feel.pipeline

import com.movie.feel.Review
import com.movie.feel.helpers.CurrentUserData
import com.movie.feel.interfaces.pipeline.ReviewProcessingStage_I
import com.movie.feel.services.GateService

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/28/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
class ReviewProcessingStage extends AbstractStage implements ReviewProcessingStage_I {

    GateService gate

    public ReviewProcessingStage() {
        gate = new GateService()
    }

    @Override
    public void startStage() {
        List<Review> reviews = CurrentUserData.movie.reviews.toList()

        if(reviews != null && reviews.size() > 0)
        {
            CurrentUserData.output = gate.anotateReviews(CurrentUserData.movie.getTitle(), reviews)
        }
    }
}
