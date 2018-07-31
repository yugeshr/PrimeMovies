package ralli.yugesh.com.primemovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import ralli.yugesh.com.primemovies.adapter.MovieAdapter;
import ralli.yugesh.com.primemovies.data.FavoritelistContract;
import ralli.yugesh.com.primemovies.data.FavoritelistDbHelper;
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
    private static RecyclerView mMoviesList;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesList = findViewById(R.id.rv_posters);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
            mMoviesList.setLayoutManager(layoutManager);
        }else {
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
            mMoviesList.setLayoutManager(layoutManager);
        }

        mMoviesList.setHasFixedSize(true);

        FavoritelistDbHelper favoritelistDbHelper = new FavoritelistDbHelper(this);
        SQLiteDatabase sqLiteDatabase = favoritelistDbHelper.getWritableDatabase();

        movieAdapter = new MovieAdapter(this);
        mMoviesList.setAdapter(movieAdapter);

        isOnline();

        if (savedInstanceState!= null) {
            if (savedInstanceState.containsKey("sort")){
                sortBy = savedInstanceState.getInt("sort");
            }
        }

        loadMovieData();
    }

    private void loadMovieData() {

        Log.d(TAG,"loadMovieData()");
        mCursor = getAllMovies();

        URL movieDataUrl = NetworkUtils.buildUrl(sortBy);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(FETCH_MOVIE_DATA_URL,movieDataUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> fetchMovieLoader = loaderManager.getLoader(MOVIES_LOADER);

        if (fetchMovieLoader == null){
            loaderManager.initLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        }else {
            loaderManager.restartLoader(MOVIES_LOADER, queryBundle, this).forceLoad();
        }
        mCursor.close();
    }

    @Override
    public void onClick(String selectedMovie) {
        String[] data = new String[selectedMovie.length()];
        for (int i=0; i<selectedMovie.length();i++){
            data = selectedMovie.split("---");
        }
        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_POSTER",data[0]);
        bundle.putString("EXTRA_TITLE", data[2]);
        bundle.putString("EXTRA_PLOT",data[3]);
        bundle.putString("EXTRA_RATING",data[4]);
        bundle.putString("EXTRA_DATE",data[5]);
        bundle.putString("EXTRA_ID",data[1]);
        bundle.putBoolean("EXTRA_FLAG",flag);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClickFavorite(int id) {
        flag = true;

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
            bundle.putBoolean("EXTRA_FLAG",flag);

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
                try{
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
        }
        else {
            System.out.println("null value");
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_sort_tr:
                sortBy = 0;
                flag = false;
                mMoviesList.setAdapter(movieAdapter);
                loadMovieData();
                break;
            case R.id.action_sort_mp:
                sortBy = 1;
                flag = false;
                mMoviesList.setAdapter(movieAdapter);
                loadMovieData();
                break;
            case R.id.action_favorite: {
                flag = true;
                mCursor = getAllMovies();
                favoriteAdapter = new MovieAdapter(this, mCursor);
                mMoviesList.setAdapter(favoriteAdapter);
                favoriteAdapter.notifyDataSetChanged();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void isOnline() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sort",sortBy);
    }

    private Cursor getAllMovies(){
        try {
            return getContentResolver()
                    .query(FavoritelistContract.FavortitelistEntry.CONTENT_URI,
                            null,null,null,null);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart()");
        SharedPreferences preferences = getSharedPreferences("moviefavorite", 0);
        Boolean val = preferences.getBoolean("val",false);
        if (val) {
            mCursor = getAllMovies();
            favoriteAdapter = new MovieAdapter(this,mCursor);
            mMoviesList.setAdapter(favoriteAdapter);
            favoriteAdapter.notifyDataSetChanged();
        }
    }
}
