package com.movie.feel

import gate.AnnotationSet
import gate.Corpus

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 10/1/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileExportService {

    def grailsApplication

    public exportReviewsToFiles(Movie movie) {
        String exportRoot = grailsApplication.config.moviefeel.output

        ByteBuffer buffer = ByteBuffer.allocate(1000000)

        movie.reviews.each   {
            File target = new File(exportRoot + movie.title + "/" + it.source + "/" + it.id + ".txt")
            File parent = target.getParentFile()
            if(!parent.exists() && !parent.mkdirs())
            {

            }
            FileOutputStream fout = new FileOutputStream(target)
            FileChannel fc = fout.getChannel()

            for(int i=0; i< it.quote.length(); ++i)
            {
                buffer.put( it.quote[i].getBytes())
            }

            buffer.flip()

            fc.write(buffer)
            buffer.clear()
        }
    }

    public exportProcessedReviewsToFiles(Corpus corpus) {
        String exportRoot = grailsApplication.config.moviefeel.output

        File target = new File(exportRoot + "/" + "output" + ".txt")
        File parent = target.getParentFile()

        if(!parent.exists() && !parent.mkdirs())
        {

        }

        FileOutputStream fout = new FileOutputStream(target)
        FileChannel fc = fout.getChannel()
        ByteBuffer buffer = ByteBuffer.allocate(10000000)

        corpus.each   {

            def contentStream = it.getContent().toString()
            def test = ((AnnotationSet)it.namedAnnotationSets.get("Output"))
            def annotation = test.find {it.type.contentEquals("paragraph")}
            String ratingStream = "";
            if(annotation != null)
            {
                ratingStream = annotation.features.get("rating").toString()
            }
            def output = (contentStream + " - " + ratingStream + "\n").bytes
            for(int i=0; i< output.size(); ++i)
            {
                buffer.put(output[i])
            }

            buffer.flip()
            fc.write(buffer)

            buffer.clear()
        }
    }

    public exportTrainingReviews(String movieTitle, List<Review> reviews) {
        String exportRoot = grailsApplication.config.moviefeel.output

        reviews.each   {

            String title = movieTitle.replaceAll("\\W+", "");
            String critic = it.critic.replaceAll("\\W+", "");

            if(it.quote != null && it.rating != null)
            {
                File target = new File(exportRoot + title + "Training" + "/" + critic + ".xml")
                File parent = target.getParentFile()
                if(!parent.exists() && !parent.mkdirs())
                {

                }
                FileOutputStream fos = new FileOutputStream(target)
                XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance()
                XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos)

                try
                {
                    writer.writeStartDocument("UTF-8", "1.0")
                    writer.writeStartElement("GateDocument")
                    writer.writeCharacters("\n")

                    writer.writeStartElement("GateDocumentFeatures")
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Feature")
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Name")
                    writer.writeAttribute("className","java.Lang.String")
                    writer.writeCharacters("MimeType")
                    //end Name
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Value")
                    writer.writeAttribute("className","java.Lang.String")
                    writer.writeCharacters("text/plain")
                    //end Value
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    //end Feature
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    //end GateDocumentFeatures
                    writer.writeEndElement()
                    writer.writeCharacters("\n")

                    writer.writeStartElement("TextWithNodes")
                    writer.writeCharacters("\n")
                    writer.writeEmptyElement("Node")
                    writer.writeAttribute("id","0")
                    //end Node
                    writer.writeCharacters("\n")
                    writer.writeCharacters(it.quote)
                    writer.writeCharacters("\n")
                    writer.writeEmptyElement("Node")
                    writer.writeAttribute("id",it.quote.length().toString())
                    //end Node
                    writer.writeCharacters("\n")
                    //end TextWithNodes
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    writer.writeCharacters("\n")
                    writer.writeCharacters("\n")

                    writer.writeStartElement("AnnotationSet")
                    writer.writeAttribute("Name","Key")
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Annotation")
                    writer.writeAttribute("Id","1")
                    writer.writeAttribute("Type", "paragraph")
                    writer.writeAttribute("StartNode","0")
                    writer.writeAttribute("EndNode",it.quote.length().toString())
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Feature")
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Name")
                    writer.writeAttribute("className","java.Lang.String")
                    writer.writeCharacters("rating")
                    //end Name
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Value")
                    writer.writeAttribute("className","java.Lang.String")
                    writer.writeCharacters(it.rating)
                    //end Value
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    //end Feature
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    //end Annotation
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    //end AnnotationSet
                    writer.writeEndElement()

                    writer.writeStartElement("AnnotationSet")
                    writer.writeAttribute("Name","Original markups")
                    writer.writeCharacters("\n")
                    writer.writeStartElement("Annotation")
                    writer.writeAttribute("Id","0")
                    writer.writeAttribute("Type", "paragraph")
                    writer.writeAttribute("StartNode","0")
                    writer.writeAttribute("EndNode",it.quote.length().toString())
                    writer.writeCharacters("\n")
                    //end Annotation
                    writer.writeEndElement()
                    writer.writeCharacters("\n")
                    //end AnnotationSet
                    writer.writeEndElement()

                    writer.writeCharacters("\n")
                    //end GateDocument
                    writer.writeEndElement()
                    writer.writeEndDocument()
                }
                catch(Exception e) {
                    //An exception means an incorrect input data, an since this means that the data is
                    //, it will probably not be want we want to export, so it is ignored.
                }
                writer.flush()
                writer.close()
                fos.close()
            }
        }
    }

    public exportReviewsToFiles(String movieTitle, String source, List<Review> reviews) {
        String exportRoot = grailsApplication.config.moviefeel.output

        ByteBuffer buffer = ByteBuffer.allocate(1000000)

        reviews.each   {

            String title = movieTitle.replaceAll("\\W+", "");
            String critic = it.critic.replaceAll("\\W+", "");
            File target = new File(exportRoot + title + "/" + it.source + "/" + critic + ".txt")
            File parent = target.getParentFile()
            if(!parent.exists() && !parent.mkdirs())
            {

            }
            FileOutputStream fout = new FileOutputStream(target)
            FileChannel fc = fout.getChannel()
            if(it.quote != null)
            {
                byte[] quoteStream = it.quote.bytes
                for(int i=0; i< quoteStream.size(); ++i)
                {
                    buffer.put(quoteStream[i])
                }
            }

            buffer.flip()

            fc.write(buffer)
            buffer.clear()
        }
    }

    public exportRatedReviewsToFiles(String movieTitle, String source, List<Review> reviews) {
        String exportRoot = grailsApplication.config.moviefeel.output

        ByteBuffer buffer = ByteBuffer.allocate(1000000)

        reviews.each   {

            String title = movieTitle.replaceAll("\\W+", "");
            String critic = it.critic.replaceAll("\\W+", "");

            if(it.quote != null && it.rating != null)
            {
                File target = new File(exportRoot + title + "Rated" + "/" + it.source + "/" + critic + ".txt")
                File parent = target.getParentFile()
                if(!parent.exists() && !parent.mkdirs())
                {

                }
                FileOutputStream fout = new FileOutputStream(target)
                FileChannel fc = fout.getChannel()


                byte[] quoteStream = it.quote.bytes
                for(int i=0; i< quoteStream.size(); ++i)
                {
                    buffer.put(quoteStream[i])
                }

                byte[] ratingStream = ("= Rating-" + it.rating).bytes

                for(int i=0; i< ratingStream.size(); ++i)
                {
                    buffer.put(ratingStream[i])
                }

                buffer.flip()

                fc.write(buffer)
                buffer.clear()
            }
        }
    }
}