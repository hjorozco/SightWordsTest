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
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.executors.AppExecutors;
import com.weebly.hectorjorozco.sightwordstest.utils.Utils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EditStudentNameDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";
    private static final String DIALOG_FRAGMENT_FIRST_NAME_ARGUMENT_KEY = "first_name";
    private static final String DIALOG_FRAGMENT_LAST_NAME_ARGUMENT_KEY = "last_name";
    private static final String DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY = "is_default_test";


    public interface EditStudentNameDialogFragmentListener {
        void onEditStudentName(String firstName, String lastName);
    }


    public EditStudentNameDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static EditStudentNameDialogFragment newInstance(String title, String firstName, String lastName,boolean isDefaultTest) {
        EditStudentNameDialogFragment editStudentNameDialogFragment =
                new EditStudentNameDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        bundle.putString(DIALOG_FRAGMENT_FIRST_NAME_ARGUMENT_KEY, firstName);
        bundle.putString(DIALOG_FRAGMENT_LAST_NAME_ARGUMENT_KEY, lastName);
        bundle.putBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY, isDefaultTest);
        editStudentNameDialogFragment.setArguments(bundle);
        return editStudentNameDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);
        final String originalFirstName = bundle.getString(DIALOG_FRAGMENT_FIRST_NAME_ARGUMENT_KEY);
        final String originalLastName = bundle.getString(DIALOG_FRAGMENT_LAST_NAME_ARGUMENT_KEY);
        boolean isDefaultTest = bundle.getBoolean(DIALOG_FRAGMENT_IS_DEFAULT_TEST_ARGUMENT_KEY);

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

        AlertDialog.Builder builder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color, colorDarkString, title)));

        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_edit_student_name, null);
        builder.setView(view);

        final MaterialEditText firstNameEditText = view.findViewById(R.id.dialog_fragment_edit_student_name_first_name_edit_text);
        final MaterialEditText lastNameEditText = view.findViewById(R.id.dialog_fragment_edit_student_name_last_name_edit_text);

        firstNameEditText.setTextColor(colorDark);
        firstNameEditText.setPrimaryColor(colorDark);
        firstNameEditText.setUnderlineColor(colorLight);

        lastNameEditText.setTextColor(colorDark);
        lastNameEditText.setPrimaryColor(colorDark);
        lastNameEditText.setUnderlineColor(colorLight);

        firstNameEditText.setText(originalFirstName);
        lastNameEditText.setText(originalLastName);

        builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This method will be overridden
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
        button.setTextColor(colorDark);
        button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorDark);


        // Override the positive button OnClickListener to modify its default behavior of closing
        // the AlertDialog after click.
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean closeDialog;
                // If one or both of the EditTexts are empty do not close the DialogFragment
                if (Utils.atLeastOneEditTextEmpty(firstNameEditText, lastNameEditText, getContext())) {
                    closeDialog = false;
                } else {

                    String firstName = Objects.requireNonNull(firstNameEditText.getText()).toString().trim();
                    firstName = WordUtils.capitalizeFully(firstName);
                    String lastName = Objects.requireNonNull(lastNameEditText.getText()).toString().trim();
                    lastName = WordUtils.capitalizeFully(lastName);

                    // If the user kept the same student name send it to the "StudentTestResultFragment"
                    // and close the DialogFragment
                    if (originalFirstName != null && originalLastName != null
                            && originalFirstName.equals(firstName) && originalLastName.equals(lastName)) {
                        sendStudentNameToTargetFragment(firstName, lastName);
                        closeDialog = true;

                    } else {
                        // If the name was changed first check if there is not other student with the changed name
                        // already in the "students" table
                        final String finalFirstName = firstName;
                        final String finalLastName = lastName;
                        Future<Boolean> studentNameAlreadyOnTableResult =
                                AppExecutors.getInstance().diskIO().submit(new Callable<Boolean>() {
                                    @Override
                                    public Boolean call() {
                                        return AppDatabase.getInstance(getContext())
                                                .studentDao().findStudentsWithName(finalFirstName, finalLastName).size() == 1;
                                    }
                                });

                        boolean studentNameAlreadyOnTable = false;
                        try {
                            studentNameAlreadyOnTable = studentNameAlreadyOnTableResult.get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        // If the changed student name is already on the "students" table show a message and
                        // do not close the DialogFragment
                        if (studentNameAlreadyOnTable) {
                            Toast.makeText(getContext(), getString(
                                    R.string.menu_student_test_results_action_edit_student_already_on_table_message),
                                    Toast.LENGTH_SHORT).show();
                            closeDialog = false;
                        } else {

                            // Send the changed name to the "StudentTestResultFragment" to update the
                            // students record in the DB
                            sendStudentNameToTargetFragment(firstName, lastName);
                            closeDialog = true;
                        }
                    }
                }

                if (closeDialog)
                    dismiss();
            }
        });

        return alertDialog;
    }


    private void sendStudentNameToTargetFragment(String firstName, String lastName) {
        EditStudentNameDialogFragmentListener listener = (EditStudentNameDialogFragmentListener) getTargetFragment();
        Objects.requireNonNull(listener).onEditStudentName(firstName, lastName);
    }

}