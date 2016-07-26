package com.mosemos.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_tempUnit_key),
                context.getString(R.string.pref_tempUnit_metric))
                .equals(context.getString(R.string.pref_tempUnit_metric));
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateStr) {
        SimpleDateFormat dbSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat uxSimpleDateFormat = new SimpleDateFormat("EEE, MMM dd");

        String formattedDateString = null;
        try {
            formattedDateString = uxSimpleDateFormat.format(dbSimpleDateFormat.parse(dateStr));
        } catch (ParseException e) {
            Log.e("Utility", "Cannot convert date: " + dateStr);
            e.printStackTrace();
        }

        return formattedDateString;
    }
}