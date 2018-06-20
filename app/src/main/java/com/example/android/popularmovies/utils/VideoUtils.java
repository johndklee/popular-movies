package com.example.android.popularmovies.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * VideoUtils
 *
 * convenient ways to watch video
 */
public class VideoUtils {

    private static final String TAG = VideoUtils.class.getSimpleName();

    /**
     *
     * Using the Youtube video id, launch either Youtube app (if available) or web browser to watch
     * Based on https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     *
     * @param context Context to start Activity
     * @param id Youtube video id
     */
    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + id));
        try {
            // check PackageManager to see whether Youtube app is installed on the device
            if (appIntent.resolveActivity(context.getPackageManager()) != null) {
                Log.d(TAG, "trying to launch Youtube app: uri="+appIntent.getData());
                context.startActivity(appIntent);
                return;
            }
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "failed to launch Youtube app", ex);
        }
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        Log.d(TAG, "trying to launch web browser app: uri="+webIntent.getData());
        context.startActivity(webIntent);
    }

}
