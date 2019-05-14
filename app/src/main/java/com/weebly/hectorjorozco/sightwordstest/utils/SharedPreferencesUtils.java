package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.weebly.hectorjorozco.sightwordstest.R;

public class SharedPreferencesUtils {

    /**
     * Gets the Students order value from Shared Preferences
     *
     * @return An integer that represents the Students order on the RecyclerView of activity_movies.xml
     * 0 = Order by First Name, 1 = Order by Last Name, 2 = Order by Test result
     */
    public static boolean getShowLoadStudentsInfoValueFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), 0);
        return sharedPreferences.getBoolean(context.getString(
                R.string.show_load_students_info_key), true);
    }

    /**
     * Sets the student order value on Shared Preferences
     *
     * @param context The context used to get resources
     * @param showInfo True if the Load Students information Dialog Fragment is going to be shown.
     *                 False otherwise.
     */
    public static void setShowLoadStudentsInfoValueOnSharedPreferences(Context context, boolean showInfo) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.shared_preferences_name), 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(context.getString(R.string.show_load_students_info_key), showInfo);
            editor.apply();
    }




}

