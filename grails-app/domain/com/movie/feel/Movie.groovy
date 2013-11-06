package com.movie.feel

class Movie {

    String rottenTomatoId
    String imdbId
    String title
    String year
    String mpaa_rating
    String critics_consensus
    String synopsis

    // all 3 are actually HashMaps<String,String> and must be taken accordingly.
    String release_dates
    String ratings
    String posters

    String processedRating
    String fearRating
    String angerRating
    String joyRating
    String surpriseRating
    String disgustRating

    static hasMany = [reviews: Review]

    static constraints = {
        reviews lazy: false
        mpaa_rating nullable: true
        rottenTomatoId nullable: true
        imdbId nullable: true
        ratings nullable: true
        posters nullable: true
        release_dates nullable: true
        synopsis nullable: true
        critics_consensus nullable: true
        critics_consensus(length: 1..2000)
        release_dates(length: 1..2000)
        ratings(size: 1..2000)
        posters(size: 1..2000)
        synopsis(size: 1..2000)

        // for now
        processedRating nullable: true
        fearRating nullable: true
        angerRating nullable: true
        joyRating nullable: true
        surpriseRating nullable: true
        processedRating nullable: true
        disgustRating nullable: true
    }

    static mapping = {
        reviews lazy: false, cascade: "all,delete-orphan"
    }

}
