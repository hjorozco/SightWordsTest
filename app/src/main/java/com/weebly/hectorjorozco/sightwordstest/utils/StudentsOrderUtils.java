package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;

import java.util.Collections;
import java.util.List;

public class StudentsOrderUtils {

    private static final int ZERO = 0;
    private static final String EMPTY_STRING = "";

    // Position values on the menu Spinner
    private static final int ORDER_BY_FIRST_NAME = ZERO;
    private static final int ORDER_BY_LAST_NAME = 1;
    private static final int ORDER_BY_TEST_RESULT = 2;
    private static final int ORDER_BY_TEST_TYPE = 3;

    /**
     * Gets the Students order value from Shared Preferences
     *
     * @return An integer that represents the Students order on the RecyclerView of activity_movies.xml
     * 0 = Order by First Name, 1 = Order by Last Name, 2 = Order by Test result
     */
    public static int getStudentsOrderValueFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), ZERO);
        return sharedPreferences.getInt(context.getString(
                R.string.students_order_shared_preference_key), ORDER_BY_LAST_NAME);
    }


    /**
     * Sets the student order value on Shared Preferences
     *
     * @param context The context used to get resources
     * @param value   The student order value that will be saved
     */
    public static boolean setStudentOrderValueOnSharedPreferences(Context context, int value) {
        // If the spinner selected value is different from the previous one save the value
        if (value != getStudentsOrderValueFromSharedPreferences(context)) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.shared_preferences_name), ZERO);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(context.getString(R.string.students_order_shared_preference_key), value);
            editor.apply();

            return true;
        }

        return false;
    }


    public static String getStudentsOrderString(Context context) {
        int value = getStudentsOrderValueFromSharedPreferences(context);
        String studentOrder;
        switch (value) {
            case ORDER_BY_FIRST_NAME:
                studentOrder = context.getString(R.string.activity_main_sorted_by_first_name_message);
                break;
            case ORDER_BY_LAST_NAME:
                studentOrder = context.getString(R.string.activity_main_sorted_by_last_name_message);
                break;
            case ORDER_BY_TEST_RESULT:
                studentOrder = context.getString(R.string.activity_main_sorted_by_test_result_message);
                break;
            case ORDER_BY_TEST_TYPE:
                studentOrder = context.getString(R.string.activity_main_sorted_by_test_type_message);
                break;
            default:
                studentOrder = EMPTY_STRING;
        }

        return studentOrder;
    }


    /**
     * Orders a list of students based on the order value saved on SharedPreferences.
     *
     * @param context        Used to access SharedPreferences
     * @param studentEntries The list of students to be ordered
     * @return an ordered list of students
     */
    public static List<StudentEntry> UpdateStudentsListOrder(Context context, List<StudentEntry> studentEntries) {

        switch (getStudentsOrderValueFromSharedPreferences(context)) {
            case ORDER_BY_FIRST_NAME:
                Collections.sort(studentEntries, StudentEntry.LastNameComparator);
                Collections.sort(studentEntries, StudentEntry.FirstNameComparator);
                break;
            case ORDER_BY_LAST_NAME:
                Collections.sort(studentEntries, StudentEntry.FirstNameComparator);
                Collections.sort(studentEntries, StudentEntry.LastNameComparator);
                break;
            case ORDER_BY_TEST_RESULT:
                Collections.sort(studentEntries, StudentEntry.FirstNameComparator);
                Collections.sort(studentEntries, StudentEntry.LastNameComparator);
                Collections.sort(studentEntries, StudentEntry.TestResultComparator);
                Collections.sort(studentEntries, StudentEntry.TestTypeComparator);
                break;
            case ORDER_BY_TEST_TYPE:
                Collections.sort(studentEntries, StudentEntry.FirstNameComparator);
                Collections.sort(studentEntries, StudentEntry.LastNameComparator);
                Collections.sort(studentEntries, StudentEntry.TestTypeComparator);
                break;
            default:
                break;
        }

        return studentEntries;
    }

}

