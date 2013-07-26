package com.movie.feel

import static org.junit.Assert.*
import org.junit.*

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

        Assert.assertTrue(toyStoryResults.size() == 1)
        Assert.assertTrue(lordOfTheRingsResults.size() == 10)
        Assert.assertTrue(universalSoldierResults.size() == 7)

        Assert.assertTrue(toyStoryResults.get(0).title == "Toy Story 3")
        Assert.assertTrue(lordOfTheRingsResults.get(0).title == "The Lord of the Rings: The Return of the King")
        Assert.assertTrue(universalSoldierResults.get(0).year == "1992")
        Assert.assertNotNull(universalSoldierResults.get(0).mpaa_rating)

        def posters = toyStoryResults.get(0).posters.replace("}", "]")
        posters = posters.replace("{", "[")
        def map = new GroovyShell().evaluate(posters)

        Assert.assertTrue(map instanceof LinkedHashMap)

    }
}
