package com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.widget.CompoundButtonCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.SharedPreferencesUtils;

import java.util.Objects;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.LOAD_STUDENTS_NOTIFICATION_DIALOG_FRAGMENT;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.MAX_NUMBER_OF_STUDENTS_IN_A_CLASS;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.READ_CSV_FILE_REQUEST_CODE;


public class LoadStudentsNotificationDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";


    public interface LoadStudentsDialogFragmentListener {
        void onLoadStudents(Uri uri);
    }


    public LoadStudentsNotificationDialogFragment() {
        // Empty constructor is required for DialogFragment
    }


    public static LoadStudentsNotificationDialogFragment newInstance(String title) {
        LoadStudentsNotificationDialogFragment loadStudentsNotificationDialogFragment =
                new LoadStudentsNotificationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        loadStudentsNotificationDialogFragment.setArguments(bundle);
        return loadStudentsNotificationDialogFragment;
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
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_load_students_notification, null);
        builder.setView(view);

        TextView textView1 = view.findViewById(R.id.dialog_fragment_load_students_notification_text_view_1);
        textView1.setText(Html.fromHtml(getString(R.string.dialog_fragment_load_students_notification_text_1)));
        TextView textView2 = view.findViewById(R.id.dialog_fragment_load_students_notification_text_view_2);
        textView2.setText(Html.fromHtml(getString(R.string.dialog_fragment_load_student_notification_text_2)));
        TextView textView3 = view.findViewById(R.id.dialog_fragment_load_students_notification_text_view_3);
        textView3.setText(getString(R.string.dialog_fragment_load_student_notification_text_3, MAX_NUMBER_OF_STUDENTS_IN_A_CLASS));

        view.findViewById(R.id.dialog_fragment_load_students_notification_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentUtils.showInfoMessageDialogFragment(getString(R.string.dialog_fragment_load_students_notification_info_text),
                        getString(R.string.dialog_fragment_load_students_notification_info_tag),
                        LOAD_STUDENTS_NOTIFICATION_DIALOG_FRAGMENT, true, getParentFragmentManager());
            }
        });

        CheckBox checkBox = view.findViewById(R.id.dialog_fragment_load_students_notification_checkbox);
        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorPrimaryLight)};
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setShowLoadStudentsInfoValueOnSharedPreferences(
                        Objects.requireNonNull(getContext()), !isChecked);
            }
        });

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
        alertDialog.show();

        // Sets the color of the positive and negative buttons.
        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(colorPrimaryDark);
        button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorPrimaryDark);

        // Override the positive button OnClickListener to modify its default behavior of closing
        // the AlertDialog after click.
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                // browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Filter to show only text files, using the image MIME data type.
                intent.setType("text/*");
                startActivityForResult(intent, READ_CSV_FILE_REQUEST_CODE);
            }
        });

        return alertDialog;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri;
        if (requestCode == READ_CSV_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK & data != null) {
            // A URI to the document will be contained in the return intent
            uri = data.getData();
        } else {
            uri = null;
        }
        LoadStudentsDialogFragmentListener listener = (LoadStudentsDialogFragmentListener) getActivity();
        if (listener != null) {
            listener.onLoadStudents(uri);
        }
        dismiss();
    }
}