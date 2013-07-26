package com.movie.feel.helpers

/**
 * Created with IntelliJ IDEA.
 * User: alexcosma
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
        if(date)
        if (source == Constants.RottenTomatoes) {
            def dateParser = new java.text.SimpleDateFormat("yyyy-mm-dd")
            return dateParser.parse(date)
        }
        return null;
    }

}
