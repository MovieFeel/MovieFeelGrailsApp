package com.movie.feel

class Review {
    /**
     * Source of the review (Rotten Tomatoes, IMDB, Twitter etc)
     */
    String source
    String critic
    String date
    String quote
    String freshness
    String publication
    String original_score
    String rating
    // actually hash map
    String links
    static belongsTo = [movie: Movie]

    public Review(String source,String critic,String date,String quote, String rating)
    {
        this.source = source
        this.critic = critic
        this.date = date
        this.quote = quote
        this.rating = rating
    }



    static constraints = {
        // place 'Anonymous' in case it is not available
        critic nullable: false
        quote nullable: false
        source nullable: false
        freshness nullable: true
        publication nullable: true
        original_score nullable: true
        rating nullable : true
        links nullable: true
        links(size: 1..2000)
        quote(size: 1..20000)
    }

}
