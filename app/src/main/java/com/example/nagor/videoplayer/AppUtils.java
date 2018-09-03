package com.example.nagor.videoplayer;

import android.util.Log;

/**
 * Created by Nagor
 * on 9/3/2018.
 */

 class AppUtils {
     static String formatTime(int timeInSeconds) {
         Log.e("format Time"," "+timeInSeconds);
        String formattedTime = "00:00";
        try {
            timeInSeconds = timeInSeconds / 1000;
            int hours = timeInSeconds / 3600;
            int secondsLeft = timeInSeconds - hours * 3600;
            int minutes = secondsLeft / 60;
            int seconds = secondsLeft - minutes * 60;

            formattedTime = "";
            if (hours < 10 && hours > 0) {
                formattedTime += "0";
                formattedTime += hours + ":";
            }

            if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (seconds < 10)
                formattedTime += "0";
            formattedTime += seconds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedTime;
    }
}
