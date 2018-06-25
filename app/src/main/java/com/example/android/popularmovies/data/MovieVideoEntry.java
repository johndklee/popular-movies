package com.example.android.popularmovies.data;

public class MovieVideoEntry {

    private static final String TAG = MovieVideoEntry.class.getSimpleName();

    private String movie_id;
    private String video_id;
    private String video_name;
    private String video_site;
    private String video_key;

    public MovieVideoEntry() {
    }

    @SuppressWarnings("WeakerAccess")
    public String getMovieID() {
        return this.movie_id;
    }

    public void setMovieID(String movie_id) {
        this.movie_id = movie_id;
    }

    @SuppressWarnings("WeakerAccess")
    public String getVideoID() {
        return this.video_id;
    }

    public void setVideoID(String video_id) {
        this.video_id = video_id;
    }

    public String getVideoName() {
        return video_name;
    }

    public void setVideoName(String video_name) {
        this.video_name = video_name;
    }

    public String getVideoSite() {
        return this.video_site;
    }

    public void setVideoSite(String video_site) {
        this.video_site = video_site;
    }

    public String getVideoKey() {
        return this.video_key;
    }

    public void setVideoKey(String video_key) {
        this.video_key = video_key;
    }

    public String toString() {
        return TAG+":"+getMovieID()+":"+getVideoID();
    }

}
