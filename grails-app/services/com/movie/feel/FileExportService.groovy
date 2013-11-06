package com.movie.feel

import gate.AnnotationSet
import gate.Corpus

import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class FileExportService {

    def grailsApplication

    public exportReviewsToFiles(Movie movie) {
        String exportRoot = grailsApplication.config.moviefeel.output

        ByteBuffer buffer = ByteBuffer.allocate(1000000)

        movie.reviews.each {
            File target = new File(exportRoot + movie.title + "/" + it.source + "/" + it.id + ".txt")
            File parent = target.getParentFile()
            if (!parent.exists() && !parent.mkdirs()) {

            }
            FileOutputStream fout = new FileOutputStream(target)
            FileChannel fc = fout.getChannel()

            for (int i = 0; i < it.quote.length(); ++i) {
                buffer.put(it.quote[i].getBytes())
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

        if (!parent.exists() && !parent.mkdirs()) {

        }

        FileOutputStream fout = new FileOutputStream(target)
        FileChannel fc = fout.getChannel()
        ByteBuffer buffer = ByteBuffer.allocate(10000000)

        corpus.each {

            def contentStream = it.getContent().toString()
            def test = ((AnnotationSet) it.namedAnnotationSets.get("Test"))
            def annotation = test.find { it.type.contentEquals("paragraph") }
            def ratingStream = annotation.features.get("rating").toString()
            def output = (contentStream + " - " + ratingStream + "\n").bytes
            for (int i = 0; i < output.size(); ++i) {
                buffer.put(output[i])
            }

            buffer.flip()
            fc.write(buffer)

            buffer.clear()
        }
    }

    public exportReviewsToFiles(String movieTitle, String source, List<Review> reviews) {
        String exportRoot = grailsApplication.config.moviefeel.output

        ByteBuffer buffer = ByteBuffer.allocate(1000000)

        reviews.each {

            String title = movieTitle.replaceAll("\\W+", "");
            String critic = it.critic.replaceAll("\\W+", "");
            File target = new File(exportRoot + title + "/" + it.source + "/" + critic + ".txt")
            File parent = target.getParentFile()
            if (!parent.exists() && !parent.mkdirs()) {

            }
            FileOutputStream fout = new FileOutputStream(target)
            FileChannel fc = fout.getChannel()
            if (it.quote != null) {
                byte[] quoteStream = it.quote.bytes
                for (int i = 0; i < quoteStream.size(); ++i) {
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

        reviews.each {

            String title = movieTitle.replaceAll("\\W+", "");
            String critic = it.critic.replaceAll("\\W+", "");

            if (it.quote != null && it.rating != null) {
                File target = new File(exportRoot + title + "Rated" + "/" + it.source + "/" + critic + ".txt")
                File parent = target.getParentFile()
                if (!parent.exists() && !parent.mkdirs()) {

                }
                FileOutputStream fout = new FileOutputStream(target)
                FileChannel fc = fout.getChannel()


                byte[] quoteStream = it.quote.bytes
                for (int i = 0; i < quoteStream.size(); ++i) {
                    buffer.put(quoteStream[i])
                }

                byte[] ratingStream = ("= Rating-" + it.rating).bytes

                for (int i = 0; i < ratingStream.size(); ++i) {
                    buffer.put(ratingStream[i])
                }

                buffer.flip()

                fc.write(buffer)
                buffer.clear()
            }
        }
    }
}
