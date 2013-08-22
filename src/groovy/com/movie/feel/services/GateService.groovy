package com.movie.feel.services

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
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 8/21/13
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
class GateService {

    private static final Logger log = Logger.getLogger(GateService.class);

    /**
     * Atomic counter that we use to obtain a unique ID for each handler
     * instance.
     */
    private static AtomicInteger nextId = new AtomicInteger(1);

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

    private GateService() {
        Gate.init();
         // load ANNIE as an application from a gapp file
         application =
            PersistenceManager.loadObjectFromFile(new File(new File(
                      Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
                        ANNIEConstants.DEFAULT_FILE));
        handlerId = nextId.getAndIncrement();
        log.info("init() for GateHandler " + handlerId);
        // create a corpus and give it to the controller
        corpus = gate.Factory.newCorpus("webapp corpus");
        ((SerialAnalyserController)application).setCorpus(corpus);
    }


    public void cleanup() throws Exception {
        log.info("cleanup() for GateHandler " + handlerId);
        gate.Factory.deleteResource(corpus);
        gate.Factory.deleteResource(application);
    }

    public String testGate(String text) {
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
}
