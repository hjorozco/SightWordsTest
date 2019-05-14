package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.weebly.hectorjorozco.sightwordstest.R;

public class NumberOfStudentsUtils {

    private static final int ZERO = 0;

    /**
     * Gets the number of students from Shared Preferences
     *
     * @return the number of students
     */
    public static int getNumberOfStudentsFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), ZERO);
        return sharedPreferences.getInt(context.getString(
                R.string.number_of_students_shared_preference_key), ZERO);
    }


    /**
     * Sets the number of students on Shared Preferences
     *
     * @param context The context used to get resources
     * @param numberOfStudents   The number of students that will be saved
     */
    public static void setNumberOfStudentsOnSharedPreferences(Context context, int numberOfStudents) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), ZERO);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.number_of_students_shared_preference_key), numberOfStudents);
        editor.apply();
    }

}

