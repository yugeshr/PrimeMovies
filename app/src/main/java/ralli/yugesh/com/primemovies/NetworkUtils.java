package ralli.yugesh.com.primemovies;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

final class NetworkUtils {

    private static final String API_KEY = ""; //TODO: Add api key here

    private static final String MOVIE_BASE_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    private static final String MOVIE_BASE_URL_RATED = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    private static final String MOVIE_BASE_URL_TRAILERS = "https://api.themoviedb.org/3/movie/";

    private static final String PARAM_LANGUAGE = "language";
    private static final String language = "en-US";
    private static final String videos = "/videos?";
    private static final String reviews = "/reviews?";

    /**
     * Builds the URL used to fetch data from tmdb.
     * @return The URL to use to fetch data from tmdb.
     */

    public static URL buildUrl (int sortBy){
        URL url = null;

        if (sortBy == 1) {
            Uri builtUri = Uri.parse(MOVIE_BASE_URL_POPULAR).buildUpon()
                    .appendQueryParameter(PARAM_LANGUAGE,language)
                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }else {
            Uri builtUri = Uri.parse(MOVIE_BASE_URL_RATED).buildUpon()
                    .appendQueryParameter(PARAM_LANGUAGE,language)
                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    public static URL buildTrailerUrl (String id){
        URL url = null;

        String updatedBaseUrl = MOVIE_BASE_URL_TRAILERS + id + videos + "api_key=" + API_KEY;
        Uri builtUri = Uri.parse(updatedBaseUrl).buildUpon().build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewUrl (String id){
        URL url = null;

        String updatedBaseUrl = MOVIE_BASE_URL_TRAILERS + id + reviews + "api_key=" + API_KEY;
        Uri builtUri = Uri.parse(updatedBaseUrl).buildUpon().build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
