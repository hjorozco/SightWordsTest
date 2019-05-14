package com.weebly.hectorjorozco.sightwordstest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.Spanned;

import com.weebly.hectorjorozco.sightwordstest.R;

import java.util.ArrayList;
import java.util.List;

public class TestTypeUtils {

    private static final int ZERO = 0;

    /**
     * Gets the default test selection value from Shared Preferences
     *
     * @return An integer that represents the default test selection for the classroom (individual
     * students can have a different test selection saved on the DB students table).
     */
    public static int getDefaultTestTypeValueFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name), ZERO);
        return sharedPreferences.getInt(context.getString(
                R.string.test_selection_shared_preference_key), ZERO);
    }


    /**
     * Sets the default test selection value on Shared Preferences
     *
     * @param context The context used to get resources
     * @param value   The test selection value that will be saved
     */
    public static void setDefaultTestTypeValueOnSharedPreferences(Context context, int value) {
        // If the spinner selected value is different from the previous one save the value
        if (value != getDefaultTestTypeValueFromSharedPreferences(context)) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.shared_preferences_name), ZERO);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(context.getString(R.string.test_selection_shared_preference_key), value);
            editor.apply();
        }
    }


    /**
     * Gets a text that shows the type of test and the number of words on it from Shared Preferences.
     *
     * @param context The context used to get resources
     * @param widget  True if the test type title is for the widget, false otherwise.
     * @return a String with the type of tests and the number of words on it
     */
    public static String getDefaultTestTypeTitleFromSharedPreferences(Context context, boolean widget) {

        int defaultTestType = getDefaultTestTypeValueFromSharedPreferences(context);
        String appNumberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                getColor(R.color.colorPrimaryLight) & 0x00ffffff);

        String widgetNumberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                getColor(R.color.colorWhite80PercentTransparency));

        if (widget) {
            return context.getString(R.string.widget_test_type_text,
                    getTestTypeString(context, defaultTestType),
                    widgetNumberOfWordsOnTestColorHexValue,
                    String.valueOf(WordUtils.getNumberOfWordsOnList(defaultTestType)));
        } else {
            return context.getString(R.string.test_type_text,
                    getTestTypeString(context, defaultTestType),
                    appNumberOfWordsOnTestColorHexValue,
                    String.valueOf(WordUtils.getNumberOfWordsOnList(defaultTestType)));
        }
    }


    /**
     * Gets a text that shows the type of test and the number of words on it
     *
     * @param context     The context used to access resources.
     * @param isDefaultTest True if the student test is the default for the class, false otherwise
     * @param testType    The test type that the student is being tested on.
     * @return a String title
     */
    public static String getTestTypeTitle(Context context, int testType, boolean isDefaultTest) {

        String numberOfWordsOnTestColorHexValue;

        if (isDefaultTest) {
            numberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                    getColor(R.color.colorPrimaryLight) & 0x00ffffff);
        } else {
            numberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                    getColor(R.color.colorSecondaryLight) & 0x00ffffff);
        }

        return context.getString(R.string.test_type_text,
                TestTypeUtils.getTestTypeString(context, testType),
                numberOfWordsOnTestColorHexValue,
                String.valueOf(WordUtils.getNumberOfWordsOnList(testType)));
    }


    /**
     * Gets a text that shows the type of test and the number of words on it
     *
     * @param context     The context used to access resources.
     * @param isDefaultTest True if the student test is the default for the class, false otherwise
     * @param testType    The test type that the student is being tested on.
     * @return a String title
     */
    public static String getTestTypeTitleForStudentTestResultsFragment(Context context, int testType, boolean isDefaultTest) {

        String numberOfWordsOnTestColorHexValue;

        if (isDefaultTest) {
            numberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                    getColor(R.color.colorPrimaryLight) & 0x00ffffff);
        } else {
            numberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                    getColor(R.color.colorSecondaryLight) & 0x00ffffff);
        }

        return context.getString(R.string.test_type_for_students_test_result_fragment_text,
                TestTypeUtils.getTestTypeString(context, testType),
                numberOfWordsOnTestColorHexValue,
                String.valueOf(WordUtils.getNumberOfWordsOnList(testType)));
    }


    /**
     * Returns the name of the test that the student will be tested on
     *
     * @param context  Used to get string resources.
     * @param testType A number representing the test type
     * @return A string with the name of the test type
     */
    public static String getTestTypeString(Context context, int testType) {
        return context.getResources().getStringArray(R.array.test_types)[testType];
    }


    /**
     * Returns a list of Spanned that will be used on test selector spinners
     *
     * @param context The context used to get resources
     * @param dolch   True if the list to return is a list of Dolch tests, false if the list to return
     *                is a list of Fry tests.
     * @return A list of spanned
     */
    public static List<Spanned> getTestSelectorSpinnerArray(Context context, boolean dolch, boolean isDefaultTest) {

        int colorLight;

        if (isDefaultTest) {
            colorLight = R.color.colorPrimaryLight;
        } else {
            colorLight = R.color.colorSecondaryLight;
        }

        String numberOfWordsOnTestColorHexValue = Integer.toHexString(context.getResources().
                getColor(colorLight) & 0x00ffffff);

        List<Spanned> testSelectorSpinnerArray = new ArrayList<>();

        if (dolch) {
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_dolch_prek, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_dolch_k, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_dolch_1st_grade, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_dolch_2nd_grade, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_dolch_3rd_grade, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_dolch_nouns, numberOfWordsOnTestColorHexValue)));
        } else {
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_1st, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_2nd, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_3rd, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_4th, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_5th, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_6th, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_7th, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_8th, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_9th, numberOfWordsOnTestColorHexValue)));
            testSelectorSpinnerArray.add(Html.fromHtml(
                    context.getString(R.string.test_selector_spinner_fry_10th, numberOfWordsOnTestColorHexValue)));
        }

        return testSelectorSpinnerArray;
    }

}

