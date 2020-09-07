package com.weebly.hectorjorozco.sightwordstest.utils;

import androidx.fragment.app.FragmentManager;
import android.text.Html;

import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ShareDialogFragment;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;

public class DialogFragmentUtils {

    /**
     * Helper method that show a DialogFragment that lets the user select how to share students' results
     * @param title The title of the Dialog Fragment
     * @param tag The tag of the Dialog Fragment
     * @param isClassShared True if MainActivity creates this fragment to share the whole class results,
     *                      False if StudentTestResultsFragment creates this DialogFragment to share
     *                      a particular student test results.
     * @param fragmentManager The fragment manager used to display the DialogFragment
     */
    public static void showShareDialogFragment(String title, String tag, boolean isClassShared, boolean isDefaultTest,
                                               FragmentManager fragmentManager) {
        ShareDialogFragment shareDialogFragment = ShareDialogFragment.newInstance(title, isClassShared, isDefaultTest);

        // If the student test results are being shared
        if (!isClassShared) {
            // Sets StudentTestResultsFragment as the target fragment to get results from shareDialogFragment
            shareDialogFragment.setTargetFragment(fragmentManager.getFragments().get(0), 300);
        }
        shareDialogFragment.show(fragmentManager, tag);
    }

    // Helper method that show a DialogFragment that shows info about the share options
    public static void showInfoMessageDialogFragment(String message, String tag, byte caller, boolean isDefaultTest,
                                                    FragmentManager fragmentManager) {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(message),
                        EMPTY_STRING, isDefaultTest, caller, EMPTY_STRING);

        if (fragmentManager != null) {
            messageDialogFragment.show(fragmentManager, tag);
        }
    }
}
