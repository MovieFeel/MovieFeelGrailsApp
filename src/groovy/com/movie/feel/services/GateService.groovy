package com.movie.feel.services

import com.movie.feel.Movie
import com.movie.feel.Review
import gate.AnnotationSet
import gate.Corpus
import gate.CorpusController
import gate.Document
import gate.Gate
import gate.Utils
import gate.creole.ANNIEConstants
import gate.creole.ExecutionException
import gate.creole.ResourceInstantiationException
import gate.creole.SerialAnalyserController
import gate.util.persistence.PersistenceManager
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import gate.persist.SerialDataStore

import java.nio.ByteBuffer
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/21/13
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
class GateService {

    def static fileExportService
    def static grailsApplication
    private static boolean isInitialized
    private static final Logger log = Logger.getLogger(GateService.class);

    class ratingElements {
        public int rating
        public int anger
        public int joy
        public int fear
        public int surprise
        public int disgust
    }

    /**
     * Atomic counter that we use to obtain a unique ID for each handler
     * instance.
     */
    private static AtomicInteger nextId = new AtomicInteger(1)

    /**
     * The ID of this handler instance.
     */
    private int handlerId;

    /**
     * The application that will be run.
     */
    private Object application;

    /**
     * A corpus that will be used to hold the document being processed.
     */
    private Corpus corpus;

    /**
     * Set the application that will be run over the documents.
     */
    public void setApplication(SerialAnalyserController application) {
        this.application = application;
    }

    public GateService() {
        isInitialized = false
    }

    public void InitGate() {
        def realPath = grailsApplication.config.moviefeel.gateLocation
        Gate.pluginsHome = new File(realPath + "/gate-files/plugins")

        Gate.gateHome = new File(realPath + "/gate-files/")
        Gate.siteConfigFile = new File(realPath + "/gate-files/gate.xml")
        Gate.userConfigFile = new File(realPath + "/gate-files/user-gate.xml")
        Gate.init()
        // load ANNIE as an application from a gapp file
        // application =
        //    PersistenceManager.loadObjectFromFile(new File(new File(
        //              Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
        //               ANNIEConstants.DEFAULT_FILE));

        // load the saved application
        application =
            (CorpusController) PersistenceManager.loadObjectFromFile(
                    new File(realPath + "/gate-files/gateApp.xgapp"))

        handlerId = nextId.getAndIncrement()
        log.info("init() for GateHandler " + handlerId);
        // create a corpus and give it to the controller
        corpus = gate.Factory.newCorpus("trial")
        application.setCorpus(corpus)
        isInitialized = true
    }


    public void cleanup() throws Exception {
        log.info("cleanup() for GateHandler " + handlerId);
        gate.Factory.deleteResource(corpus);
        gate.Factory.deleteResource(application);
    }

    public String anotateReviews(Movie movie, List<Review> reviews)
    {
        if(!isInitialized)
            InitGate()

        String mime = "text/plain";
        List<Document> documents = new ArrayList<Document>()
        for (Review review : reviews)
        {
            try
            {
                documents.add((Document)gate.Factory.createResource("gate.corpora.DocumentImpl",
                        Utils.featureMap("stringContent", review.quote, "mimeType", mime)))
            }
            catch(ResourceInstantiationException e) {
                return failureMessage("Could not create GATE document for input text")
            }
        }

        try {
            corpus.addAll(documents)
            application.execute();

            //fileExportService.exportProcessedReviewsToFiles(corpus)
            processReviews(movie, corpus)

            return successMessage(documents)
            log.info("Application completed")
        }
        catch(ExecutionException e) {
            return failureMessage("Error occurred which executing GATE application");
        }
        finally {
            // remember to do the clean-up tasks in a finally
            corpus.clear()
            for(Document document : documents)
                gate.Factory.deleteResource(document)
        }
    }

    /**
     * Render the document's features in an HTML table.
     */
    private String successMessage(List<Document> documents)
    throws IOException {
        String response = "";
        response+="<h1>Documents features: GATE handler " + handlerId + "</h1>";
        for (Document doc : documents)
        {
            response+="<table>";
            response+="<tr><td><b>Name</b></td><td><b>Value</b></td></tr>";
            for(Map.Entry<Object, Object> entry : doc.getFeatures().entrySet()) {
                response+="<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>";
            }
            response+="</table>";
            response+="<br>";
            response+="<br>";
            response+="<br>";
        }

        return response;
    }


