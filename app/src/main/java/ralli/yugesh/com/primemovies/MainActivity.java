package ralli.yugesh.com.primemovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener {

    private ImageView imageView;
    private String imageLink;
    private static final int MOVIE_LIST_ITEMS = 20;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //imageView = (ImageView) findViewById(R.id.iv_thumbnail_data);
        RecyclerView mMoviesList = (RecyclerView) findViewById(R.id.rv_posters);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMoviesList.setLayoutManager(layoutManager);

        mMoviesList.setHasFixedSize(true);

        MovieAdapter movieAdapter = new MovieAdapter(MOVIE_LIST_ITEMS, this);
        mMoviesList.setAdapter(movieAdapter);

        //URL imageLoadUrl = NetworkUtils.buildUrl();

        //imageLink = imageLoadUrl.toString();

        //Picasso.get().load(imageLink).into(imageView);

        //movieAdapter = new MovieAdapter(getApplicationContext(),Arrays.asList(movieData));

        //GridView gridView = (GridView) findViewById(R.id.gv_movies);
        //gridView.setAdapter(movieAdapter);
        
        loadMovieData();

    }

    private void loadMovieData() {

        URL movieDataUrl = NetworkUtils.buildUrl();
        new FetchMovieDataTask().execute(movieDataUrl);

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) {
            mToast.cancel();
        }

        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();

    }

    public class FetchMovieDataTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL fetchUrl = urls[0];
            String fetchResults = null;
            try{
                fetchResults = NetworkUtils.getResponseFromHttpUrl(fetchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return fetchResults;
        }

        @Override
        protected void onPostExecute(String fetchResults) {
            if (fetchResults != null && !fetchResults.equals("")) {

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
                Toast.makeText(getApplicationContext(),"Sorted by Top Rated",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_sort_mp:
                Toast.makeText(getApplicationContext(),"Sorted by Most Popular",Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
