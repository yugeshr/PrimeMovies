package ralli.yugesh.com.primemovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent parent = getIntent();
        String movieDataFromIntent = parent.getStringExtra(Intent.EXTRA_TEXT);
        //System.out.println(posterPathFromIntent);

        for (int i=0; i<movieDataFromIntent.length();i++){
             data = movieDataFromIntent.split("---");
        }

        TextView titleView = findViewById(R.id.tv_title);
        TextView plotView = findViewById(R.id.tv_plot);
        TextView ratingView = findViewById(R.id.tv_rating);
        TextView dateView = findViewById(R.id.tv_releasedate);
        ImageView posterView = findViewById(R.id.iv_poster);

        titleView.setText(data[2]);
        plotView.setText(data[3]);

        String rating = data[4]+"/10";
        ratingView.setText(rating);

        String[] releaseDate = data[5].split("-",2);
        dateView.setText(releaseDate[0]);

        String posterPath = "http://image.tmdb.org/t/p/w185/" + data[0];
        Picasso.get().load(posterPath).into(posterView);

    }
}
