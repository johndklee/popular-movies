package com.example.android.popularmovies.data;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

@Entity(tableName = "movie_list_item", primaryKeys = {"movieListType", "movieID"})
public class MovieListItemEntry implements MovieListType {

    private static final String TAG = MovieListItemEntry.class.getSimpleName();

    @NonNull
    private String movieListType = SHOW_MY_FAVORITES;
    @NonNull
    private String movieID = "";

    private String posterPath;
    private String originalTitle;
    private String overview;
    private double voteAverage;
    private double popularity;
    private int voteCount;
    private String releaseDate;

    private static final String POSTER_PATH_PREFIX = "http://image.tmdb.org/t/p/w185";

    public MovieListItemEntry() {
    }

    @NonNull
    public String getMovieID() {
        return this.movieID;
    }

    public void setMovieID(@NonNull String movie_id) {
        this.movieID = movie_id;
    }

    @NonNull
    public String getMovieListType() {
        return this.movieListType;
    }

    public void setMovieListType(@NonNull String movie_list_type) {
        this.movieListType = movie_list_type;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public void setPosterPath(String poster_path) {
        this.posterPath = poster_path;
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public void setOriginalTitle(String original_title) {
        this.originalTitle = original_title;
    }

    public String getOverview() {
        return this.overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return this.voteAverage;
    }

    public void setVoteAverage(double vote_average) {
        this.voteAverage = vote_average;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return this.voteCount;
    }

    public void setVoteCount(int vote_count) {
        this.voteCount = vote_count;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String release_date) {
        this.releaseDate = release_date;
    }

    public String getFullPosterPath() {
        if (TextUtils.isEmpty(posterPath)) {
            return null;
        }
        return POSTER_PATH_PREFIX+posterPath;
    }

    public String getVoteAverageOutOf10() {
        if (voteCount <= 0 || voteAverage <= 0.00) {
            return null;
        }
        return voteAverage+"/10";
    }

    public String getReleaseYear() {
        if (TextUtils.isEmpty(releaseDate)) {
            return null;
        }
        return releaseDate.substring(0, 4);
    }

    public String toString() {
        return TAG+":"+getMovieID();
    }

}
