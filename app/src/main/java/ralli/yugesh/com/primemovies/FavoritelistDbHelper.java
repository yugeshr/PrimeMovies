package ralli.yugesh.com.primemovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FavoritelistDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "favoritelist.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritelistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                FavoritelistContract.FavortitelistEntry.TABLE_NAME + "(" +
                FavoritelistContract.FavortitelistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritelistContract.FavortitelistEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoritelistContract.FavortitelistEntry.COLUMN_ID + " TEXT NOT NULL, " +
                FavoritelistContract.FavortitelistEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritelistContract.FavortitelistEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                FavoritelistContract.FavortitelistEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                FavoritelistContract.FavortitelistEntry.COLUMN_DATE + " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritelistContract.FavortitelistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
