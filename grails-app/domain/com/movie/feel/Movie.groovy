package com.movie.feel

class Movie {

    String rottenTomatoId
    String imdbId
    String title
    String rating
    String synopsis
    String plot_simple
    String year

    // all 3 are actually HashMaps<String,String> and must be taken accordingly.
    String release_dates
    String ratings
    String posters
    String genres
    String actors
    String directors

    String processedRating
    String fearRating
    String angerRating
    String joyRating
    String surpriseRating
    String disgustRating

    static hasMany = [reviews: Review]

    static constraints = {
        reviews lazy: false
        rating nullable: true
        rottenTomatoId nullable: true
        imdbId nullable: true
        ratings nullable: true
        posters nullable: true
        release_dates nullable: true
        synopsis nullable: true
        plot_simple nullable: true
        actors nullable: true
        directors nullable: true
        year nullable: true
        genres nullable: true
        release_dates(length: 1..2000)
        ratings(size: 1..2000)
        posters(size: 1..2000)
        actors(size: 1..2000)
        directors(size: 1..2000)
        genres(size: 1..2000)
        synopsis(size: 1..2000)

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
