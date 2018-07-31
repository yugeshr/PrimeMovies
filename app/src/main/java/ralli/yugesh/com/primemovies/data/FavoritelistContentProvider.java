package ralli.yugesh.com.primemovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritelistContentProvider extends ContentProvider {

    private FavoritelistDbHelper mFavoritelistDbHelper;

    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    private static final int FAVORITELIST = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritelistContract.AUTHORITY,FavoritelistContract.PATH,FAVORITELIST);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFavoritelistDbHelper = new FavoritelistDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = mFavoritelistDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){
            case FAVORITELIST: {
                returnCursor = sqLiteDatabase.query( FavoritelistContract.FavortitelistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase sqLiteDatabase = mFavoritelistDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case FAVORITELIST: {
                long id = sqLiteDatabase
                        .insert(FavoritelistContract.FavortitelistEntry.TABLE_NAME,null,contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoritelistContract.FavortitelistEntry.CONTENT_URI,id);
                }else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase sqLiteDatabase = mFavoritelistDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch (match){
            case FAVORITELIST: {
                moviesDeleted = sqLiteDatabase
                        .delete(FavoritelistContract.FavortitelistEntry.TABLE_NAME, "movieId=?", selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
