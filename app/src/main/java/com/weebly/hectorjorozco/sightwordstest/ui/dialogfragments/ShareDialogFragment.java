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
import android.widget.ImageView;
import android.widget.RadioButton;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;

import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.CSV_DETAILED_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.CSV_SIMPLE_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.PDF_DETAILED_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.PDF_SIMPLE_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_DIALOG_FRAGMENT;

public class ShareDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_IS_CLASS_SHARED_ARGUMENT_KEY = "is_class_shared";
    private static final String DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY = "is_default_test";
    private int mShareDocumentTypeSelected;


    public interface ShareDialogFragmentListener {
        void onShare(int shareDocumentTypeSelected);
    }


    public ShareDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static ShareDialogFragment newInstance(String title, boolean isClassShared, boolean isDefaultTest) {
        ShareDialogFragment shareDialogFragment =
                new ShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_CLASS_SHARED_ARGUMENT_KEY, isClassShared);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY, isDefaultTest);
        shareDialogFragment.setArguments(bundle);
        return shareDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
        final boolean isClassShared = bundle.getBoolean(DIALOG_FRAGMENT_IS_CLASS_SHARED_ARGUMENT_KEY);
        final boolean isDefaultTest = bundle.getBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY);

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
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_share, null);
        builder.setView(view);

        final RadioButton pdfRadioButton = view.findViewById(R.id.dialog_fragment_share_pdf_radio_button);
        RadioButton csvRadioButton = view.findViewById(R.id.dialog_fragment_share_csv_radio_button);
        final RadioButton simpleRadioButton = view.findViewById(R.id.dialog_fragment_share_simple_radio_button);
        RadioButton detailedRadioButton = view.findViewById(R.id.dialog_fragment_share_detailed_radio_button);

        pdfRadioButton.setChecked(true);
        simpleRadioButton.setChecked(true);

        pdfRadioButton.setTextColor(colorDark);
        csvRadioButton.setTextColor(colorDark);
        simpleRadioButton.setTextColor(colorDark);
        detailedRadioButton.setTextColor(colorDark);

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {colorDark, color};
        CompoundButtonCompat.setButtonTintList(pdfRadioButton, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(csvRadioButton, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(simpleRadioButton, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(detailedRadioButton, new ColorStateList(states, colors));

        ImageView infoImageView1 = view.findViewById(R.id.dialog_fragment_share_info_image_view_1);
        ImageView infoImageView2 = view.findViewById(R.id.dialog_fragment_share_info_image_view_2);

        if (isDefaultTest) {
            infoImageView1.setImageResource(R.drawable.ic_info_outline_color_primary_24dp);
            infoImageView2.setImageResource(R.drawable.ic_info_outline_color_primary_24dp);
        } else {
            infoImageView1.setImageResource(R.drawable.ic_info_outline_color_secondary_24dp);
            infoImageView2.setImageResource(R.drawable.ic_info_outline_color_secondary_24dp);
        }

        infoImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentUtils.showInfoMessageDialogFragment(getString(R.string.share_dialog_fragment_info_text_1),
                        getString(R.string.share_dialog_fragment_info_tag_1), SHARE_DIALOG_FRAGMENT, isDefaultTest, getFragmentManager());
            }
        });

        infoImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentUtils.showInfoMessageDialogFragment(getString(R.string.share_dialog_fragment_info_text_2),
                        getString(R.string.share_dialog_fragment_info_tag_2), SHARE_DIALOG_FRAGMENT, isDefaultTest, getFragmentManager());
            }
        });

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pdfRadioButton.isChecked()) {
                    if (simpleRadioButton.isChecked()) {
                        mShareDocumentTypeSelected = PDF_SIMPLE_SHARE_DOCUMENT_TYPE;
                    } else {
                        mShareDocumentTypeSelected = PDF_DETAILED_SHARE_DOCUMENT_TYPE;
                    }
                } else {
                    if (simpleRadioButton.isChecked()) {
                        mShareDocumentTypeSelected = CSV_SIMPLE_SHARE_DOCUMENT_TYPE;
                    } else {
                        mShareDocumentTypeSelected = CSV_DETAILED_SHARE_DOCUMENT_TYPE;
                    }
                }

                ShareDialogFragmentListener listener;
                if (isClassShared) {
                    // If the whole class are being shared, set the listener to MainActivity
                    // (the activity that created this Dialog Fragment)
                    listener = (ShareDialogFragmentListener) getActivity();
                } else {
                    // If only the student class results are being shared, set the listener to
                    // StudentTestResultsFragment (the target Fragment that created this Dialog Fragment)
                    listener = (ShareDialogFragmentListener) getTargetFragment();
                }

                Objects.requireNonNull(listener).onShare(mShareDocumentTypeSelected);
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
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(colorDark);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(colorDark);

        return alertDialog;
    }


}