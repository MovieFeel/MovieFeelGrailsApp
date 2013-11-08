package com.movie.feel.helpers

import com.movie.feel.Review

import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: Darius
 * Date: 26.07.13
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
class Extras {

    /**
     * Format the string from JSON to correct HashMap string
     * @param s - the input string
     * @return formated hashmap string
     */
    static String formatHashMap(String s) {
        if (s != null) {
            String aux = s.replace("{", "[")
            aux = aux.replace("}", "]")
            return aux;
        } else return null
    }

    /**
     * Format the string from JSON to correct HashMap string
     * @param s - the input string
     * @return formated hashmap string
     */
    static String formatJSON(String s) {
        if (s != null) {
            String aux = s.replace("[", "{")
            aux = aux.replace("]", "}")
            aux = aux.replace(",\\", ",")
            return aux;
        } else return null
    }

    static String formatTitle(String title) {
        String[] titles = title.split(' ')
        String urlTitle = ""
        for (int i = 0; i < titles.length - 1; i++) {
            urlTitle += titles[i] + "+"
        }
        urlTitle += titles[titles.length - 1]
        return urlTitle
    }

    static Date formatDate(String date, String source) {
        if (date)
            if (source == Constants.RottenTomatoes) {
                return new SimpleDateFormat("yyyy-MM-d", Locale.ENGLISH).parse(date);
            }
        return null;
    }

    static ArrayList<Review> synchronizeLists(List<Review> imdbReviews, List<Review> rottenTomatoesReviews) {
        def allReviews = new ArrayList<Review>()
        allReviews.addAll(imdbReviews)
        allReviews.addAll(rottenTomatoesReviews)
        return allReviews
    }
}
