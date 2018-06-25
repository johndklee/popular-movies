package com.example.android.popularmovies.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieDetailEntry;
import com.example.android.popularmovies.data.MovieListItemEntry;
import com.example.android.popularmovies.data.MovieListType;
import com.example.android.popularmovies.data.MovieReviewEntry;
import com.example.android.popularmovies.data.MovieVideoEntry;

import org.json.JSONObject;

/**
 * JSONUtils
 *
 * convenient ways to create data object from JSONObject
 */
public class JSONUtils implements MovieListType {

    public static MovieListItemEntry createMovieListResult(JSONObject jsonObject, Context context) {
        MovieListItemEntry result = new MovieListItemEntry();
        String no_data = context.getResources().getString(R.string.no_data_message);
        loadMovieListResult(result, jsonObject, no_data);
        return result;
    }

    public static MovieDetailEntry createMovieDetailsResult(JSONObject jsonObject, Context context) {
        MovieDetailEntry result = new MovieDetailEntry();
        String no_data = context.getResources().getString(R.string.no_data_message);
        loadMovieListResult(result, jsonObject, no_data);
        result.setRuntime(jsonObject.optInt("runtime"));
        return result;
    }

    private static void loadMovieListResult(MovieListItemEntry result, JSONObject jsonObject, String no_data) {
        String movie_id = jsonObject.optString("id");
        if (TextUtils.isEmpty(movie_id)) {
            throw new IllegalArgumentException("id must be defined");
        }
        result.setMovieID(movie_id);
        result.setMovieListType(SHOW_MY_FAVORITES);
        result.setPosterPath(jsonObject.optString("poster_path", no_data));
        result.setOriginalTitle(jsonObject.optString("original_title", no_data));
        result.setOverview(jsonObject.optString("overview", no_data));
        result.setReleaseDate(jsonObject.optString("release_date", no_data));
        result.setVoteCount(jsonObject.optInt("vote_count"));
        result.setVoteAverage(jsonObject.optDouble("vote_average"));
        result.setPopularity(jsonObject.optDouble("popularity"));
    }

    public static MovieVideoEntry createMovieVideosResult(String movie_id, JSONObject jsonObject, Context context) {
        MovieVideoEntry result = new MovieVideoEntry();
        String no_data = context.getResources().getString(R.string.no_data_message);
        if (TextUtils.isEmpty(movie_id)) {
            throw new IllegalArgumentException("id must be defined");
        }
        result.setMovieID(movie_id);
        result.setVideoID(jsonObject.optString("id", no_data));
        result.setVideoName(jsonObject.optString("name", no_data));
        result.setVideoSite(jsonObject.optString("site", no_data));
        result.setVideoKey(jsonObject.optString("key", no_data));
        return result;
    }

    public static MovieReviewEntry createMovieReviewsResult(String movie_id, JSONObject jsonObject, Context context) {
        MovieReviewEntry result = new MovieReviewEntry();
        String no_data = context.getResources().getString(R.string.no_data_message);
        if (TextUtils.isEmpty(movie_id)) {
            throw new IllegalArgumentException("id must be defined");
        }
        result.setMovieID(movie_id);
        result.setReviewID(jsonObject.optString("id", no_data));
        result.setReviewAuthor(jsonObject.optString("author", no_data));
        result.setReviewContent(jsonObject.optString("content", no_data));
        result.setReviewURL(jsonObject.optString("url", no_data));
        return result;
    }

}
