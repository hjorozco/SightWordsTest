package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.ui.WordListsInformationActivity;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;

import java.util.List;
import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.ADD_STUDENT_FRAGMENT;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_NO_TESTS;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_TESTS;

public class ChangeTestDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_TEST_TYPE_ARGUMENT_KEY = "test_type";
    private static final String DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY = "is_default_test";
    private static final String DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY = "caller";
    private int mTestTypeSelected;

    public interface ChangeTestDialogFragmentListener {
        void onChangeTest(int testType);
    }


    public ChangeTestDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    /**
     * Creates a new instance of this DialogFragment
     *
     * @param title    The title of the AlertDialog inside this DialogFragment
     * @param testType The test type before any change (used the initialize the RadioGroup and Spinner)
     * @param caller   Number that defines who is calling this fragment
     * @return a ChangeTestDialogFragment
     */
    public static ChangeTestDialogFragment newInstance(String title, int testType, boolean isDefaultTest, byte caller) {
        ChangeTestDialogFragment changeTestDialogFragment =
                new ChangeTestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putInt(DIALOG_FRAGMENT_TEST_TYPE_ARGUMENT_KEY, testType);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY, isDefaultTest);
        bundle.putByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY, caller);
        changeTestDialogFragment.setArguments(bundle);
        return changeTestDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
        final int testType = Objects.requireNonNull(bundle).getInt(DIALOG_FRAGMENT_TEST_TYPE_ARGUMENT_KEY);
        boolean isDefaultTest = bundle.getBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY);
        final byte caller = bundle.getByte(DIALOG_FRAGMENT_CALLER_ARGUMENT_KEY);

        final boolean isStudentChanged = caller == ADD_STUDENT_FRAGMENT || caller == STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_TESTS
                || caller == STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_NO_TESTS;

        int colorDark;
        int color;
        if (isDefaultTest) {
            colorDark = getResources().getColor(R.color.colorPrimaryDark);
            color = getResources().getColor(R.color.colorPrimary);
        } else {
            colorDark = getResources().getColor(R.color.colorSecondaryDark);
            color = getResources().getColor(R.color.colorSecondary);
        }

        String colorDarkString = Integer.toHexString(colorDark & 0x00ffffff);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color, colorDarkString, title)));

        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_change_test, null);
        builder.setView(view);

        setupTestSelector(view, testType, isDefaultTest);

        // Sets the color of the RadioGroup and Spinner
        RadioButton dolchRadioButton = view.findViewById(R.id.change_test_dialog_fragment_dolch_radio_button);
        RadioButton fryRadioButton = view.findViewById(R.id.change_test_dialog_fragment_fry_radio_button);
        fryRadioButton.setTextColor(colorDark);
        dolchRadioButton.setTextColor(colorDark);

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {colorDark, color};
        CompoundButtonCompat.setButtonTintList(dolchRadioButton, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(fryRadioButton, new ColorStateList(states, colors));

        ImageView infoImageView = view.findViewById(R.id.change_test_dialog_fragment_info_image_view);

        if (isDefaultTest) {
            infoImageView.setImageResource(R.drawable.ic_info_outline_color_primary_24dp);
        } else {
            infoImageView.setImageResource(R.drawable.ic_info_outline_color_secondary_24dp);
        }

        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WordListsInformationActivity.class);
                startActivity(intent);
            }
        });

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If the test type was changed send the selected test type to the appropriate
                // fragment or activity
                ChangeTestDialogFragmentListener listener;

                if (isStudentChanged) {
                    // If the test is being changed for a student, set the listener to the
                    // appropriate caller fragment )AddStudentFragment or StudentTestResultsFragment)
                    listener = (ChangeTestDialogFragmentListener) getTargetFragment();
                } else {
                    // If the default test is being changed, set the listener to MainActivity
                    // (the activity that created this Dialog Fragment)
                    listener = (ChangeTestDialogFragmentListener) getActivity();
                }

                Objects.requireNonNull(listener).onChangeTest(mTestTypeSelected);
            }
        });

        // If the test is being changed for a student add a neutral button that lets the user change the
        // student test to the default
        if (isStudentChanged) {
            builder.setNeutralButton(getString(R.string.change_to_default_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Send the default test type to the appropriate fragment
                    ChangeTestDialogFragmentListener listener = (ChangeTestDialogFragmentListener) getTargetFragment();
                    Objects.requireNonNull(listener).onChangeTest(
                            TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(Objects.requireNonNull(getContext())));
                }
            });
        }

        builder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        // If the caller of this fragment is StudentTestResults and the student has one or more tests
        // show a warning indicating that the previous test results will be deleted.
        if (caller == STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_TESTS) {
            if (isDefaultTest) {
                alertDialog.setIcon(R.drawable.ic_warning_blue_24dp);
            } else {
                alertDialog.setIcon(R.drawable.ic_warning_brown_24dp);
            }
            TextView warningMessageTextView = view.findViewById(R.id.change_test_dialog_fragment_warning_message_text_view);
            warningMessageTextView.setTextColor(colorDark);
            warningMessageTextView.setVisibility(View.VISIBLE);
        }

        alertDialog.show();

        // Sets the color of buttons
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(colorDark);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(colorDark);
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(colorDark);

        return alertDialog;
    }


    private void setupTestSelector(View view, int studentTestType, boolean isDefaultTest) {

        final Context context = getContext();

        List<Spanned> testSelectorSpinnerArray;
        int spinnerSelection;

        final List<Spanned> testSelectorSpinnerDolchArray =
                TestTypeUtils.getTestSelectorSpinnerArray(Objects.requireNonNull(context), true, isDefaultTest);

        final List<Spanned> testSelectorSpinnerFryArray =
                TestTypeUtils.getTestSelectorSpinnerArray(context, false, isDefaultTest);

        RadioButton dolchRadioButton = view.findViewById(R.id.change_test_dialog_fragment_dolch_radio_button);
        RadioButton fryRadioButton = view.findViewById(R.id.change_test_dialog_fragment_fry_radio_button);

        // If the selected test is one of the Dolch types
        if (studentTestType < 6) {
            dolchRadioButton.setChecked(true);
            fryRadioButton.setChecked(false);
            testSelectorSpinnerArray = testSelectorSpinnerDolchArray;
            spinnerSelection = studentTestType;
        } else {
            // If the selected test is one of the Fry types
            dolchRadioButton.setChecked(false);
            fryRadioButton.setChecked(true);
            testSelectorSpinnerArray = testSelectorSpinnerFryArray;
            spinnerSelection = studentTestType - 6;
        }

        final Spinner testSelectorSpinner = view.findViewById(R.id.change_test_dialog_fragment_spinner);

        final int testSelectorSpinnerItemLayout;
        if (isDefaultTest) {
            testSelectorSpinnerItemLayout = R.layout.test_selector_item_spinner_color_primary_dark;
        } else {
            testSelectorSpinnerItemLayout = R.layout.test_selector_item_spinner_color_secondary_dark;
        }
        ArrayAdapter<Spanned> spinnerAdapter = new ArrayAdapter<>
                (context, testSelectorSpinnerItemLayout, testSelectorSpinnerArray);

        testSelectorSpinner.setAdapter(spinnerAdapter);
        testSelectorSpinner.setSelection(spinnerSelection);

        // Listens to checked radio buttons on the radio group
        final RadioGroup testSelectorRadioGroup = view.findViewById(R.id.change_test_dialog_fragment_radio_group);
        testSelectorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                ArrayAdapter<Spanned> spinnerAdapter;
                if (checkedId == R.id.change_test_dialog_fragment_dolch_radio_button) {
                    spinnerAdapter = new ArrayAdapter<>
                            (context, testSelectorSpinnerItemLayout, testSelectorSpinnerDolchArray);

                } else {
                    spinnerAdapter = new ArrayAdapter<>
                            (context, testSelectorSpinnerItemLayout, testSelectorSpinnerFryArray);
                }
                testSelectorSpinner.setAdapter(spinnerAdapter);
                testSelectorSpinner.setSelection(0);
            }
        });

        // Listens to item selections on the spinner
        testSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (testSelectorRadioGroup.getCheckedRadioButtonId() == R.id.change_test_dialog_fragment_dolch_radio_button) {
                    mTestTypeSelected = position;
                } else {
                    mTestTypeSelected = position + 6;
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

}