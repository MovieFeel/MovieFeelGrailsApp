package com.movie.feel

import com.movie.feel.helpers.Constants
import com.movie.feel.pipeline.MovieRetrieverStage
import com.movie.feel.pipeline.PipelineWrapper
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/27/13
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
class PipelineTests {

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    /**
     * TODO: delete after implementing everything
     */
    @Test
    void testPipelineStageTransition() {

        PipelineWrapper pipeline = new PipelineWrapper()
        pipeline.startPipeline("Test")
        assertTrue(pipeline.getState() == Constants.STATE_IDLE)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_SEARCH)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_MOVIE_RET)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_REVIEW_RET)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_REVIEW_PROC)

    }

    @Test
    void initialTestStatus() {
        PipelineWrapper pipeline = new PipelineWrapper()
        pipeline.startPipeline("Test")
        assertTrue(pipeline.getState() == Constants.STATE_IDLE)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_INIT)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_SEARCH)

        pipeline.executeState()
        assertTrue(pipeline.getState() == Constants.STATE_MOVIE_RET)

        pipeline.executeState()
        assertTrue(pipeline.getStatus() == Constants.STATUS_RETRIEVER_STAGE_SUCCESS)
    }

    @Test
    void testFirstStage() {

        MovieRetrieverStage mrs = new MovieRetrieverStage()
        mrs.startStage()
        assertTrue(mrs.startStage("Lord of the Rings") == null)
    }
}
