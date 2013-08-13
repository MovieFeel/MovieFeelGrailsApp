package com.movie.feel

class Movie {

    String rottenTomatoId
    String imdbId
    String title
    String year
    String mpaa_rating
    String runtime
    String critics_consensus
    String synopsis

    // all 3 are actually HashMaps<String,String> and must be taken accordingly.
    String release_dates
    String ratings
    String posters
    ArrayList<String> extendedRuntimes

    // Todo: think of better name
    // Todo: we're gonna have a 'feelings' enum, right?
    String generalFeeling

    static hasMany = [reviews: Review]

    static constraints = {
        reviews lazy: false
        runtime nullable: true
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
        generalFeeling nullable: true
    }

    static mapping = {
        reviews lazy: false, cascade: "all,delete-orphan"
    }

}
