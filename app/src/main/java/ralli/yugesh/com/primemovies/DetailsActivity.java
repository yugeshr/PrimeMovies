package ralli.yugesh.com.primemovies;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import org.json.JSONException;
import java.net.URL;
import butterknife.BindView;
import butterknife.ButterKnife;
import ralli.yugesh.com.primemovies.adapter.ReviewsAdapter;
import ralli.yugesh.com.primemovies.adapter.TrailersAdapter;
import ralli.yugesh.com.primemovies.data.FavoritelistContract;
import ralli.yugesh.com.primemovies.utils.MovieDatabaseJson;
import ralli.yugesh.com.primemovies.utils.NetworkUtils;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
TrailersAdapter.TrailersAdapterOnClickHandler{

    private static final String TAG = "Log";
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    LinearLayoutManager trailersLayoutManager;
    LinearLayoutManager horizontalLayout2;

    private String posterData;
    private String title;
    private String plot;
    private String ratingData;
    private String date;
    private String id;

    @BindView(R.id.rv_trailers) RecyclerView mTrailersList;
    @BindView(R.id.rv_reviews) RecyclerView mReviewsList;

    @BindView(R.id.tv_title) TextView titleView;
    @BindView(R.id.tv_plot) TextView plotView;
    @BindView(R.id.tv_rating) TextView ratingView;
    @BindView(R.id.tv_releasedate) TextView dateView;
    @BindView(R.id.iv_poster) ImageView posterView;
    @BindView(R.id.tbtn_favorite) ToggleButton toggleButton;

    private SharedPreferences pref;

    private static final String MOVIE_FAVORITE = "moviefavorite";
    private static final String POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    private Parcelable mSaveState;
    private int positionIndex;
    private int topView;
    private Parcelable listState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        Intent parent = getIntent();
        Bundle bundle = parent.getExtras();
        posterData = bundle.getString("EXTRA_POSTER");
        title = bundle.getString("EXTRA_TITLE");
        plot = bundle.getString("EXTRA_PLOT");
        ratingData = bundle.getString("EXTRA_RATING");
        date = bundle.getString("EXTRA_DATE");
        id = bundle.getString("EXTRA_ID");

        mReviewsList.setHasFixedSize(true);
        mTrailersList.setHasFixedSize(true);

        trailersLayoutManager = new LinearLayoutManager(this);
        mTrailersList.setLayoutManager(trailersLayoutManager);

        trailersAdapter = new TrailersAdapter(this);
        mTrailersList.setAdapter(trailersAdapter);

        LayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsList.setLayoutManager(reviewsLayoutManager);

        LinearLayoutManager horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mReviewsList.setLayoutManager(horizontalLayout);

        reviewsAdapter = new ReviewsAdapter();
        mReviewsList.setAdapter(reviewsAdapter);

        titleView.setText(title);
        plotView.setText(plot);

        String rating = ratingData+"/10";
        ratingView.setText(rating);

        String[] releaseDate = date.split("-",2);
        dateView.setText(releaseDate[0]);

        String posterPath = POSTER_URL + posterData;
        Picasso.get().load(posterPath).into(posterView);

        pref = getSharedPreferences(MOVIE_FAVORITE, 0);
        final Boolean flag = pref.getBoolean("Favorite "+id, true);
        Log.d("Favorite "+id, flag.toString());

        toggleButton.setChecked(isAlreadyFavorited(id));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    addNewMovie();
                    SharedPreferences.Editor editor = getSharedPreferences(MOVIE_FAVORITE, MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite "+id, true);
                    editor.putBoolean("val",false);
                    editor.apply();
                }
                else {
                    String whereClause = "movieId=?";
                    String[] whereArgs = new String[] {id};
                    getContentResolver().delete(FavoritelistContract.FavortitelistEntry.CONTENT_URI,whereClause,whereArgs);

                    SharedPreferences.Editor editor = getSharedPreferences(MOVIE_FAVORITE, MODE_PRIVATE).edit();
                    editor.putBoolean("Favorite "+id, false);
                    editor.putBoolean("val",true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Removed movie from favorites",Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadData(id);
    }

    private boolean isAlreadyFavorited(String id) {
        String[] queryId = new String[]{id};
        Cursor cursor = getContentResolver().query(FavoritelistContract.FavortitelistEntry.CONTENT_URI, null,
                FavoritelistContract.FavortitelistEntry.COLUMN_ID+"=?",queryId, null);
        int size = cursor.getCount();
        cursor.close();
        return size > 0;
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
        if(uri != null) {
            Toast.makeText(getApplicationContext(),"Added movie to favorites",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed to add movie to favorites",Toast.LENGTH_SHORT).show();
        }
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
                    setTrailers(jsonData);
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

    private void setTrailers(String[] data) {
        String[] trailer = new String[data.length];

        for (String aData : data) {
            trailer = data;
        }
        trailersAdapter.setTrailerData(trailer);
    }

    private void setReviews(String[] data) throws NullPointerException {
        String[] author = new String[data.length];
        String[] content = new String[data.length];
        String[] reviewData;

        for (int j=0;j<data.length;j++) {

            for (String aData : data) {
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

    @Override
    protected void onPause() {
        super.onPause();
        positionIndex = trailersLayoutManager.findFirstVisibleItemPosition();
        View startView = mTrailersList.getChildAt(0);
        topView = (startView == null) ? 0 : (startView.getTop()) - mTrailersList.getPaddingTop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (positionIndex!= 1) {
            trailersLayoutManager.scrollToPositionWithOffset(positionIndex,topView);
        }
    }
}
