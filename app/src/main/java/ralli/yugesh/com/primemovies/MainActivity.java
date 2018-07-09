package ralli.yugesh.com.primemovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private ImageView imageView;
    private int sortBy = 1;
    private MovieAdapter movieAdapter;
    private Toast mToast;

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

        loadMovieData();

    }

    private void loadMovieData() {

        URL movieDataUrl = NetworkUtils.buildUrl(sortBy);
        new FetchMovieDataTask().execute(movieDataUrl);

    }

    @Override
    public void onClick(String selectedMovie) {
        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,selectedMovie);
        startActivity(intent);
    }

    public class FetchMovieDataTask extends AsyncTask<URL, Void, String[]>{

        @Override
        protected String[] doInBackground(URL... urls) {
            URL fetchUrl = urls[0];
            String fetchResponse;
            try{
                fetchResponse = NetworkUtils.getResponseFromHttpUrl(fetchUrl);
                String[] simpleJsonData = MovieDatabaseJson.getStringsFromJson(MainActivity.this,fetchResponse);
                return simpleJsonData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] fetchResults) {
            if (fetchResults != null && !fetchResults.equals("")) {
                    //System.out.println(movieString);
                    movieAdapter.setPosterPath(fetchResults);
            }
            else {
                System.out.println("null value");
            }
            super.onPostExecute(fetchResults);
        }
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
}
