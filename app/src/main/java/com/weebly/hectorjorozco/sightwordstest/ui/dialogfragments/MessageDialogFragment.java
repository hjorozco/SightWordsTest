package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;

import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.ADD_STUDENT_FRAGMENT;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.MAIN_ACTIVITY_ABOUT_MESSAGE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.MAIN_ACTIVITY_MAX_NUMBER_OF_STUDENTS_REACHED;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULT_FRAGMENT_MAX_NUMBER_OF_TESTS_REACHED;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.WORD_LISTS_INFORMATION_FRAGMENT;

public class MessageDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY = "message";
    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY = "is_default_test";
    private static final String DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY = "caller";
    private static final String DIALOG_FRAGMENT_ADDED_STUDENT_NAME_ARGUMENT_KEY = "added_student_name";


    public interface MaxNumberOfStudentsReachedListener {
        void onMaxNumberOfStudentsReached(String addedStudentName);
    }

    public interface MaxNumberOfTestsReachedListener {
        void onMaxNumberOfTestsReached();
    }


    public MessageDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static MessageDialogFragment newInstance(CharSequence message, String title, boolean isDefaultTest, byte caller, String addedStudentName) {

        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY, message);
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY, isDefaultTest);
        bundle.putByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY, caller);
        bundle.putString(DIALOG_FRAGMENT_ADDED_STUDENT_NAME_ARGUMENT_KEY, addedStudentName);
        messageDialogFragment.setArguments(bundle);
        return messageDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int colorDark;
        Bundle arguments = getArguments();

        CharSequence message = null;
        String title = getString(R.string.activity_main_dialog_fragment_error);
        boolean isDefaultTest = false;
        byte caller = 0;
        String addedStudentName = EMPTY_STRING;

        if (arguments != null) {
            message = arguments.getCharSequence(DIALOG_FRAGMENT_MESSAGE_ARGUMENT_KEY, "Hello");
            title = arguments.getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
            isDefaultTest = arguments.getBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY);
            caller = arguments.getByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY);
            addedStudentName = arguments.getString(DIALOG_FRAGMENT_ADDED_STUDENT_NAME_ARGUMENT_KEY);
        }

        AlertDialog.Builder builder;
        int alertDialogStyle;

        if (isDefaultTest) {
            alertDialogStyle = R.style.ThemeDialogCustomPrimaryColor;
            colorDark = getResources().getColor(R.color.colorPrimaryDark);
        } else {
            alertDialogStyle = R.style.ThemeDialogCustomSecondaryColor;
            colorDark = getResources().getColor(R.color.colorSecondaryDark);
        }

        builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()),
                alertDialogStyle);
        builder.setMessage(message).setTitle(title);
        final byte finalCaller = caller;
        final String finalAddedStudentName = addedStudentName;

        builder.setPositiveButton(R.string.alert_dialogs_positive_button_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (finalCaller == MAIN_ACTIVITY_MAX_NUMBER_OF_STUDENTS_REACHED) {
                            MaxNumberOfStudentsReachedListener listener = (MaxNumberOfStudentsReachedListener) getActivity();
                            if (listener != null) {
                                listener.onMaxNumberOfStudentsReached(finalAddedStudentName);
                            }
                        } else if (finalCaller == STUDENT_TEST_RESULT_FRAGMENT_MAX_NUMBER_OF_TESTS_REACHED) {
                            MaxNumberOfTestsReachedListener listener = (MaxNumberOfTestsReachedListener) getTargetFragment();
                            if (listener != null) {
                                listener.onMaxNumberOfTestsReached();
                            }
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (caller == ADD_STUDENT_FRAGMENT || caller == MAIN_ACTIVITY_MAX_NUMBER_OF_STUDENTS_REACHED
        || caller == STUDENT_TEST_RESULT_FRAGMENT_MAX_NUMBER_OF_TESTS_REACHED) {
            if (isDefaultTest) {
                alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
            } else {
                alertDialog.setIcon(R.drawable.ic_warning_brown_24dp);
            }
        }

        alertDialog.show();

        Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorDark);

        // If the message is for Word Lists Information, make the text selectable
        if (caller == WORD_LISTS_INFORMATION_FRAGMENT) {
            TextView textView = Objects.requireNonNull(alertDialog.getWindow()).getDecorView()
                    .findViewById(android.R.id.message);
            textView.setTextIsSelectable(true);
            textView.setHighlightColor(Objects.requireNonNull(getContext()).getResources()
                    .getColor(R.color.colorPrimaryMediumLight));
        }

        // If the message is for About information, make the links in text activated
        if (caller== MAIN_ACTIVITY_ABOUT_MESSAGE) {
            TextView textView = Objects.requireNonNull(alertDialog.getWindow()).getDecorView()
                    .findViewById(android.R.id.message);
            textView.setClickable(true);
            textView.setLinkTextColor(getResources().getColor(R.color.colorLinks));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return alertDialog;
    }

}
