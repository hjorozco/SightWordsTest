package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;

import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.DELETE_OLDEST_STUDENT_RESULTS_DIALOG_FRAGMENT;


public class DeleteOldestStudentResultsDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_STUDENT_NAME_ARGUMENT_KEY = "student_name";
    private static final String DIALOG_FRAGMENT_NUMBER_OF_TEST_ARGUMENT_KEY = "number_of_tests";
    private static final String DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY = "is_default_test";


    public interface DeleteOldestStudentResultsDialogFragmentListener {
        void onDeleteOldestStudentResults(int numberOfTestsToDelete);
    }


    public DeleteOldestStudentResultsDialogFragment() {
        // Empty constructor is required for DialogFragment
    }


    public static DeleteOldestStudentResultsDialogFragment newInstance(String studentName, int numberOfTests, boolean isDefaultTest) {
        DeleteOldestStudentResultsDialogFragment deleteOldestStudentResultsDialogFragment =
                new DeleteOldestStudentResultsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_STUDENT_NAME_ARGUMENT_KEY, studentName);
        bundle.putInt(DIALOG_FRAGMENT_NUMBER_OF_TEST_ARGUMENT_KEY, numberOfTests);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY, isDefaultTest);
        deleteOldestStudentResultsDialogFragment.setArguments(bundle);
        return deleteOldestStudentResultsDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String studentName = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_STUDENT_NAME_ARGUMENT_KEY);
        final int numberOfTests = Objects.requireNonNull(bundle).getInt(DIALOG_FRAGMENT_NUMBER_OF_TEST_ARGUMENT_KEY);
        final boolean isDefaultTest = bundle.getBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY);

        int colorDark;
        int colorLight;

        if (isDefaultTest) {
            colorDark = getResources().getColor(R.color.colorPrimaryDark);
            colorLight = getResources().getColor(R.color.colorPrimaryLight);
        } else {
            colorDark = getResources().getColor(R.color.colorSecondaryDark);
            colorLight = getResources().getColor(R.color.colorSecondaryLight);
        }

        String colorDarkString = Integer.toHexString(colorDark & 0x00ffffff);
        String title = getString(R.string.menu_student_test_results_action_delete_old_results_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color, colorDarkString, title)));
        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_delete_oldest_student_results, null);
        builder.setView(view);

        TextView textView = view.findViewById(R.id.dialog_fragment_delete_oldest_student_results_text_view);
        final MaterialEditText numberOfTestsToDeleteEditText = view.findViewById(R.id.dialog_fragment_delete_oldest_student_results_edit_text);
        ImageView infoImageView = view.findViewById(R.id.dialog_fragment_delete_oldest_student_results_image_view);

        // If the student has only 2 test, only one can be deleted
        if (numberOfTests == 2) {
            textView.setText(Html.fromHtml(getString(R.string.dialog_fragment_delete_oldest_student_results_text_view_one_test_to_delete_text, studentName)));
            numberOfTestsToDeleteEditText.setVisibility(View.GONE);
            infoImageView.setVisibility(View.GONE);
        } else {

            // If the student has more than 2 tests let the user decide how many tests he wants to delete
            numberOfTestsToDeleteEditText.setVisibility(View.VISIBLE);
            infoImageView.setVisibility(View.VISIBLE);

            textView.setText(Html.fromHtml(getString(R.string.dialog_fragment_delete_oldest_student_results_text_view_text, numberOfTests, studentName)));

            infoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragmentUtils.showInfoMessageDialogFragment(getString(R.string.dialog_fragment_delete_oldest_student_results_info_message),
                            getString(R.string.dialog_fragment_delete_oldest_student_results_info_tag),
                            DELETE_OLDEST_STUDENT_RESULTS_DIALOG_FRAGMENT, isDefaultTest, getFragmentManager());
                }
            });


            numberOfTestsToDeleteEditText.setTextColor(colorDark);
            numberOfTestsToDeleteEditText.setPrimaryColor(colorDark);
            numberOfTestsToDeleteEditText.setUnderlineColor(colorLight);
            numberOfTestsToDeleteEditText.setHint(getString(R.string.dialog_fragment_delete_oldest_student_results_edit_text_message, numberOfTests - 1));
        }

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This method will be overridden
            }
        });

        builder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (isDefaultTest) {
            alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
        } else {
            alertDialog.setIcon(R.drawable.ic_warning_brown_24dp);
        }

        alertDialog.show();

        // Sets the color of the positive and negative buttons.
        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(colorDark);
        button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorDark);

        // Override the positive button OnClickListener to modify its default behavior of closing
        // the AlertDialog after click.
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteOldestStudentResultsDialogFragmentListener listener = (DeleteOldestStudentResultsDialogFragmentListener) getTargetFragment();

                // If the student has only 2 tests only 1 (the oldest one) will be deleted
                if (numberOfTests == 2) {

                    if (listener != null) {
                        listener.onDeleteOldestStudentResults(1);
                    }
                    dismiss();
                } else {

                    // If the student has more than 2 tests get the number of tests that the user wants to delete
                    // (validate it between 1 and the number of tests the student has minus 1.
                    Editable numberOfTestsToDeleteText = numberOfTestsToDeleteEditText.getText();

                    if (numberOfTestsToDeleteText != null && numberOfTestsToDeleteText.length() != 0) {
                        int numberOfTestsToDelete = Integer.valueOf(numberOfTestsToDeleteText.toString());

                        if (numberOfTestsToDelete == 0) {
                            numberOfTestsToDeleteEditText.setError(getString(R.string.dialog_fragment_delete_oldest_student_results_edit_text_error_message, numberOfTests - 1));
                        } else {
                            if (numberOfTestsToDelete > numberOfTests - 1) {
                                numberOfTestsToDeleteEditText.setError(getString(R.string.dialog_fragment_delete_oldest_student_results_edit_text_error_message, numberOfTests - 1));
                            } else {
                                if (listener != null) {
                                    listener.onDeleteOldestStudentResults(numberOfTestsToDelete);
                                }
                                dismiss();
                            }
                        }
                    } else {
                        numberOfTestsToDeleteEditText.setError(getString(R.string.dialog_fragment_delete_oldest_student_results_edit_text_error_message, numberOfTests - 1));
                    }
                }
            }
        });

        return alertDialog;
    }

}