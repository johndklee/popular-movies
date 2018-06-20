package com.example.android.popularmovies.data;

import org.json.JSONObject;

public class MovieReviewsResult {

    public String review_id;
    public String review_author;
    public String review_content;
    public String review_url;

    private final JSONObject mJSONObject;

    public MovieReviewsResult(JSONObject jsonObject) {
        mJSONObject = jsonObject;
    }

    public String getReviewAuthor() {
        return review_author;
    }

    public String getReviewContent() {
        return review_content;
    }

    public String getReviewURL() {
        return review_url;
    }

    public String toString() {
        return mJSONObject.toString();
    }

}
