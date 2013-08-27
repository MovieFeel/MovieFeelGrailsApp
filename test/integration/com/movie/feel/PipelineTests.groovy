package com.movie.feel

import com.movie.feel.pipeline.MovieRetrieverStage
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

    @Test
    void testFirstStage() {

        MovieRetrieverStage mrs = new MovieRetrieverStage()
        mrs.init()
        assertTrue(mrs.startStage("Lord+of+the+Rings") == null)
    }
}
