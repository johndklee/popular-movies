package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String MDB_API_KEY_QP_KEY = "api_key";
    private static final String MDB_API_KEY_QP_VALUE = BuildConfig.MDB_APP_KEY;

    private static final String MDB_POPULAR_PATH = "popular";
    private static final String MDB_TOP_RATED_PATH = "top_rated";
    private static final String MDB_MOVIE_PATH = "movie";
    private static final String MDB_VIDEOS_PATH = "videos";
    private static final String MDB_REVIEWS_PATH = "reviews";

    private static final String MDB_PAGE_QP_NAME = "page";

    /**
     * Build URL to fetch popular movies
     * https://developers.themoviedb.org/3/movies/get-popular-movies
     * Get Popular
     * GET /movie/popular
     * Get a list of the current popular movies on TMDb. This list updates daily.
     *
     * @param page Page ID
     * @return URL for MDB API
     */
    public static URL buildPopularMoviesURL(int page) {
        Log.d(TAG, "buildPopularMoviesURL( "+page+" ) called");
        URL url;
        try {
            if (page < 0) page = 1;
            Uri uri = Uri.parse(MDB_BASE_URL).buildUpon()
                    .appendPath(MDB_POPULAR_PATH)
                    .appendQueryParameter(MDB_API_KEY_QP_KEY, MDB_API_KEY_QP_VALUE)
                    .appendQueryParameter(MDB_PAGE_QP_NAME, String.valueOf(page))
                    .build();
            url = new URL(uri.toString());
        } catch (Throwable e) {
            Log.e(TAG, "failed to build URL", e);
            url = null;
        }
        Log.d(TAG, "returned "+url);
        return url;
    }

    /**
     * Build URL to fetch top rated movies
     * https://developers.themoviedb.org/3/movies/get-top-rated-movies
     * Get Top Rated
     * GET /movie/top_rated
     * Get the top rated movies on TMDb.
     *
     * @param page Page ID
     * @return URL for MDB API
     */
    public static URL buildTopRatedMoviesURL(int page) {
        Log.d(TAG, "buildTopRatedMoviesURL("+page+") called");
        URL url;
        try {
            if (page < 0) page = 1;
            Uri uri = Uri.parse(MDB_BASE_URL).buildUpon()
                    .appendPath(MDB_TOP_RATED_PATH)
                    .appendQueryParameter(MDB_API_KEY_QP_KEY, MDB_API_KEY_QP_VALUE)
                    .appendQueryParameter(MDB_PAGE_QP_NAME, String.valueOf(page))
                    .build();
            url = new URL(uri.toString());
        } catch (Throwable e) {
            Log.e(TAG, "failed to build URL", e);
            url = null;
        }
        Log.d(TAG, "returned "+url);
        return url;
    }

    /**
     * Build URL to fetch videos of a movie
     * https://developers.themoviedb.org/3/movies/get-movie-videos
     * Get Videos
     * GET /movie/{movie_id}/videos
     * Get the videos that have been added to a movie.
     *
     * @param movie_id Movie ID
     * @return URL for MDB API
     */
    public static URL buildMovieVideosURL(int movie_id) {
        Log.d(TAG, "buildMovieVideosURL("+movie_id+") called");
        URL url;
        try {
            Uri uri = Uri.parse(MDB_BASE_URL).buildUpon()
                    .appendPath(MDB_MOVIE_PATH)
                    .appendPath(String.valueOf(movie_id))
                    .appendPath(MDB_VIDEOS_PATH)
                    .appendQueryParameter(MDB_API_KEY_QP_KEY, MDB_API_KEY_QP_VALUE)
                    .build();
            url = new URL(uri.toString());
        } catch (Throwable e) {
            Log.e(TAG, "failed to build URL", e);
            url = null;
        }
        Log.d(TAG, "returned "+url);
        return url;
    }

    /**
     * Build URL to fetch reviews of a movie
     * https://developers.themoviedb.org/3/movies/get-movie-reviews
     * Get Reviews
     * GET /movie/{movie_id}/reviews
     * Get the user reviews for a movie.
     *
     * @param movie_id Movie ID
     * @return URL for MDB API
     */
    public static URL buildMovieReviewsURL(int movie_id) {
        Log.d(TAG, "buildMovieReviewsURL("+movie_id+") called");
        URL url;
        try {
            Uri uri = Uri.parse(MDB_BASE_URL).buildUpon()
                    .appendPath(MDB_MOVIE_PATH)
                    .appendPath(String.valueOf(movie_id))
                    .appendPath(MDB_REVIEWS_PATH)
                    .appendQueryParameter(MDB_API_KEY_QP_KEY, MDB_API_KEY_QP_VALUE)
                    .build();
            url = new URL(uri.toString());
        } catch (Throwable e) {
            Log.e(TAG, "failed to build URL", e);
            url = null;
        }
        Log.d(TAG, "returned "+url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Check whether the device has network access
     *
     * @param context
     * @return true if the device has network access
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }

}
