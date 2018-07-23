package ralli.yugesh.com.primemovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private SQLiteDatabase sqLiteDatabase;

    private String posterData;
    private String title;
    private String plot;
    private String ratingData;
    private String date;
    private String id;
    private Boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView titleView = findViewById(R.id.tv_title);
        TextView plotView = findViewById(R.id.tv_plot);
        TextView ratingView = findViewById(R.id.tv_rating);
        TextView dateView = findViewById(R.id.tv_releasedate);
        ImageView posterView = findViewById(R.id.iv_poster);

        FavoritelistDbHelper favoritelistDbHelper = new FavoritelistDbHelper(this);
        sqLiteDatabase = favoritelistDbHelper.getWritableDatabase();

        Intent parent = getIntent();
        Bundle bundle = parent.getExtras();
        assert bundle != null;
        posterData = bundle.getString("EXTRA_POSTER");
        title = bundle.getString("EXTRA_TITLE");
        plot = bundle.getString("EXTRA_PLOT");
        ratingData = bundle.getString("EXTRA_RATING");
        date = bundle.getString("EXTRA_DATE");
        id = bundle.getString("EXTRA_ID");
        flag = bundle.getBoolean("EXTRA_FLAG");

        titleView.setText(title);
        plotView.setText(plot);

        String rating = ratingData+"/10";
        ratingView.setText(rating);

        String[] releaseDate = date.split("-",2);
        dateView.setText(releaseDate[0]);

        String posterPath = "http://image.tmdb.org/t/p/w185/" + posterData;
        Picasso.get().load(posterPath).into(posterView);

        /*Toast.makeText(getApplicationContext(),"Movie added to favorites!", Toast.LENGTH_SHORT).show();
        addNewMovie();

        String whereClause = "movieId=?";
        String[] whereArgs = new String[] {id};
        sqLiteDatabase.delete(FavoritelistContract.FavortitelistEntry.TABLE_NAME,whereClause,whereArgs);
        finish();*/

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
}
