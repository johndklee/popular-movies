package com.example.android.popularmovies.data;

public class MovieReviewEntry {

    private static final String TAG = MovieReviewEntry.class.getSimpleName();

    private String movie_id;
    private String review_id;
    private String review_author;
    private String review_content;
    private String review_url;

    public MovieReviewEntry() {
    }

    @SuppressWarnings("WeakerAccess")
    public String getMovieID() {
        return this.movie_id;
    }

    public void setMovieID(String movie_id) {
        this.movie_id = movie_id;
    }

    @SuppressWarnings("WeakerAccess")
    public String getReviewID() {
        return this.review_id;
    }

    public void setReviewID(String review_id) {
        this.review_id = review_id;
    }

    public String getReviewAuthor() {
        return review_author;
    }

    public void setReviewAuthor(String review_author) {
        this.review_author = review_author;
    }

    public String getReviewContent() {
        return review_content;
    }

    public void setReviewContent(String review_content) {
        this.review_content = review_content;
    }

    public String getReviewURL() {
        return review_url;
    }

    public void setReviewURL(String review_url) {
        this.review_url = review_url;
    }

    public String toString() {
        return TAG+":"+getMovieID()+":"+getReviewID();
    }

}
