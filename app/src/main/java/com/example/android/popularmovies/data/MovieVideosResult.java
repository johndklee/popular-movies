package com.example.android.popularmovies.data;

import org.json.JSONObject;

public class MovieVideosResult {

    public String video_id;
    public String video_name;
    public String video_site;
    public String video_key;

    private final JSONObject mJSONObject;

    public MovieVideosResult(JSONObject jsonObject) {
        mJSONObject = jsonObject;
    }

    public String getVideoName() {
        return video_name;
    }

    public String getYoutubeKey() {
        if ("Youtube".equalsIgnoreCase(video_site)) {
            return video_key;
        }
        return null;
    }

    public String toString() {
        return mJSONObject.toString();
    }

}
