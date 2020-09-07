package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;

import com.weebly.hectorjorozco.sightwordstest.R;

import java.util.Objects;

/**
 * Dialog Fragment class that shows a yes/no confirmation alert dialog
 */
public class ConfirmationDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY = "message";
    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY = "primary_color";
    private static final String DIALOG_FRAGMENT_STUDENT_TO_DELETE_POSITION_ARGUMENT_KEY = "student_to_delete_position";
    private static final String DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY = "caller";

    // Values used to identify who is calling the ConfirmationDialogFragment
    public static final byte MAIN_ACTIVITY_DELETE_STUDENT = 0;
    public static final byte MAIN_ACTIVITY_DELETE_STUDENTS = 1;
    public static final byte MAIN_ACTIVITY_DELETE_CLASS = 2;
    public static final byte MAIN_ACTIVITY_FEEDBACK = 3;
    public static final byte MAIN_ACTIVITY_DONATE = 4;
    public static final byte MAIN_ACTIVITY_CREATE_SAMPLE_CLASS = 5;
    public static final byte STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TEST = 6;
    public static final byte STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TESTS = 7;
    public static final byte STUDENT_TEST_RESULTS_FRAGMENT_DELETE_ALL_TESTS = 8;
    public static final byte TEST_ACTIVITY_SAVE_TEST = 9;


    public interface ConfirmationDialogFragmentListener {
        void onConfirmation(boolean answerYes, int studentToDeletePosition, byte dialogFragmentCallerType);
    }

    public ConfirmationDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static ConfirmationDialogFragment newInstance(CharSequence message, String title,
                                                         boolean isDefaultTest, int itemToDeletePosition,
                                                         byte caller) {
        ConfirmationDialogFragment confirmationDialogFragment =
                new ConfirmationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY, message);
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY, isDefaultTest);
        bundle.putInt(DIALOG_FRAGMENT_STUDENT_TO_DELETE_POSITION_ARGUMENT_KEY, itemToDeletePosition);
        bundle.putByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY, caller);
        confirmationDialogFragment.setArguments(bundle);
        confirmationDialogFragment.setCancelable(false);
        return confirmationDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int colorDark;
        Bundle arguments = getArguments();

        CharSequence message = null;
        String title = getString(R.string.activity_main_dialog_fragment_error);
        boolean isDefaultTest = true;
        int itemToDeletePosition = 0;
        byte caller = 0;

        if (arguments != null) {
            message = arguments.getCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY,
                    getString(R.string.activity_main_dialog_fragment_error));
            title = arguments.getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
            isDefaultTest = arguments.getBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY);
            itemToDeletePosition = arguments.getInt(DIALOG_FRAGMENT_STUDENT_TO_DELETE_POSITION_ARGUMENT_KEY);
            caller = arguments.getByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY);
        }

        // Ask if the user is sure about deleting the student
        int alertDialogStyle;
        if (isDefaultTest) {
            alertDialogStyle = R.style.ThemeDialogCustomPrimaryColor;
            colorDark = getResources().getColor(R.color.colorPrimaryDark);
        } else {
            alertDialogStyle = R.style.ThemeDialogCustomSecondaryColor;
            colorDark = getResources().getColor(R.color.colorSecondaryDark);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()),
                alertDialogStyle);

        builder.setMessage(message).setTitle(title);

        ConfirmationDialogFragmentListener listener;
        // If a fragment created this Dialog Fragment
        if (caller == STUDENT_TEST_RESULTS_FRAGMENT_DELETE_ALL_TESTS || caller == STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TEST
                || caller == STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TESTS) {
            listener = (ConfirmationDialogFragmentListener) getTargetFragment();
        } else {
            // If an activity created this Dialog Fragment
            listener = (ConfirmationDialogFragmentListener) getActivity();
        }

        final ConfirmationDialogFragmentListener listenerFinal = listener;
        final byte finalCaller = caller;
        final int finalItemToDeletePosition = itemToDeletePosition;

        builder.setPositiveButton(R.string.delete_student_alert_dialog_positive_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Objects.requireNonNull(listenerFinal).onConfirmation
                        (true, finalItemToDeletePosition, finalCaller);
            }
        });

        builder.setNegativeButton(R.string.delete_student_alert_dialog_negative_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Objects.requireNonNull(listenerFinal).onConfirmation
                        (false, finalItemToDeletePosition, finalCaller);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (caller == MAIN_ACTIVITY_DELETE_CLASS || caller == MAIN_ACTIVITY_DELETE_STUDENTS) {
            alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
        } else if (caller == MAIN_ACTIVITY_DELETE_STUDENT
                || caller == STUDENT_TEST_RESULTS_FRAGMENT_DELETE_ALL_TESTS
                || caller == STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TESTS
                || caller == STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TEST){
            if (isDefaultTest) {
                alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
            } else {
                alertDialog.setIcon(R.drawable.ic_warning_brown_24dp);
            }
        }

        alertDialog.show();

        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorDark);
        button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(colorDark);

        return alertDialog;
    }

}
