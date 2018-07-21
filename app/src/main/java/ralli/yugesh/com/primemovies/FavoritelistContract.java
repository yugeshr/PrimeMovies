package ralli.yugesh.com.primemovies;

import android.provider.BaseColumns;

class FavoritelistContract {

    public static final class FavortitelistEntry implements BaseColumns {

        public static final String TABLE_NAME = "favoritelist";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_TITLE = "title" ;
        public static final String COLUMN_POSTER_PATH = "posterPath" ;
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating" ;
        public static final String COLUMN_DATE = "releaseDate";

    }
}
