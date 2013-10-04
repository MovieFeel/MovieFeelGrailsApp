package com.movie.feel.services

import com.movie.feel.Movie
import com.movie.feel.Review
import gate.AnnotationSet
import gate.Corpus

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

    def static grailsApplication

    public static exportReviewsToFiles(Movie movie) {
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
                buffer.put( it.quote[i])
            }

            buffer.flip()

           fc.write(buffer)
           buffer.clear()
        }
    }

    public static exportProcessedReviewsToFiles(String movieTitle, String source, Corpus corpus) {
        String exportRoot = grailsApplication.config.moviefeel.output

        File target = new File(exportRoot + movieTitle + "/" + "output" + ".txt")
        File parent = target.getParentFile()

        if(!parent.exists() && !parent.mkdirs())
        {

        }

        FileOutputStream fout = new FileOutputStream(target)
        FileChannel fc = fout.getChannel()
        ByteBuffer buffer = ByteBuffer.allocate(10000000)

        corpus.each   {

            def contentStream = it.getContent().toString()
            def test = ((AnnotationSet)it.namedAnnotationSets.get("Test"))
            def annotation = test.find {it.type.contentEquals("paragraph")}
            def ratingStream = annotation.features.get("rating").toString()
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

    public static exportReviewsToFiles(String movieTitle, String source, List<Review> reviews) {
        String exportRoot = grailsApplication.config.moviefeel.output

        ByteBuffer buffer = ByteBuffer.allocate(1000000)

        reviews.each   {
            File target = new File(exportRoot + movieTitle + "/" + it.source + "/" + it.critic + ".txt")
            File parent = target.getParentFile()
            if(!parent.exists() && !parent.mkdirs())
            {

            }
            FileOutputStream fout = new FileOutputStream(target)
            FileChannel fc = fout.getChannel()
            byte[] quoteSteam = it.quote.bytes
            for(int i=0; i< quoteSteam.size(); ++i)
            {
                buffer.put(quoteSteam[i])
            }

            buffer.flip()

            fc.write(buffer)
            buffer.clear()
        }
    }
}
