package com.movie.feel

class Review {

    String critic
    String date
    String quote
    String freshness
    String publication
        String original_score
    // actually hash map
    String links

    static constraints = {
        freshness nullable: true
        publication nullable: true
        original_score nullable: true
        links nullable: true
    }

}
