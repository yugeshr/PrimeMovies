package ralli.yugesh.com.primemovies;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.Arrays;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
TrailersAdapter.TrailersAdapterOnClickHandler{

    private static final String TAG = "LOG";
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;
    private static RecyclerView mReviewsList;
    private static RecyclerView mTrailersList;
    LinearLayoutManager HorizontalLayout;
    LinearLayoutManager HorizontalLayout2;

    private String posterData;
    private String title;
    private String plot;
    private String ratingData;
    private String date;
    private String id;
    private Boolean flag;

    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent parent = getIntent();
        Bundle bundle = parent.getExtras();
        posterData = bundle.getString("EXTRA_POSTER");
        title = bundle.getString("EXTRA_TITLE");
        plot = bundle.getString("EXTRA_PLOT");
        ratingData = bundle.getString("EXTRA_RATING");
        date = bundle.getString("EXTRA_DATE");
        id = bundle.getString("EXTRA_ID");
        flag = bundle.getBoolean("EXTRA_FLAG");

        mReviewsList = findViewById(R.id.rv_reviews);
        mReviewsList.setHasFixedSize(true);

        mTrailersList = findViewById(R.id.rv_trailers);
        mTrailersList.setHasFixedSize(true);

        LayoutManager trailersLayoutManager = new LinearLayoutManager(this);
        mTrailersList.setLayoutManager(trailersLayoutManager);

        HorizontalLayout2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mTrailersList.setLayoutManager(HorizontalLayout2);

        trailersAdapter = new TrailersAdapter(this);
        mTrailersList.setAdapter(trailersAdapter);

        LayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsList.setLayoutManager(reviewsLayoutManager);

        HorizontalLayout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mReviewsList.setLayoutManager(HorizontalLayout);

        reviewsAdapter = new ReviewsAdapter();
        mReviewsList.setAdapter(reviewsAdapter);

        TextView titleView = findViewById(R.id.tv_title);
        TextView plotView = findViewById(R.id.tv_plot);
        TextView ratingView = findViewById(R.id.tv_rating);
        TextView dateView = findViewById(R.id.tv_releasedate);
        ImageView posterView = findViewById(R.id.iv_poster);

        titleView.setText(title);
        plotView.setText(plot);

        String rating = ratingData+"/10";
        ratingView.setText(rating);

        String[] releaseDate = date.split("-",2);
        dateView.setText(releaseDate[0]);

        String posterPath = POSTER_URL + posterData;
        Picasso.get().load(posterPath).into(posterView);

        final FloatingActionButton fab = findViewById(R.id.fab);
        if (flag) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
        }else {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));

                    String whereClause = "movieId=?";
                    String[] whereArgs = new String[] {id};
                    getContentResolver().delete(FavoritelistContract.FavortitelistEntry.CONTENT_URI,whereClause,whereArgs);

                    Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    flag = false;
                }
                else if(!flag){
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));

                    addNewMovie();

                    Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    flag = true;
                }

            }
        });

        loadData(id);

        /*trailerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + ytLink));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + ytLink));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }

            }
        });*/

    }

    private void addNewMovie(){

        ContentValues cv = new ContentValues();

        cv.put(FavoritelistContract.FavortitelistEntry.COLUMN_POSTER_PATH,posterData);
        cv.put(FavoritelistContract.FavortitelistEntry.COLUMN_ID,id);
        cv.put(FavoritelistContract.FavortitelistEntry.COLUMN_TITLE,title);
        cv.put(FavoritelistContract.FavortitelistEntry.COLUMN_PLOT,plot);
        cv.put(FavoritelistContract.FavortitelistEntry.COLUMN_RATING,ratingData);
        cv.put(FavoritelistContract.FavortitelistEntry.COLUMN_DATE,date);

        Uri uri = getContentResolver().insert(FavoritelistContract.FavortitelistEntry.CONTENT_URI,cv);
    }

    private void loadData(String id){
        URL trailerDataUrl = NetworkUtils.buildTrailerUrl(id);
        URL reviewDataUrl = NetworkUtils.buildReviewUrl(id);

        Bundle queryBundle = new Bundle();
        queryBundle.putString("trailers",trailerDataUrl.toString());
        queryBundle.putString("reviews",reviewDataUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> fetchDataLoader = loaderManager.getLoader(1);
        Loader<Object> fetchReviewLoader = loaderManager.getLoader(2);

        if (fetchDataLoader == null && fetchReviewLoader == null){
            loaderManager.initLoader(1, queryBundle, this).forceLoad();
            loaderManager.initLoader(2, queryBundle, this).forceLoad();
        }else {
            loaderManager.restartLoader(1, queryBundle, this).forceLoad();
            loaderManager.restartLoader(2, queryBundle, this).forceLoad();
        }
    }

    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(final int i, final Bundle args) {
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
                if (i == 1){
                    String fetchTrailerUrlString = args.getString("trailers");
                    if (fetchTrailerUrlString == null) {
                        return null;
                    }
                    try{
                        URL trailerUrl = new URL(fetchTrailerUrlString);
                        return NetworkUtils.getResponseFromHttpUrl(trailerUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }else {
                    String fetchReviewUrlString = args.getString("reviews");
                    if (fetchReviewUrlString == null){
                        return null;
                    }
                    try {
                        URL reviewUrl = new URL(fetchReviewUrlString);
                        return NetworkUtils.getResponseFromHttpUrl(reviewUrl);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

        int id = loader.getId();

        if (data != null && !data.equals("")) {

            String[] jsonData;

            try {
                if (id == 1){
                    jsonData = MovieDatabaseJson.getTrailerStringsFromJson(data);
                    setYtLink(jsonData);
                }
                else {
                    jsonData = MovieDatabaseJson.getReviewStringsFromJson(data);
                    setReviews(jsonData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("null value");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public void setYtLink(String[] data) {
        String[] trailer = new String[data.length];

        for(int j=0; j<data.length;j++){
            trailer = data;
        }
        trailersAdapter.setTrailerData(trailer);
    }

    public void setReviews(String[] data) throws NullPointerException {
        String[] author = new String[data.length];
        String[] content = new String[data.length];
        String[] reviewData;

        for (int j=0;j<data.length;j++) {

            for (int i=0; i<data.length;i++){
                reviewData = data[j].split("---");
                author[j] = reviewData[0];
                content[j] = reviewData[1];
            }
        }
        reviewsAdapter.setAuthorData(author);
        reviewsAdapter.setReviewData(content);
    }

    @Override
    public void onClick(String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
