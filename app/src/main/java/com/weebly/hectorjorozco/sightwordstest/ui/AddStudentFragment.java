package com.weebly.hectorjorozco.sightwordstest.ui;

import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.CompoundButtonCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.executors.AppExecutors;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ChangeTestDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.Utils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;
import com.weebly.hectorjorozco.sightwordstest.widget.WidgetProvider;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.ADD_STUDENT_FRAGMENT;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.MAX_NUMBER_OF_STUDENTS_IN_A_CLASS;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.NUMBER_OF_STUDENTS_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SQLITE_CONSTRAINT_UNIQUE_CODE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_WITH_NO_TESTS_GRADE;

/**
 * Fragment that will let the user to add a new student to the database
 */
public class AddStudentFragment extends Fragment implements
        ChangeTestDialogFragment.ChangeTestDialogFragmentListener {

    // Member variable for the Database
    private AppDatabase mAppDatabase;
    private FragmentActivity mFragmentActivity;
    private Toolbar mToolbar;
    private MaterialEditText mFirstNameEditText;
    private MaterialEditText mLastNameEditText;
    private TextView mTestTypeTextView;
    private CheckBox mAddStudentCheckbox;
    private boolean mTablet;
    private boolean mIsDefaultTest;
    private int mStudentTestType;
    private int mNumberOfStudents;

    private static final String SIMPLE_NAME = AddStudentFragment.class.getSimpleName();

    private static final String STUDENT_TEST_TYPE_SAVED_INSTANCE_STATE_KEY = "student_test_type";
    private static final String NUMBER_OF_STUDENTS_SAVED_INSTANCE_STATE_KEY = "number_of_students_bundle_info_key";


    public AddStudentFragment() {
    }

    // Interface implemented in AddStudentActivity.java to receive the name of the added student
    public interface StudentAddedListener {
        void onStudentAdded(String firstName, String lastName, int numberOfStudents);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTablet = getResources().getBoolean(R.bool.tablet);
        // Gets the activity that the fragment is attached to.
        mFragmentActivity = getActivity();
        if (mFragmentActivity != null) {
            // Gets an instance of the Database
            mAppDatabase = AppDatabase.getInstance(mFragmentActivity.getApplicationContext());

            if (savedInstanceState == null) {
                mStudentTestType = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity);
            } else {
                mStudentTestType = savedInstanceState.getInt(STUDENT_TEST_TYPE_SAVED_INSTANCE_STATE_KEY);
            }
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_student, container, false);

        mFirstNameEditText = rootView.findViewById(R.id.fragment_add_student_first_name_edit_text);
        mLastNameEditText = rootView.findViewById(R.id.fragment_add_student_last_name_edit_text);
        mTestTypeTextView = rootView.findViewById(R.id.fragment_add_student_test_type_text_view);
        mAddStudentCheckbox = rootView.findViewById(R.id.fragment_add_student_checkbox);

        // Gets the toolbar from the fragment's layout
        mToolbar = rootView.findViewById(R.id.fragment_add_student_tool_bar);

        // Get the number of students passed to the fragment.
        if (mTablet) {
            if (savedInstanceState == null) {
                Bundle arguments = getArguments();
                if (arguments != null) {
                    if (arguments.containsKey(NUMBER_OF_STUDENTS_KEY)) {
                        mNumberOfStudents = arguments.getInt(NUMBER_OF_STUDENTS_KEY);
                    }
                }
            } else {
                mNumberOfStudents = savedInstanceState.getInt(NUMBER_OF_STUDENTS_SAVED_INSTANCE_STATE_KEY);
            }
        } else {
            if (savedInstanceState == null) {
                AddStudentActivity activity = (AddStudentActivity) getActivity();
                if (activity != null) {
                    mNumberOfStudents = activity.numberOfStudents;
                }
            } else {
                mNumberOfStudents = savedInstanceState.getInt(NUMBER_OF_STUDENTS_SAVED_INSTANCE_STATE_KEY);
            }
        }

        if (!mTablet) {
            // If a phone, set the back navigation arrow.
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFragmentActivity != null) {
                        mFragmentActivity.onBackPressed();
                    }
                }
            });

        }

        mIsDefaultTest = mStudentTestType == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity);

        setupToolbarMenu();

        setupContent();

        setUpFab(rootView);

        return rootView;
    }


    private void setupToolbarMenu() {
        mToolbar.inflateMenu(R.menu.menu_add_student);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.menu_add_student_action_change) {

                    showChangeTestDialogFragment();

                }
                return true;
            }
        });
    }


    // Helper method that sets up the Edit Texts where the user will enter the student's name
    private void setupContent() {

        int colorDark;
        int colorLight;
        if (mIsDefaultTest) {
            colorDark = mFragmentActivity.getResources().getColor(R.color.colorPrimaryDark);
            colorLight = mFragmentActivity.getResources().getColor(R.color.colorPrimaryLight);
        } else {
            colorDark = mFragmentActivity.getResources().getColor(R.color.colorSecondaryDark);
            colorLight = mFragmentActivity.getResources().getColor(R.color.colorSecondaryLight);
        }

        String defaultTestTypeString = TestTypeUtils.getTestTypeString(mFragmentActivity, mStudentTestType);
        String numberOfWordsOnTestColorHexValue = Integer.toHexString(colorLight & 0x00ffffff);
        int numberOfWords = WordUtils.getNumberOfWordsOnList(mStudentTestType);

        mTestTypeTextView.setTextColor(colorDark);
        mTestTypeTextView.setText(Html.fromHtml(getString(R.string.fragment_add_student_test_type_message,
                defaultTestTypeString, numberOfWordsOnTestColorHexValue, numberOfWords)));

        mFirstNameEditText.setTextColor(colorDark);
        mFirstNameEditText.setPrimaryColor(colorDark);
        mFirstNameEditText.setUnderlineColor(colorLight);

        mLastNameEditText.setTextColor(colorDark);
        mLastNameEditText.setPrimaryColor(colorDark);
        mLastNameEditText.setUnderlineColor(colorLight);

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {colorDark, colorLight};
        CompoundButtonCompat.setButtonTintList(mAddStudentCheckbox, new ColorStateList(states, colors));

        mAddStudentCheckbox.setTextColor(colorLight);

    }


    // Set up Floating Action Button to add a new Student to the "students" DB table when clicked
    private void setUpFab(View rootView) {

        FloatingActionButton fab = rootView.findViewById(R.id.fragment_add_student_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final boolean isDefaultTest =
                        mStudentTestType == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity);

                // Validates, retrieves and formats user input.
                if (Utils.atLeastOneEditTextEmpty(mFirstNameEditText, mLastNameEditText, mFragmentActivity))
                    return;

                String firstName = Objects.requireNonNull(mFirstNameEditText.getText()).toString().trim();
                firstName = WordUtils.capitalizeFully(firstName);
                String lastName = Objects.requireNonNull(mLastNameEditText.getText()).toString().trim();
                lastName = WordUtils.capitalizeFully(lastName);

                // Inserts the new student data into the "students" database table.
                final StudentEntry studentEntry = new StudentEntry(firstName, lastName,
                        STUDENT_WITH_NO_TESTS_GRADE, mStudentTestType, new Date(), EMPTY_STRING);

                Future<Boolean[]> addStudentResult = AppExecutors.getInstance().diskIO().submit(new Callable<Boolean[]>() {
                    @Override
                    public Boolean[] call() {
                        boolean studentInserted = false;
                        if (mAppDatabase != null) {
                            // insert new student in DB.
                            try {
                                mAppDatabase.studentDao().insertStudent(studentEntry);
                                studentInserted = true;
                            } catch (SQLiteConstraintException e) {
                                // If there was an error inserting the student show an error message
                                if (e.toString().contains(SQLITE_CONSTRAINT_UNIQUE_CODE)) {
                                    mFragmentActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MessageDialogFragment messageDialogFragment =
                                                    MessageDialogFragment.newInstance(
                                                            getString(R.string.duplicated_student_dialog_fragment_text),
                                                            getString(R.string.duplicated_student_dialog_fragment_title),
                                                            isDefaultTest, ADD_STUDENT_FRAGMENT, EMPTY_STRING);

                                            messageDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                                                    getString(R.string.duplicated_student_dialog_fragment_tag));
                                        }
                                    });

                                }
                                Log.e(SIMPLE_NAME, Objects.requireNonNull(e.getMessage()));
                            }
                        }

                        Boolean[] result = new Boolean[2];
                        result[0] = studentInserted;
                        result[1] = mAddStudentCheckbox.isChecked();

                        return result;
                    }
                });

                Boolean[] result = new Boolean[2];
                try {
                    result = addStudentResult.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                boolean studentInserted = result[0];
                boolean addMoreStudents = result[1];

                if (studentInserted) {
                    mNumberOfStudents++;
                    if (addMoreStudents && mNumberOfStudents < MAX_NUMBER_OF_STUDENTS_IN_A_CLASS) {
                        mStudentTestType = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity);
                        mIsDefaultTest = true;
                        setupContent();
                        mFirstNameEditText.getText().clear();
                        mLastNameEditText.getText().clear();
                        mFirstNameEditText.requestFocus();
                        WidgetProvider.updateWidgetWithStudentsInformation(mFragmentActivity);
                        Toast.makeText(mFragmentActivity, getString(R.string.new_student_added_toast_text_1,
                                studentEntry.getFirstName(), studentEntry.getLastName()), Toast.LENGTH_SHORT).show();
                    } else {
                        if (mTablet) {
                            Toast.makeText(mFragmentActivity, getString(R.string.new_student_added_toast_text_1,
                                    studentEntry.getFirstName(), studentEntry.getLastName()), Toast.LENGTH_SHORT).show();
                            showWordListsInformationFragment();
                        } else {
                            if (mFragmentActivity != null) {
                                StudentAddedListener listener = (StudentAddedListener) getActivity();
                                Objects.requireNonNull(listener).onStudentAdded
                                        (studentEntry.getFirstName(), studentEntry.getLastName(), mNumberOfStudents);
                                mFragmentActivity.finish();
                            }

                        }
                    }
                } else {
                    mFirstNameEditText.requestFocus();
                }
            }
        });

    }


    // Helper method that displays WordListsInformationFragment on the Detail Container of Main Activity.
    private void showWordListsInformationFragment() {
        if (mFragmentActivity != null) {
            FragmentManager fragmentManager = mFragmentActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_detail_layout, new WordListsInformationFragment())
                    .commit();
        }
    }


    // Helper method that show a DialogFragment that lets the user to select a new test type for the
    // student
    private void showChangeTestDialogFragment() {
        FragmentManager fragmentManager = mFragmentActivity.getSupportFragmentManager();
        ChangeTestDialogFragment changeTestDialogFragment =
                ChangeTestDialogFragment.newInstance(
                        getString(R.string.change_student_test_dialog_fragment_title), mStudentTestType, mIsDefaultTest, ADD_STUDENT_FRAGMENT);

        // Sets AddStudentFragment as the target fragment to get results from changeTestDialogFragment
        changeTestDialogFragment.setTargetFragment(AddStudentFragment.this, 300);

        changeTestDialogFragment.show(fragmentManager, getString(R.string.change_student_test_dialog_fragment_tag));
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NUMBER_OF_STUDENTS_SAVED_INSTANCE_STATE_KEY, mNumberOfStudents);
        outState.putInt(STUDENT_TEST_TYPE_SAVED_INSTANCE_STATE_KEY, mStudentTestType);
    }

    // Implementation of the interface ChangeTestDialogFragment.ChangeTestDialogFragmentListener
    // It receives a test type selected by the user on ChangeTestDialogFragment
    @Override
    public void onChangeTest(int testType) {

        // If the changed student test is the same it was before show a message and don't update the UI
        if (mStudentTestType == testType) {
            Toast.makeText(mFragmentActivity, getString(
                    R.string.change_test_dialog_fragment_student_test_not_changed_message),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Update the UI and member variables and show a message.
            mStudentTestType = testType;
            mIsDefaultTest = mStudentTestType == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity);
            setupContent();
            Toast.makeText(mFragmentActivity,
                    getString(R.string.change_test_dialog_fragment_student_test_changed_message),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
