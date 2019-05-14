package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.StudentsOrderUtils;

import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SORT_BY_FIRST_NAME_VALUE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SORT_BY_LAST_NAME_VALUE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SORT_BY_TEST_RESULT_VALUE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SORT_BY_TEST_TYPE_VALUE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SORT_STUDENTS_DIALOG_FRAGMENT;

public class SortStudentsDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private int mSortCriteriaSelected;


    public interface SortStudentsDialogFragmentListener {
        void onSortStudents(int sortCriteriaSelected);
    }


    public SortStudentsDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static SortStudentsDialogFragment newInstance(String title) {
        SortStudentsDialogFragment sortStudentsDialogFragment =
                new SortStudentsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        sortStudentsDialogFragment.setArguments(bundle);
        return sortStudentsDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int colorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);
        String colorPrimaryDarkString = Integer.toHexString(colorPrimaryDark & 0x00ffffff);
        int radioButtonToCheckId=0;

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color, colorPrimaryDarkString, title)));

        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_sort_students, null);
        builder.setView(view);

        final RadioGroup sortByRadioGroup = view.findViewById(R.id.dialog_fragment_sort_students_by_radio_group);

        switch (StudentsOrderUtils.getStudentsOrderValueFromSharedPreferences(getActivity())){
            case SORT_BY_FIRST_NAME_VALUE:
                radioButtonToCheckId = R.id.dialog_fragment_sort_students_by_first_name_radio_button;
                break;
            case SORT_BY_LAST_NAME_VALUE:
                radioButtonToCheckId = R.id.dialog_fragment_sort_students_by_last_name_radio_button;
                break;
            case SORT_BY_TEST_RESULT_VALUE:
                radioButtonToCheckId = R.id.dialog_fragment_sort_students_by_test_result_radio_button;
                break;
            case SORT_BY_TEST_TYPE_VALUE:
                radioButtonToCheckId = R.id.dialog_fragment_sort_students_by_test_type_radio_button;
                break;
        }

        sortByRadioGroup.check(radioButtonToCheckId);

        view.findViewById(R.id.dialog_fragment_sort_students_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentUtils.showInfoMessageDialogFragment(getString(R.string.sort_students_dialog_fragment_info_text),
                        getString(R.string.sort_students_dialog_fragment_info_tag), SORT_STUDENTS_DIALOG_FRAGMENT, true, getFragmentManager());
            }
        });

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (sortByRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.dialog_fragment_sort_students_by_first_name_radio_button:
                        mSortCriteriaSelected = SORT_BY_FIRST_NAME_VALUE;
                        break;
                    case R.id.dialog_fragment_sort_students_by_last_name_radio_button:
                        mSortCriteriaSelected =SORT_BY_LAST_NAME_VALUE;
                        break;
                    case R.id.dialog_fragment_sort_students_by_test_result_radio_button:
                        mSortCriteriaSelected =SORT_BY_TEST_RESULT_VALUE;
                        break;
                    case R.id.dialog_fragment_sort_students_by_test_type_radio_button:
                        mSortCriteriaSelected =SORT_BY_TEST_TYPE_VALUE;
                        break;
                }

                SortStudentsDialogFragmentListener listener = (SortStudentsDialogFragmentListener) getActivity();
                Objects.requireNonNull(listener).onSortStudents(mSortCriteriaSelected);
                dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        // Sets the color of the positive and negative buttons.
        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(colorPrimaryDark);
        button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorPrimaryDark);

        return alertDialog;
    }

}