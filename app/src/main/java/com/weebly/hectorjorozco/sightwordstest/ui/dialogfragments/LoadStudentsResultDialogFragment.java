package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.SharedPreferencesUtils;

import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.LOAD_STUDENTS_RESULT_DIALOG_FRAGMENT;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.MAX_NUMBER_OF_STUDENTS_IN_A_CLASS;


public class LoadStudentsResultDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_NUMBER_OF_STUDENTS_LOADED_ARGUMENT_KEY = "number_of_students_loaded";
    private static final String DIALOG_FRAGMENT_NUMBER_OF_STUDENTS_NOT_LOADED_ARGUMENT_KEY = "number_of_students_not_loaded";
    private static final String DIALOG_FRAGMENT_STUDENTS_LOADED_INFO_ARGUMENT_KEY = "students_loaded_info";
    private static final String DIALOG_FRAGMENT_STUDENTS_NOT_LOADED_INFO_ARGUMENT_KEY = "students_not_loaded_info";
    private static final String DIALOG_FRAGMENT_MAX_NUMBER_OF_STUDENTS_REACHED_ARGUMENT_KEY = "max_number_of_students_reached";


    public LoadStudentsResultDialogFragment() {
        // Empty constructor is required for DialogFragment
    }


    public static LoadStudentsResultDialogFragment newInstance(String title,
                                                               int numberOfStudentsLoaded,
                                                               int numberOfStudentsNotLoaded,
                                                               String studentsLoadedInfo,
                                                               String studentsNotLoadedInfo,
                                                               boolean maxNumberOfStudentsReached) {
        LoadStudentsResultDialogFragment loadStudentsResultDialogFragment =
                new LoadStudentsResultDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putInt(DIALOG_FRAGMENT_NUMBER_OF_STUDENTS_LOADED_ARGUMENT_KEY, numberOfStudentsLoaded);
        bundle.putInt(DIALOG_FRAGMENT_NUMBER_OF_STUDENTS_NOT_LOADED_ARGUMENT_KEY, numberOfStudentsNotLoaded);
        bundle.putString(DIALOG_FRAGMENT_STUDENTS_LOADED_INFO_ARGUMENT_KEY, studentsLoadedInfo);
        bundle.putString(DIALOG_FRAGMENT_STUDENTS_NOT_LOADED_INFO_ARGUMENT_KEY, studentsNotLoadedInfo);
        bundle.putBoolean(DIALOG_FRAGMENT_MAX_NUMBER_OF_STUDENTS_REACHED_ARGUMENT_KEY, maxNumberOfStudentsReached);
        loadStudentsResultDialogFragment.setArguments(bundle);
        return loadStudentsResultDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
        final int numberOfStudentsLoaded = bundle.getInt(DIALOG_FRAGMENT_NUMBER_OF_STUDENTS_LOADED_ARGUMENT_KEY);
        final int numberOfStudentsNotLoaded = bundle.getInt(DIALOG_FRAGMENT_NUMBER_OF_STUDENTS_NOT_LOADED_ARGUMENT_KEY);
        final String studentsLoadedInfo = bundle.getString(DIALOG_FRAGMENT_STUDENTS_LOADED_INFO_ARGUMENT_KEY);
        final String studentsNotLoadedInfo = bundle.getString(DIALOG_FRAGMENT_STUDENTS_NOT_LOADED_INFO_ARGUMENT_KEY);
        boolean maxNumberOfStudentsReached = bundle.getBoolean(DIALOG_FRAGMENT_MAX_NUMBER_OF_STUDENTS_REACHED_ARGUMENT_KEY);

        int colorDark = getResources().getColor(R.color.colorPrimaryDark);

        String colorDarkString = Integer.toHexString(colorDark & 0x00ffffff);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color, colorDarkString, title)));

        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_load_students_result, null);
        builder.setView(view);

        String studentsLoadedStudentWordSuffix;
        if (numberOfStudentsLoaded == 1) {
            studentsLoadedStudentWordSuffix = EMPTY_STRING;
        } else {
            studentsLoadedStudentWordSuffix = "s";
        }

        String studentsNotLoadedStudentWordSuffix;
        if (numberOfStudentsNotLoaded == 1) {
            studentsNotLoadedStudentWordSuffix = EMPTY_STRING;
        } else {
            studentsNotLoadedStudentWordSuffix = "s";
        }

        TextView maxNumberOfStudentsReachedTextView =
                view.findViewById(R.id.dialog_fragment_load_students_result_max_number_of_students_reached_text_view);
        TextView numberOfStudentsLoadedTextView =
                view.findViewById(R.id.dialog_fragment_load_students_result_loaded_text_view);
        TextView numberOfStudentsNotLoadedTextView =
                view.findViewById(R.id.dialog_fragment_load_students_result_not_loaded_text_view);

        maxNumberOfStudentsReachedTextView.setText(Html.fromHtml(getString(R.string.max_reached_message_text_2,
                MAX_NUMBER_OF_STUDENTS_IN_A_CLASS)));
        numberOfStudentsLoadedTextView.setText(
                getString(R.string.dialog_fragment_load_students_result_number_of_students_loaded_text,
                        numberOfStudentsLoaded, studentsLoadedStudentWordSuffix));
        numberOfStudentsNotLoadedTextView.setText(
                getString(R.string.dialog_fragment_load_students_result_number_of_students_not_loaded_text,
                        numberOfStudentsNotLoaded, studentsNotLoadedStudentWordSuffix));

        ImageView studentsLoadedInfoImageView =
                view.findViewById(R.id.dialog_fragment_load_students_result_loaded_info_image_view);
        ImageView studentsNotLoadedInfoImageView =
                view.findViewById(R.id.dialog_fragment_load_students_result_not_loaded_info_image_view);

        if (numberOfStudentsLoaded == 0) {
            studentsLoadedInfoImageView.setVisibility(View.INVISIBLE);
        } else {
            studentsLoadedInfoImageView.setVisibility(View.VISIBLE);
        }

        if (numberOfStudentsNotLoaded == 0) {
            studentsNotLoadedInfoImageView.setVisibility(View.INVISIBLE);
        } else {
            studentsNotLoadedInfoImageView.setVisibility(View.VISIBLE);
        }

        studentsLoadedInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialogFragment(studentsLoadedInfo,
                        getString(R.string.dialog_fragment_students_loaded_info_title, numberOfStudentsLoaded));
            }
        });

        studentsNotLoadedInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialogFragment(studentsNotLoadedInfo,
                        getString(R.string.dialog_fragment_students_not_loaded_info_title, numberOfStudentsNotLoaded));
            }
        });

        CheckBox checkBox = view.findViewById(R.id.dialog_fragment_load_students_result_check_box);
        if (!SharedPreferencesUtils.getShowLoadStudentsInfoValueFromSharedPreferences(Objects.requireNonNull(getContext()))) {
            int[][] states = {{android.R.attr.state_checked}, {}};
            int[] colors = {getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorPrimaryLight)};
            CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferencesUtils.setShowLoadStudentsInfoValueOnSharedPreferences(
                            Objects.requireNonNull(getContext()), isChecked);
                }
            });
            checkBox.setVisibility(View.VISIBLE);
        } else{
            checkBox.setVisibility(View.GONE);
        }

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        if (maxNumberOfStudentsReached) {
            alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
            maxNumberOfStudentsReachedTextView.setVisibility(View.VISIBLE);
        } else {
            if (numberOfStudentsNotLoaded > 0) {
                alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
            }
            maxNumberOfStudentsReachedTextView.setVisibility(View.GONE);
        }


        alertDialog.show();

        // Sets the color of the positive and negative buttons.
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(colorDark);

        return alertDialog;
    }

    // Helper method that show a DialogFragment that shows info about loaded or not loaded students
    private void showInfoDialogFragment(String text, String title) {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(text), title, true, LOAD_STUDENTS_RESULT_DIALOG_FRAGMENT, EMPTY_STRING);

        if (getFragmentManager() != null) {
            messageDialogFragment.show(getFragmentManager(),
                    getString(R.string.about_dialog_fragment_tag));
        }
    }

}