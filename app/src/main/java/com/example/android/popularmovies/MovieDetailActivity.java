package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieListResult;
import com.example.android.popularmovies.utils.JSONUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        try {
            String jsonString = getIntent().getStringExtra("json");
            MovieListResult mMovie = JSONUtils.createMovieListResult(new JSONObject(jsonString), this);

            String image_path = mMovie.getFullPosterPath();
            ImageView image_view = findViewById(R.id.movie_poster);
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(image_view);

            TextView title = findViewById(R.id.movie_title);
            title.setText(mMovie.original_title);

            TextView release_date = findViewById(R.id.movie_release_date);
            release_date.setText(String.valueOf(mMovie.release_date));

            TextView overview = findViewById(R.id.movie_overview);
            overview.setText(mMovie.overview);

            TextView popularity = findViewById(R.id.movie_popularity);
            popularity.setText(String.valueOf(mMovie.popularity));

            TextView vote_count = findViewById(R.id.movie_vote_count);
            vote_count.setText(String.valueOf(mMovie.vote_count));

            TextView vote_average = findViewById(R.id.movie_vote_average);
            vote_average.setText(String.valueOf(mMovie.vote_average));


            Log.d(TAG, "json="+jsonString);
        } catch (Throwable e) {
            Log.d(TAG, "onCreate while digesting json object", e);
        }

    }
}
