package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.ui.WordListsInformationActivity;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;

import java.util.List;
import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.DOWNLOAD_RESOURCES_DIALOG_FRAGMENT;

public class DownloadResourcesDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    public static final int SHARE_RESOURCES_LINK_RESOURCE_TYPE_VALUE = -3;
    public static final int DOWNLOAD_ALL_WORDS_LISTS_TYPE_VALUE = -2;
    public static final int DOWNLOAD_ALL_FLASH_CARDS_TYPE_VALUE = -1;
    private static final int NUMBER_OF_DOLCH_LISTS = 6;
    private static final int NUMBER_OF_FRY_LISTS = 10;

    private ScrollView mScrollView;
    private RadioGroup mResourceTypeRadioGroup;
    private RadioGroup mTestTypeRadioGroup;
    private Spinner mTestSelectorSpinner;

    // Interface that will be implemented on MainActivity to listen for user selected download
    public interface DownloadResourcesDialogFragmentListener {
        void onDownloadResources(int resourceTypeSelected);
    }

    public DownloadResourcesDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static DownloadResourcesDialogFragment newInstance(String title) {
        DownloadResourcesDialogFragment downloadResourcesDialogFragment =
                new DownloadResourcesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        downloadResourcesDialogFragment.setArguments(bundle);
        return downloadResourcesDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int colorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);

        String colorPrimaryDarkString = Integer.toHexString(colorPrimaryDark & 0x00ffffff);

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color, colorPrimaryDarkString, title)));

        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_download_resources, null);
        builder.setView(view);

        mScrollView = view.findViewById(R.id.dialog_fragment_download_resources_scroll_view);
        mResourceTypeRadioGroup = view.findViewById(R.id.dialog_fragment_download_resources_resource_type_radio_group);
        mTestTypeRadioGroup = view.findViewById(R.id.dialog_fragment_download_resources_test_type_radio_group);
        mTestSelectorSpinner = view.findViewById(R.id.dialog_fragment_download_resources_spinner);

        setupViewControls();

        view.findViewById(R.id.dialog_fragment_download_resources_info_image_view_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentUtils.showInfoMessageDialogFragment(getString(R.string.download_resources_dialog_fragment_info_text),
                        getString(R.string.download_resources_dialog_fragment_info_tag), DOWNLOAD_RESOURCES_DIALOG_FRAGMENT, true, getFragmentManager());
            }
        });

        view.findViewById(R.id.dialog_fragment_download_resources_info_image_view_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WordListsInformationActivity.class);
                startActivity(intent);
            }
        });

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadResourcesDialogFragmentListener listener =
                        (DownloadResourcesDialogFragmentListener) getActivity();
                Objects.requireNonNull(listener).onDownloadResources(getResourceTypeSelected());
            }
        });

        builder.setNeutralButton(getString(R.string.menu_main_download_on_another_device_alert_dialog_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadResourcesDialogFragmentListener listener =
                        (DownloadResourcesDialogFragmentListener) getActivity();
                Objects.requireNonNull(listener).onDownloadResources(SHARE_RESOURCES_LINK_RESOURCE_TYPE_VALUE);
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
        button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        button.setTextColor(colorPrimaryDark);

        return alertDialog;
    }


    private void setupViewControls() {

        final Context context = getContext();

        // Set up spinner
        final List<Spanned> testSelectorSpinnerDolchArray =
                TestTypeUtils.getTestSelectorSpinnerArray(Objects.requireNonNull(context), true, true);
        final List<Spanned> testSelectorSpinnerFryArray =
                TestTypeUtils.getTestSelectorSpinnerArray(context, false, true);

        ArrayAdapter<Spanned> spinnerAdapter = new ArrayAdapter<>
                (context, R.layout.test_selector_item_spinner_color_primary_dark, testSelectorSpinnerDolchArray);

        mTestSelectorSpinner.setAdapter(spinnerAdapter);
        mTestSelectorSpinner.setSelection(0);

        // Set up other controls
        mResourceTypeRadioGroup.check(R.id.dialog_fragment_download_resources_word_list_radio_button);
        mTestTypeRadioGroup.check(R.id.dialog_fragment_download_resources_all_radio_button);
        mTestSelectorSpinner.setVisibility(View.GONE);

        mTestTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // If the "All" radio button is selected hide the spinner. If not set up the spinner.
                if (checkedId == R.id.dialog_fragment_download_resources_all_radio_button) {
                    mTestSelectorSpinner.setVisibility(View.GONE);
                } else if (checkedId == R.id.dialog_fragment_download_resources_dolch_radio_button) {
                    mTestSelectorSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<Spanned> dolchSpinnerAdapter = new ArrayAdapter<>
                            (context, R.layout.test_selector_item_spinner_color_primary_dark, testSelectorSpinnerDolchArray);
                    mTestSelectorSpinner.setAdapter(dolchSpinnerAdapter);
                    mTestSelectorSpinner.setSelection(0);
                    scrollToBottomOfScrollView();
                } else {
                    mTestSelectorSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<Spanned> frySpinnerAdapter = new ArrayAdapter<>
                            (context, R.layout.test_selector_item_spinner_color_primary_dark, testSelectorSpinnerFryArray);
                    mTestSelectorSpinner.setAdapter(frySpinnerAdapter);
                    mTestSelectorSpinner.setSelection(0);
                    scrollToBottomOfScrollView();
                }

            }
        });

    }

    // Helper method that checks the user control selections and determines the value of the resource
    // type to be downloaded
    private int getResourceTypeSelected() {

        int spinnerSelectedItemPosition;

        boolean wordsListChecked = mResourceTypeRadioGroup.getCheckedRadioButtonId() ==
                R.id.dialog_fragment_download_resources_word_list_radio_button;
        boolean allChecked = mTestTypeRadioGroup.getCheckedRadioButtonId() ==
                R.id.dialog_fragment_download_resources_all_radio_button;
        boolean dolchChecked = mTestTypeRadioGroup.getCheckedRadioButtonId() ==
                R.id.dialog_fragment_download_resources_dolch_radio_button;

        spinnerSelectedItemPosition = mTestSelectorSpinner.getSelectedItemPosition();

        if (wordsListChecked) {
            if (allChecked) {
                return DOWNLOAD_ALL_WORDS_LISTS_TYPE_VALUE;
            }
            else if (dolchChecked) {
                return spinnerSelectedItemPosition;
            } else {
                return spinnerSelectedItemPosition + NUMBER_OF_DOLCH_LISTS;
            }
        } else {
            if (allChecked) {
                return DOWNLOAD_ALL_FLASH_CARDS_TYPE_VALUE;
            }
            else if (dolchChecked) {
                return spinnerSelectedItemPosition + NUMBER_OF_DOLCH_LISTS + NUMBER_OF_FRY_LISTS;
            } else {
                return spinnerSelectedItemPosition + NUMBER_OF_DOLCH_LISTS * 2 + NUMBER_OF_FRY_LISTS;
            }
        }

    }

    private void scrollToBottomOfScrollView(){
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}