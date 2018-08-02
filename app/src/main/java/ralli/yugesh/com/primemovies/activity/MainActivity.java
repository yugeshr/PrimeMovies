package ralli.yugesh.com.primemovies.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONException;
import java.net.URL;
import ralli.yugesh.com.primemovies.R;
import ralli.yugesh.com.primemovies.adapter.MovieAdapter;
import ralli.yugesh.com.primemovies.data.FavoritelistContract;
import ralli.yugesh.com.primemovies.utils.MovieDatabaseJson;
import ralli.yugesh.com.primemovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "LOG";
    private int sortBy = 1;
    private MovieAdapter movieAdapter;
    private MovieAdapter favoriteAdapter;
    private static final int MOVIES_LOADER = 22;
    private static final String FETCH_MOVIE_DATA_URL = "query";
    private static Cursor mCursor;
    private RecyclerView mMoviesList;
    private Boolean flag = false;
    GridLayoutManager layoutManager;
    private Parcelable mRecylcerViewParecelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesList = findViewById(R.id.rv_posters);
        mMoviesList.setSaveEnabled(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getApplicationContext(), 3);
            mMoviesList.setLayoutManager(layoutManager);
        } else {
            layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            mMoviesList.setLayoutManager(layoutManager);
        }

        mMoviesList.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        mMoviesList.setAdapter(movieAdapter);

        if (savedInstanceState != null) {
            mRecylcerViewParecelable = savedInstanceState.getParcelable("GRID_LAYOUT_PARCEL_KEY");
            if (savedInstanceState.containsKey("sort")) {
                sortBy = savedInstanceState.getInt("sort");
            }
        }

        loadMovieData();
    }

    private void loadMovieData() {
        mCursor = getAllMovies();

        URL movieDataUrl = NetworkUtils.buildUrl(sortBy);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(FETCH_MOVIE_DATA_URL, movieDataUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> fetchMovieLoader = loaderManager.getLoader(MOVIES_LOADER);

        if (fetchMovieLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        }
        mCursor.close();
    }

    @Override
    public void onClick(String selectedMovie) {
        String[] data = new String[selectedMovie.length()];
        for (int i = 0; i < selectedMovie.length(); i++) {
            data = selectedMovie.split("---");
        }
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_POSTER", data[0]);
        bundle.putString("EXTRA_TITLE", data[2]);
        bundle.putString("EXTRA_PLOT", data[3]);
        bundle.putString("EXTRA_RATING", data[4]);
        bundle.putString("EXTRA_DATE", data[5]);
        bundle.putString("EXTRA_ID", data[1]);
        bundle.putBoolean("EXTRA_FLAG", flag);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClickFavorite(int id) {
        Cursor cursor = getApplicationContext()
                .getContentResolver()
                .query(FavoritelistContract.FavortitelistEntry.CONTENT_URI,
                        null,
                        FavoritelistContract.FavortitelistEntry.COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)},
                        null);

        assert cursor != null;
        if (cursor.moveToFirst()) {
            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("EXTRA_POSTER",
                    cursor.getString(cursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_POSTER_PATH)));
            bundle.putString("EXTRA_TITLE",
                    cursor.getString(cursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_TITLE)));
            bundle.putString("EXTRA_PLOT",
                    cursor.getString(cursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_PLOT)));
            bundle.putString("EXTRA_RATING",
                    cursor.getString(cursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_RATING)));
            bundle.putString("EXTRA_DATE",
                    cursor.getString(cursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_DATE)));
            bundle.putString("EXTRA_ID",
                    cursor.getString(cursor.getColumnIndex(FavoritelistContract.FavortitelistEntry.COLUMN_ID)));

            cursor.close();
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }

            }

            @Override
            public String loadInBackground() {
                String fetchQueryUrlString = args.getString(FETCH_MOVIE_DATA_URL);
                //System.out.println(fetchQueryUrlString);
                if (fetchQueryUrlString == null) {
                    return null;
                }
                try {
                    URL movieUrl = new URL(fetchQueryUrlString);
                    return NetworkUtils.getResponseFromHttpUrl(movieUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null && !data.equals("")) {

            String[] jsonData = null;
            try {
                jsonData = MovieDatabaseJson.getStringsFromJson(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //System.out.println(jsonData);
            movieAdapter.setPosterPath(jsonData);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMoviesList.getLayoutManager().onRestoreInstanceState(mRecylcerViewParecelable);
                }
            },100);
        } else {
            System.out.println("null value");
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_tr:
                sortBy = 0;
                mMoviesList.setAdapter(movieAdapter);
                loadMovieData();
                break;
            case R.id.action_sort_mp:
                sortBy = 1;
                mMoviesList.setAdapter(movieAdapter);
                loadMovieData();
                break;
            case R.id.action_favorite: {
                mCursor = getAllMovies();
                favoriteAdapter = new MovieAdapter(this, mCursor);
                mMoviesList.setAdapter(favoriteAdapter);
                favoriteAdapter.notifyDataSetChanged();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sort", sortBy);
        mRecylcerViewParecelable = mMoviesList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("GRID_LAYOUT_PARCEL_KEY", mRecylcerViewParecelable);
    }



    private Cursor getAllMovies() {
        try {
            return getContentResolver()
                    .query(FavoritelistContract.FavortitelistEntry.CONTENT_URI,
                            null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences preferences = getSharedPreferences("moviefavorite", 0);
        Boolean val = preferences.getBoolean("val", true);
        Log.d(TAG, "onRestart() : " + val);
        if (val) {
            mCursor = getAllMovies();
            favoriteAdapter = new MovieAdapter(this, mCursor);
            mMoviesList.setAdapter(favoriteAdapter);
            favoriteAdapter.notifyDataSetChanged();
        }
    }
    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int positionIndex = savedInstanceState.getInt("positionValue");
        ((GridLayoutManager) mMoviesList.getLayoutManager()).scrollToPosition(positionIndex);
        Log.d(TAG,"Restore "+positionIndex);
    }*/
}