package com.movie.feel

import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class RetrievalServiceTests {

    def retrievalService

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testMovieRetrieval() {
        def toyStoryResults = retrievalService.searchForMovie("Toy Story 3")
        def lordOfTheRingsResults = retrievalService.searchForMovie("Lord of the Rings")
        def universalSoldierResults = retrievalService.searchForMovie("Universal Soldier")

        assertTrue(toyStoryResults.size() == 1)
        assertTrue(lordOfTheRingsResults.size() == 10)
        assertTrue(universalSoldierResults.size() == 7)

        assertTrue(toyStoryResults.get(0).title == "Toy Story 3")
        assertTrue(lordOfTheRingsResults.get(0).title == "The Lord of the Rings: The Return of the King")
        assertTrue(universalSoldierResults.get(0).year == "1992")
        assertNotNull(universalSoldierResults.get(0).mpaa_rating)

        def posters = toyStoryResults.get(0).posters
        def map = new GroovyShell().evaluate(posters)

        assertTrue(map instanceof LinkedHashMap)
    }

    @Test
    void testReviewRetrieval() {
        def toyStoryMovies = retrievalService.searchForMovie("Toy Story 3")
        def toyStoryReviews = retrievalService.getReviewsForMovie("Toy Story 3")

        assertTrue(toyStoryReviews.size() == 46)

    }
}
