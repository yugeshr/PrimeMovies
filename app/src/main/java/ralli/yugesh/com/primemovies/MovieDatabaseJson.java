package ralli.yugesh.com.primemovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class MovieDatabaseJson {

    public static String[] getStringsFromJson(String movieJsonStr)
            throws JSONException {

        /* Movie information. Each movie's info is an element of the "results" array */
        final String DATA_LIST = "results";
        final String DATA_ID = "id";
        final String DATA_TITLE = "title";
        final String DATA_POSTERPATH = "poster_path";
        final String DATA_PLOT = "overview";
        final String DATA_RATING = "vote_average";
        final String DATA_DATE = "release_date";

        /* String array to hold each movie's poster path */
        String[] parsedMovieData;

        JSONObject moviesJson = new JSONObject(movieJsonStr);

        JSONArray resultArray = moviesJson.getJSONArray(DATA_LIST);

        parsedMovieData = new String[resultArray.length()];

        for (int i=0; i< resultArray.length();i++){
            int id;
            String title;
            String posterPath;
            String plot;
            String date;
            String rating;

            /* Get the JSON object representing the movie */
            JSONObject movie = resultArray.getJSONObject(i);

            id = movie.getInt(DATA_ID);
            title = movie.getString(DATA_TITLE);
            posterPath = movie.getString(DATA_POSTERPATH);
            plot = movie.getString(DATA_PLOT);
            rating = movie.getString(DATA_RATING);
            date = movie.getString(DATA_DATE);

            parsedMovieData[i] = posterPath + "---" + id + "---" + title +"---"+ plot +"---"+ rating +"---"+ date;
            //System.out.println(parsedMovieData[i]);

        }
        return parsedMovieData;
    }

    public static String[] getTrailerStringsFromJson(String movieJsonStr)
            throws JSONException {

        final String DATA_LIST = "results";
        final String DATA_KEY = "key";

        String[] parsedMovieData;

        JSONObject moviesJson = new JSONObject(movieJsonStr);

        JSONArray resultArray = moviesJson.getJSONArray(DATA_LIST);

        parsedMovieData = new String[resultArray.length()];

        for (int i=0; i< resultArray.length();i++){
            String key;

            JSONObject movie = resultArray.getJSONObject(i);
            key = movie.getString(DATA_KEY);

            parsedMovieData[i] = key;
        }
        return parsedMovieData;
    }

    public static String[] getReviewStringsFromJson(String movieJsonStr)
            throws JSONException {

        final String DATA_LIST = "results";
        final String DATA_AUTHOR = "author";
        final String DATA_CONTENT = "content";

        String[] parsedMovieData;

        JSONObject moviesJson = new JSONObject(movieJsonStr);

        JSONArray resultArray = moviesJson.getJSONArray(DATA_LIST);

        parsedMovieData = new String[resultArray.length()];

        for (int i=0; i< resultArray.length();i++){
            String author;
            String content;

            JSONObject movie = resultArray.getJSONObject(i);
            author = movie.getString(DATA_AUTHOR);
            content = movie.getString(DATA_CONTENT);

            parsedMovieData[i] = author + "---" + content;
        }
        return parsedMovieData;
    }
}
