package ralli.yugesh.com.primemovies;

import android.net.Uri;
import android.provider.BaseColumns;

class FavoritelistContract {

    public static final String AUTHORITY = "ralli.yugesh.com.primemovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH = "favoritelist";

    public static final class FavortitelistEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String TABLE_NAME = "favoritelist";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_TITLE = "title" ;
        public static final String COLUMN_POSTER_PATH = "posterPath" ;
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating" ;
        public static final String COLUMN_DATE = "releaseDate";

    }
}
