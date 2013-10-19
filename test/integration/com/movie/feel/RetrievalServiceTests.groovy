package com.movie.feel

import com.movie.feel.services.FileExportService
import com.movie.feel.services.GateService
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
    void testRottenTomatoesMovieRetrieval() {
        def toyStoryResults = retrievalService.searchForMovie("Toy Story 3")
        def lordOfTheRingsResults = retrievalService.searchForMovie("Lord of the Rings")
        def universalSoldierResults = retrievalService.searchForMovie("Universal Soldier")

        assertTrue(toyStoryResults.size() == 1)
        assertTrue(lordOfTheRingsResults.size() == 15)
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
    void testRottenTomatoesReviewRetrieval() {
        def toyStoryMovies = retrievalService.searchForMovie("Toy Story 3")
        def toyStoryReviews = retrievalService.getReviewsForMovie("Toy Story 3")

        assertTrue(toyStoryReviews.size() > 0)
    }

    @Test
    void testImdbMovieRetrieval() {
        def lordOfTheRingsResults = retrievalService.searchForImdbMovie("Lord of the Rings")
        assertTrue(lordOfTheRingsResults.size() > 0)
    }

    @Test
    void testImdbMovieReviewRetrieval() {
        def lordOfTheRingsResults = retrievalService.searchForImdbMovie("Lord of the Rings")
        def reviews = retrievalService.getReviewsForMovieImdb("Lord of the Rings")
        assert(reviews.size() > 0)
    }

    @Test
    void testRottenTomatoesReviewExport() {
        def toyStoryMovies = retrievalService.searchForMovie("Toy Story 3")
        def toyStoryReviews = retrievalService.getReviewsForMovie("Toy Story 3")
        FileExportService.exportReviewsToFiles("Toy Story 3", "RottenTomatoes", toyStoryReviews)
        assertTrue(toyStoryReviews.size() > 0)
    }

    @Test
    void testImdbReviewExport() {
        def toyStoryMovies = retrievalService.searchForImdbMovie("Scary Movie")
        def toyStoryReviews = retrievalService.getReviewsForMovieImdb(toyStoryMovies.get(0).getTitle())
        FileExportService.exportReviewsToFiles(toyStoryMovies.get(0).title, "Imdb", toyStoryReviews)
        assertTrue(toyStoryReviews.size() > 0)
    }

    @Test
    void testImdbRatedReviewExport() {
        def toyStoryMovies = retrievalService.searchForImdbMovie("Scary Movie")
        def toyStoryReviews = retrievalService.getReviewsForMovieImdb(toyStoryMovies.get(0).getTitle())
        FileExportService.exportRatedReviewsToFiles(toyStoryMovies.get(0).title, "Imdb", toyStoryReviews)
        assertTrue(toyStoryReviews.size() > 0)
    }


    @Test
    void testRottenTomatoesReviewProcessing() {
        def toyStoryMovies = retrievalService.searchForMovie("Toy Story 3")
        def toyStoryReviews = retrievalService.getReviewsForMovie("Toy Story 3")

        GateService gate = new GateService()
        gate.anotateReviews(toyStoryReviews)
    }

    @Test
    void testRottenTomatoesReviewProcessingBadMovie() {
        def toyStoryMovies = retrievalService.searchForMovie("Scary Movie 5")
        def toyStoryReviews = retrievalService.getReviewsForMovie("Scary Movie 5")

        GateService gate = new GateService()
        gate.anotateReviews(toyStoryReviews)
    }
}
