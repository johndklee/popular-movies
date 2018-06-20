package com.example.android.popularmovies.utils;

import android.content.Context;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieDetailsResult;
import com.example.android.popularmovies.data.MovieListResult;
import com.example.android.popularmovies.data.MovieReviewsResult;
import com.example.android.popularmovies.data.MovieVideosResult;

import org.json.JSONObject;

/**
 * JSONUtils
 *
 * convenient ways to create data object from JSONObject
 */
public class JSONUtils {

    public static MovieListResult createMovieListResult(JSONObject jsonObject, Context context) {
        MovieListResult result = new MovieListResult(jsonObject);

        String no_data = context.getResources().getString(R.string.no_data_message);

        loadMovieListResult(result, jsonObject, no_data);

        return result;
    }

    private static void loadMovieListResult(MovieListResult result, JSONObject jsonObject, String no_data) {
        result.movie_id = jsonObject.optString("id", no_data);
        result.poster_path = jsonObject.optString("poster_path", no_data);

        result.title = jsonObject.optString("title", no_data);
        result.original_title = jsonObject.optString("original_title", no_data);
        result.overview = jsonObject.optString("overview", no_data);
        result.release_date = jsonObject.optString("release_date", no_data);

        result.vote_count = jsonObject.optInt("vote_count");
        result.vote_average = jsonObject.optDouble("vote_average");
        result.popularity = jsonObject.optDouble("popularity");
    }

    public static MovieDetailsResult createMovieDetailsResult(JSONObject jsonObject, Context context) {
        MovieDetailsResult result = new MovieDetailsResult(jsonObject);

        String no_data = context.getResources().getString(R.string.no_data_message);

        loadMovieListResult(result, jsonObject, no_data);

        result.runtime = jsonObject.optInt("runtime");

        return result;
    }

    public static MovieVideosResult createMovieVideosResult(JSONObject jsonObject, Context context) {
        MovieVideosResult result = new MovieVideosResult(jsonObject);

        String no_data = context.getResources().getString(R.string.no_data_message);

        result.video_id = jsonObject.optString("id", no_data);
        result.video_name = jsonObject.optString("name", no_data);
        result.video_site = jsonObject.optString("site", no_data);
        result.video_key = jsonObject.optString("key", no_data);

        return result;
    }

    public static MovieReviewsResult createMovieReviewsResult(JSONObject jsonObject, Context context) {
        MovieReviewsResult result = new MovieReviewsResult(jsonObject);

        String no_data = context.getResources().getString(R.string.no_data_message);

        result.review_id = jsonObject.optString("id", no_data);
        result.review_author = jsonObject.optString("author", no_data);
        result.review_content = jsonObject.optString("content", no_data);
        result.review_url = jsonObject.optString("url", no_data);

        return result;
    }

}