    public String testGate(String text) {
        if(!isInitialized)
            InitGate()

        log.info("Handler " + handlerId + " handling request");
        // the form also allows you to provide a mime type
        String mime = "text/plain";

        Document doc = null;
        try {
            log.debug("Creating document");
            doc = (Document)gate.Factory.createResource("gate.corpora.DocumentImpl",
                    Utils.featureMap("stringContent", text, "mimeType", mime));
        }
        catch(ResourceInstantiationException e) {
            return failureMessage("Could not create GATE document for input text");
        }
        try {
            corpus.add(doc);
            log.info("Executing application");
            application.execute();
            return successMessage(doc);
            log.info("Application completed");
        }
        catch(ExecutionException e) {
            return failureMessage("Error occurred which executing GATE application");
        }
        finally {
            // remember to do the clean-up tasks in a finally
            corpus.clear();
            log.info("Deleting document");
            gate.Factory.deleteResource(doc);
        }
    }


    /**
     * Render the document's features in an HTML table.
     */
    private String successMessage(Document doc)
    throws IOException {
        String response = "";
        response+="<h1>Document features: GATE handler " + handlerId + "</h1>";
        response+="<table>";
        response+="<tr><td><b>Name</b></td><td><b>Value</b></td></tr>";
        for(Map.Entry<Object, Object> entry : doc.getFeatures().entrySet()) {
            response+="<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>";
        }
        response+="</table>";
        return response;
    }

    /**
     * Simple error handler - you would obviously use something more
     * sophisticated in a real application.
     */
    private String failureMessage() throws IOException {
        String response = "";
        response+="<h1>Error in GATE handler " + handlerId + "</h1>";
        return response;
    }

    public processReviews(Movie movie, Corpus corpus) {
        def reviews = new ArrayList<ratingElements>();
        corpus.each   {

            def annotationSet = ((AnnotationSet)it.namedAnnotationSets.get("Output"))
            def annotationJoy = annotationSet.find {it.features.find{it.key.equals("joy")}}.features.get("joy")
            def annotationAnger = annotationSet.find {it.features.find{it.key.equals("anger")}}.features.get("anger")
            def annotationDisgust = annotationSet.find {it.features.find{it.key.equals("disgust")}}.features.get("disgust")
            def annotationSurprise = annotationSet.find {it.features.find{it.key.equals("surprise")}}.features.get("surprise")
            def annotationFear = annotationSet.find {it.features.find{it.key.equals("fear")}}.features.get("fear")
            def annotationRating = annotationSet.find {it.features.find{it.key.equals("rating")}}.features.get("rating")

            if(annotationSet != null)
            {
                def ratingElements = new ratingElements()
                ratingElements.rating = annotationRating as int
                ratingElements.anger = annotationAnger as int
                ratingElements.disgust = annotationDisgust as int
                ratingElements.joy = annotationJoy as int
                ratingElements.fear = annotationFear as int
                ratingElements.surprise = annotationSurprise as int
                reviews.add(ratingElements)
            }
        }

        def totalRating = 0
        def totalAnger = 0
        def totalDisgust = 0
        def totalSurprise = 0
        def totalJoy = 0
        def totalFear = 0

        reviews.each
        {
            totalRating+= it.rating
            totalAnger+= it.anger
            totalDisgust+= it.disgust
            totalSurprise+= it.surprise
            totalJoy+= it.joy
            totalFear+= it.fear
        }

        movie.angerRating = (totalAnger / reviews.size()).toString()
        movie.joyRating = (totalJoy / reviews.size()).toString()
        movie.fearRating = (totalFear / reviews.size()).toString()
        movie.disgustRating = (totalDisgust / reviews.size()).toString()
        movie.surpriseRating = (totalSurprise / reviews.size()).toString()
        movie.processedRating = (totalRating / reviews.size()).toString()

        if(movie.validate())
           movie.save()
    }
}
