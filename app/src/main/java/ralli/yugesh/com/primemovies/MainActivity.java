package ralli.yugesh.com.primemovies;

import android.annotation.SuppressLint;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String> {

    private int sortBy = 1;
    private MovieAdapter movieAdapter;
    private static final int MOVIES_LOADER = 22;
    private static final String FETCH_MOVIE_DATA_URL = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mMoviesList = findViewById(R.id.rv_posters);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        mMoviesList.setLayoutManager(layoutManager);

        mMoviesList.setHasFixedSize(true);

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
    }

    @Override
    public void onClick(String selectedMovie) {
        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,selectedMovie);
        startActivity(intent);
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
                jsonData = MovieDatabaseJson.getStringsFromJson(getApplicationContext(),data);
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
                loadMovieData();
                Toast.makeText(getApplicationContext(),"Sorted by Top Rated",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_sort_mp:
                sortBy = 1;
                loadMovieData();
                Toast.makeText(getApplicationContext(),"Sorted by Most Popular",Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void isOnline() {
        Runtime runtime = Runtime.getRuntime();
        int exitValue;
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            exitValue = ipProcess.waitFor();
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sort",sortBy);
    }
}
