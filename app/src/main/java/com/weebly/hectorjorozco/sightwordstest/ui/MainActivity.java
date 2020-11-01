package com.weebly.hectorjorozco.sightwordstest.ui;

import android.app.Activity;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.MenuCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.adapters.StudentsListAdapter;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.executors.AppExecutors;
import com.weebly.hectorjorozco.sightwordstest.models.ShareResult;
import com.weebly.hectorjorozco.sightwordstest.models.SparseBooleanArrayParcelable;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ChangeTestDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ConfirmationDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.DownloadResourcesDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.LoadStudentsNotificationDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.LoadStudentsResultDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ShareDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.SortStudentsDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.CustomLinkMovement;
import com.weebly.hectorjorozco.sightwordstest.utils.NumberOfStudentsUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.ShareUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.SharedPreferencesUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.StudentsOrderUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.Utils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;
import com.weebly.hectorjorozco.sightwordstest.viewmodels.MainViewModel;
import com.weebly.hectorjorozco.sightwordstest.widget.WidgetProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.weebly.hectorjorozco.sightwordstest.utils.ShareUtils.deleteFilesFromExternalStoragePrivateDocumentsDirectory;

public class MainActivity extends AppCompatActivity implements StudentsListAdapter.StudentsListAdapterListener,
        SortStudentsDialogFragment.SortStudentsDialogFragmentListener,
        ShareDialogFragment.ShareDialogFragmentListener,
        ChangeTestDialogFragment.ChangeTestDialogFragmentListener,
        ConfirmationDialogFragment.ConfirmationDialogFragmentListener,
        DownloadResourcesDialogFragment.DownloadResourcesDialogFragmentListener,
        LoadStudentsNotificationDialogFragment.LoadStudentsDialogFragmentListener,
        MessageDialogFragment.MaxNumberOfStudentsReachedListener,
        CustomLinkMovement.CustomLinkMovementListener {

    private static final String SIMPLE_NAME = MainActivity.class.getSimpleName();
    private StudentsListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean mTablet;
    private TextView mTitleTextView;
    private TextView mSortByInfoTextView;
    private TextView mEmptyClassroomTextView1;
    private TextView mEmptyClassroomTextView2;
    private LinearLayout mSnackView;
    private Menu mMenu;
    private DividerItemDecoration mDividerItemDecoration;

    // Used for action mode
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    // Used to save the swiped to delete student on rotation
    private boolean mIsAskingToDeleteStudent;
    private int mStudentWithDeleteBackgroundPosition;
    private boolean mRightSwipe;

    private final List<File> mFilesToDelete = new ArrayList<>();

    private AppDatabase mAppDatabase;

    private static final String DATE_FORMAT = "MM/dd/yyy hh:mm aaa";
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public static final int SORT_BY_FIRST_NAME_VALUE = 0;
    public static final int SORT_BY_LAST_NAME_VALUE = 1;
    public static final int SORT_BY_TEST_RESULT_VALUE = 2;
    public static final int SORT_BY_TEST_TYPE_VALUE = 3;

    private static final int ADD_STUDENT_ACTIVITY_REQUEST_CODE = 0;

    // Key of the information that will be passed to the student test results ui
    public static final String STUDENT_ENTRY_KEY = "student_info_to_pass_key";

    public static final String NUMBER_OF_STUDENTS_KEY = "number_of_students_key";

    public static final int PDF_SIMPLE_SHARE_DOCUMENT_TYPE = 1;
    public static final int PDF_DETAILED_SHARE_DOCUMENT_TYPE = 2;
    public static final int CSV_SIMPLE_SHARE_DOCUMENT_TYPE = 3;
    public static final int CSV_DETAILED_SHARE_DOCUMENT_TYPE = 4;

    // Values used to identify the caller of a DialogFragment
    private static final byte MAIN_ACTIVITY = 1;
    public static final byte MAIN_ACTIVITY_MAX_NUMBER_OF_STUDENTS_REACHED = 2;
    public static final byte MAIN_ACTIVITY_ABOUT_MESSAGE = 3;
    public static final byte TEST_ACTIVITY = 4;
    public static final byte ADD_STUDENT_FRAGMENT = 5;
    public static final byte STUDENT_TEST_RESULTS_FRAGMENT_TEST_INFO = 6;
    public static final byte STUDENT_TEST_RESULT_FRAGMENT_MAX_NUMBER_OF_TESTS_REACHED = 7;
    public static final byte STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_NO_TESTS = 8;
    public static final byte STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_TESTS = 9;
    public static final byte WORD_LISTS_INFORMATION_FRAGMENT = 10;
    public static final byte SHARE_DIALOG_FRAGMENT = 11;
    public static final byte SORT_STUDENTS_DIALOG_FRAGMENT = 12;
    public static final byte DOWNLOAD_RESOURCES_DIALOG_FRAGMENT = 13;
    public static final byte LOAD_STUDENTS_NOTIFICATION_DIALOG_FRAGMENT = 14;
    public static final byte LOAD_STUDENTS_RESULT_DIALOG_FRAGMENT = 15;
    public static final byte DELETE_OLDEST_STUDENT_RESULTS_DIALOG_FRAGMENT = 16;

    public static final int READ_CSV_FILE_REQUEST_CODE = 42;

    public static final int MAX_NUMBER_OF_STUDENTS_IN_A_CLASS = 100;

    // Add student constants
    public static final String EMPTY_STRING = "";
    public static final int STUDENT_WITH_NO_TESTS_GRADE = -1;

    // SQLiteConstraintException code thrown when the user tries to add a duplicate student
    public static final String SQLITE_CONSTRAINT_UNIQUE_CODE = "code 2067";

    private static final byte STUDENT_LOADED_SUCCESSFULLY = 0;
    private static final byte STUDENT_NOT_LOADED_SUCCESSFULLY = 1;
    private static final byte STUDENT_DUPLICATED = 2;
    private static final byte STUDENT_NAME_INCOMPLETE_NO_FIRST_NAME = 3;
    private static final byte STUDENT_NAME_INCOMPLETE_NO_LAST_NAME = 4;
    private static final byte STUDENT_NAME_INCOMPLETE_NO_FIRST_NAME_NO_LAST_NAME = 5;
    private static final byte MAX_CLASS_SIZE_EXCEEDED = 6;

    private static final byte MAX_CLASS_SIZE_EXCEED_FLAG = 20;

    public static final String SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY = "saved_instance_state_selected_items_key";
    public static final String SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY =
            "saved_instance_state_test_with_delete_background_position_key";
    public static final String SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY =
            "saved_instance_state_right_swiped_key";

    public static final int LONG_PRESS_VIBRATION_TIME_IN_MILLISECONDS = 10;

    // Share operation result codes
    public static final byte SHARE_RESULT_DATA_SHARED = 0;
    public static final byte SHARE_RESULT_NO_APP = 1;
    public static final byte SHARE_RESULT_NO_FILE_CREATED = 2;

    // Values used to set up the divider line of the Recycler View
    public static final boolean LIGHT_LINE = true;
    public static final boolean DARK_LINE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Sets style back to normal after splash image
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Stetho. Once the application is integrated, navigate to chrome://inspect in Google Chrome
        Stetho.initializeWithDefaults(this);

        deleteFilesFromExternalStoragePrivateDocumentsDirectory(this);

        // Set up toolbar
        Toolbar mainActivityToolbar = findViewById(R.id.activity_main_tool_bar);
        mainActivityToolbar.setTitle(getString(R.string.activity_main_label));
        setSupportActionBar(mainActivityToolbar);

        // True if it is a tablet, false otherwise
        mTablet = getResources().getBoolean(R.bool.tablet);

        mTitleTextView = findViewById(R.id.activity_main_title_text_view);
        mSortByInfoTextView = findViewById(R.id.activity_main_sort_by_info_text_view);
        mEmptyClassroomTextView1 = findViewById(R.id.activity_main_empty_classroom_text_view_1);
        mEmptyClassroomTextView2 = findViewById(R.id.activity_main_empty_classroom_text_view_2);
        mSnackView = findViewById(R.id.activity_main_master_layout);

        mEmptyClassroomTextView1.setText(Html.fromHtml(getString(R.string.empty_classroom_message_1)));
        mEmptyClassroomTextView1.setMovementMethod(new CustomLinkMovement(this, getSupportFragmentManager()));

        mEmptyClassroomTextView2.setText(Html.fromHtml(getString(R.string.empty_classroom_message_2)));
        mEmptyClassroomTextView2.setMovementMethod(new CustomLinkMovement(this, getSupportFragmentManager()));

        // Create students list adapter
        mAdapter = new StudentsListAdapter(this, this);
        mAdapter.setDefaultTestType();

        // Gets an instance of the application database
        mAppDatabase = AppDatabase.getInstance(getApplicationContext());

        actionModeCallback = new ActionModeCallback();
        mIsAskingToDeleteStudent = false;
        mRightSwipe = false;

        setupRecyclerView();

        setupViewModel();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY)) {
                SparseBooleanArrayParcelable selectedStudents = savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY);
                if (selectedStudents != null && selectedStudents.size() > 0) {
                    actionMode = startSupportActionMode(actionModeCallback);
                    if (actionMode != null) {
                        actionMode.setTitle(getString(R.string.action_mode_toolbar_title, selectedStudents.size()));
                    }
                    mAdapter.setSelectedStudents(selectedStudents);
                    mDividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable
                            (getResources(), R.drawable.horizontal_line_dark, null)));
                }
            }
            if (savedInstanceState.containsKey(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY) &&
                    savedInstanceState.containsKey(SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY)) {
                int position = savedInstanceState.getInt(SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY);
                boolean rightSwiped = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY);
                mAdapter.setStudentWithDeleteBackgroundData(position, rightSwiped);
                mAdapter.notifyItemChanged(position);
                mIsAskingToDeleteStudent = true;
                mStudentWithDeleteBackgroundPosition = position;
                mRightSwipe = rightSwiped;
            }
        } else {
            // If this is the first time Main Activity is created and it is running on a tablet, show
            // a Word lists information fragment on the Detail container.
            if (mTablet) {
                replaceFragmentOnContainer(new WordListsInformationFragment());
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // If on a tablet, check if the activity was started by a widget list item and if so
        // show the clicked student test results
        if (mTablet) {
            StudentEntry studentEntry;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey(STUDENT_ENTRY_KEY)) {
                studentEntry = bundle.getParcelable(STUDENT_ENTRY_KEY);
                if (studentEntry != null) {
                    displayStudentTestResultFragmentOnDetailsContainer(studentEntry);
                }
            }
        }

        // Delete the oldest CSV temporary file created and leave the newest one.
        if (mFilesToDelete.size() == 2) {
            boolean deleted = mFilesToDelete.get(0).delete();
            if (deleted) {
                mFilesToDelete.remove(0);
            }
        }
    }


    private void setupRecyclerView() {
        mDividerItemDecoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mDividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable
                (getResources(), R.drawable.horizontal_line_light, null)));

        // Set up RecyclerView that shows a students list.
        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);

        // Add a touch helper to the RecyclerView to recognize when a user swipes to delete a student.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull
                    RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // When the row is being swiped set the default UI to be the foreground view.
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected(((StudentsListAdapter.StudentViewHolder) viewHolder).viewForeground);
                }
            }

            // Sets the foreground view to be moving when swiped
            @Override
            public void onChildDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDrawOver(canvas, recyclerView, ((StudentsListAdapter.StudentViewHolder) viewHolder).viewForeground,
                        dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDraw(c, recyclerView, ((StudentsListAdapter.StudentViewHolder) viewHolder).viewForeground,
                        dX, dY, actionState, isCurrentlyActive);

                // Right swipe
                if (dX > 0) {
                    mRightSwipe = true;
                    ((StudentsListAdapter.StudentViewHolder) viewHolder).viewRightSwipeBackground.setVisibility(View.VISIBLE);
                    ((StudentsListAdapter.StudentViewHolder) viewHolder).viewLeftSwipeBackground.setVisibility(View.INVISIBLE);
                } else {
                    // Left swipe
                    mRightSwipe = false;
                    ((StudentsListAdapter.StudentViewHolder) viewHolder).viewRightSwipeBackground.setVisibility(View.INVISIBLE);
                    ((StudentsListAdapter.StudentViewHolder) viewHolder).viewLeftSwipeBackground.setVisibility(View.VISIBLE);
                }

            }

            // If the student is not deleted sets the view again to foreground
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                getDefaultUIUtil().clearView(((StudentsListAdapter.StudentViewHolder) viewHolder).viewForeground);
            }

            // Disables swipe when Action Mode is enabled
            @Override
            public boolean isItemViewSwipeEnabled() {
                return (actionMode == null);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                showDeleteStudentConfirmationDialogFragment(viewHolder.getAdapterPosition());
            }

        }).attachToRecyclerView(mRecyclerView);
    }

    // Sets up a View Model attached to the lifecycle of this activity that observes changes to
    // the STUDENTS table db data an updates the RecyclerView and Widget when the data changes.
    private void setupViewModel() {
        final Context context = this;
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getStudents().observe(this, new Observer<List<StudentEntry>>() {
            @Override
            public void onChanged(@Nullable List<StudentEntry> studentEntries) {
                if (studentEntries != null) {
                    if (studentEntries.isEmpty()) {
                        showEmptyListMessages(true, studentEntries.size());
                        if (mMenu != null) {
                            showShareAndDeleteMenuItems(false);
                            showAddAndLoadStudentsMenuItems(true);
                            mMenu.findItem(R.id.menu_main_action_create_sample_class).setVisible(true);
                        }
                        mAdapter.setStudentsListData(studentEntries);
                    } else {
                        showEmptyListMessages(false, studentEntries.size());
                        if (mMenu != null) {
                            showShareAndDeleteMenuItems(true);
                            showAddAndLoadStudentsMenuItems(!(studentEntries.size() == MAX_NUMBER_OF_STUDENTS_IN_A_CLASS));
                            mMenu.findItem(R.id.menu_main_action_create_sample_class).setVisible(false);
                        }
                        mAdapter.setStudentsListData(StudentsOrderUtils.
                                UpdateStudentsListOrder(context, studentEntries));
                    }
                    NumberOfStudentsUtils.setNumberOfStudentsOnSharedPreferences(
                            MainActivity.this, studentEntries.size());
                    WidgetProvider.updateWidgetWithStudentsInformation(context);
                }
            }
        });
    }


    private void showDeleteStudentConfirmationDialogFragment(int position) {

        mIsAskingToDeleteStudent = true;
        mStudentWithDeleteBackgroundPosition = position;

        final StudentEntry studentEntry = mAdapter.getStudentEntry(position);
        boolean primaryColor = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(this)
                == studentEntry.getTestType();
        FragmentManager fragmentManager = getSupportFragmentManager();

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(
                                R.string.delete_student_confirmation_dialog_fragment_text,
                                studentEntry.getFirstName(), studentEntry.getLastName())),
                        getString(R.string.delete_student_confirmation_dialog_fragment_title),
                        primaryColor, position,
                        ConfirmationDialogFragment.MAIN_ACTIVITY_DELETE_STUDENT);

        confirmationDialogFragment.show(fragmentManager, getString(R.string.delete_student_confirmation_dialog_fragment_tag));

    }


    private void showAddNewStudentUI() {
        if (mTablet) {
            // On a tablet, display an AddStudent fragment on the detail container of main activity.
            displayAddStudentsFragmentOnDetailsContainer();
        } else {
            // On a phone, open a new activity and request the name of the added student
            Intent intent = new Intent(this, AddStudentActivity.class);
            intent.putExtra(NUMBER_OF_STUDENTS_KEY, mAdapter.getItemCount());
            startActivityForResult(intent, ADD_STUDENT_ACTIVITY_REQUEST_CODE);
        }
    }


    private void showLoadStudentsNotificationDialogFragment() {

        // If the value on Shared Preferences indicates that the Dialog Fragment has to be shown
        if (SharedPreferencesUtils.getShowLoadStudentsInfoValueFromSharedPreferences(this)) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            LoadStudentsNotificationDialogFragment loadStudentsNotificationDialogFragment =
                    LoadStudentsNotificationDialogFragment.newInstance(
                            getString(R.string.dialog_fragment_load_students_notification_title));

            loadStudentsNotificationDialogFragment.show(fragmentManager, getString(R.string.dialog_fragment_load_students_notification_tag));
        } else {
            // Show the file picker directly

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
    }


    private void showCreateSampleClassConfirmationDialogFragment() {
        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.create_sample_class_confirmation_dialog_fragment_text)),
                        getString(R.string.create_sample_class_confirmation_dialog_fragment_title),
                        true, 0,
                        ConfirmationDialogFragment.MAIN_ACTIVITY_CREATE_SAMPLE_CLASS);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.create_sample_class_confirmation_dialog_fragment_tag));
    }


    private void showSightWordsInfoUI() {
        if (mTablet) {
            replaceFragmentOnContainer(new WordListsInformationFragment());
        } else {
            Intent intent = new Intent(this, WordListsInformationActivity.class);
            startActivity(intent);
        }
    }


    private void showHelpUI() {
        if (mTablet) {
            replaceFragmentOnContainer(new HelpFragment());
        } else {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }
    }


    // Helper method that displays a fragment on the Detail Container of Main Activity.
    private void replaceFragmentOnContainer(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_main_detail_layout, fragment)
                .commit();
    }


    // Helper method that shows a message when the student list is empty
    private void showEmptyListMessages(boolean empty, int studentListSize) {

        mTitleTextView.setText(Html.fromHtml(TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(this, false)));

        String studentWordSuffix = getString(R.string.lowercase_letter_s);
        if (studentListSize == 1) {
            studentWordSuffix = EMPTY_STRING;
        }
        mSortByInfoTextView.setText(getString(R.string.activity_main_sorted_by_message,
                studentListSize, studentWordSuffix, StudentsOrderUtils.getStudentsOrderString(this)));

        if (empty) {
            mEmptyClassroomTextView1.setVisibility(View.VISIBLE);
            mEmptyClassroomTextView2.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyClassroomTextView1.setVisibility(View.GONE);
            mEmptyClassroomTextView2.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuCompat.setGroupDividerEnabled(menu, true);

        mMenu = menu;
        MenuItem createSampleClassMenuItem = mMenu.findItem(R.id.menu_main_action_create_sample_class);

        // When the menu is first created (when the application is loaded)
        // If there are not students in the class disable share, change and delete menu items, enable
        // add and load menu items.
        if (mAdapter != null) {
            if (mAdapter.getStudentsListData() != null) {
                if (mAdapter.getStudentsListData().isEmpty()) {
                    showShareAndDeleteMenuItems(false);
                    showAddAndLoadStudentsMenuItems(true);
                    createSampleClassMenuItem.setVisible(true);
                } else {
                    showShareAndDeleteMenuItems(true);
                    showAddAndLoadStudentsMenuItems(
                            mAdapter.getItemCount() != MAX_NUMBER_OF_STUDENTS_IN_A_CLASS);
                    createSampleClassMenuItem.setVisible(false);
                }
            } else {
                showShareAndDeleteMenuItems(false);
                showAddAndLoadStudentsMenuItems(true);
                createSampleClassMenuItem.setVisible(true);
            }
        } else {
            showShareAndDeleteMenuItems(false);
            showAddAndLoadStudentsMenuItems(true);
            createSampleClassMenuItem.setVisible(true);
        }

        return true;
    }


    // Defines the actions for the menu. After an item on this menu is clicked an event is sent
    // to Google Analytics identifying the action.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_main_action_sort:
                showSortDialogFragment();
                break;
            case R.id.menu_main_action_share:
                showShareDialogFragment();
                break;
            case R.id.menu_main_action_add_students:
                showAddNewStudentUI();
                break;
            case R.id.menu_main_action_load_students:
                showLoadStudentsNotificationDialogFragment();
                break;
            case R.id.menu_main_action_create_sample_class:
                showCreateSampleClassConfirmationDialogFragment();
                break;
            case R.id.menu_main_action_delete:
                showDeleteClassConfirmationDialogFragment();
                break;
            case R.id.menu_main_action_change:
                showChangeTestDialogFragment();
                break;
            case R.id.menu_main_action_download:
                showDownloadResourcesDialogFragment();
                break;
            case R.id.menu_main_action_sight_words_info:
                showSightWordsInfoUI();
                break;
            case R.id.menu_main_action_help:
                showHelpUI();
                break;
            case R.id.menu_main_action_feedback:
                showFeedbackConfirmationDialogFragment();
                break;
            case R.id.menu_main_action_donate:
                showDonateConfirmationDialogFragment();
                break;
            case R.id.menu_main_action_about:
                showAboutMessageDialogFragment();
        }

        return super.onOptionsItemSelected(item);
    }


    // Helper method that download a resource on this device.
    private void downloadResource(String resourceUrl) {
        Uri resourceUri = Uri.parse(resourceUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, resourceUri);
        startActivity(intent);
    }


    private void showShareAndDeleteMenuItems(boolean show) {
        mMenu.findItem(R.id.menu_main_action_share).setVisible(show);
        mMenu.findItem(R.id.menu_main_action_delete).setVisible(show);
    }


    private void showAddAndLoadStudentsMenuItems(boolean show) {
        mMenu.findItem(R.id.menu_main_action_add_students).setVisible(show);
        mMenu.findItem(R.id.menu_main_action_load_students).setVisible(show);
    }


    // Shows test results for the clicked student
    @Override
    public void onNameDateClick(StudentEntry studentEntry, int position) {

        // If there are students selected (action mode is enabled) set up action mode
        if (mAdapter.getSelectedStudentsCount() > 0) {
            setupItemForActionMode(position);
        } else {
            // If there are no students selected show StudentTestResultsFragment
            if (mTablet) {
                displayStudentTestResultFragmentOnDetailsContainer(studentEntry);
            } else {
                // On a phone, open a new activity, passing the id, student first and last name and student
                // test type
                Intent intent = new Intent(this, StudentTestResultsActivity.class);
                intent.putExtra(STUDENT_ENTRY_KEY, studentEntry);
                startActivity(intent);

            }
        }
    }

    // Displays an AlertDialog with information about the last test taken by the student
    @Override
    public void onGradeClick(StudentEntry studentEntry, int position) {

        // If there are students selected (action mode is enabled) set up action mode
        if (mAdapter.getSelectedStudentsCount() > 0) {
            setupItemForActionMode(position);
        } else {

            String htmlMessage;

            boolean isDefaultTest = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(this)
                    == studentEntry.getTestType();

            int grade = studentEntry.getGrade();
            int numberOfWords = WordUtils.getNumberOfWordsOnList(studentEntry.getTestType());

            if (grade == numberOfWords) {
                htmlMessage = getString(R.string.test_information_without_incorrect_words_alert_dialog_text,
                        studentEntry.getFirstName(), studentEntry.getLastName(),
                        TestTypeUtils.getTestTypeTitle(MainActivity.this, studentEntry.getTestType(), isDefaultTest),
                        mSimpleDateFormat.format(studentEntry.getLastTestDate()));

            } else {
                htmlMessage = getString(R.string.test_information_with_incorrect_words_alert_dialog_text,
                        studentEntry.getFirstName(), studentEntry.getLastName(),
                        TestTypeUtils.getTestTypeTitle(MainActivity.this, studentEntry.getTestType(), isDefaultTest),
                        mSimpleDateFormat.format(studentEntry.getLastTestDate()), grade,
                        studentEntry.getUnknownWords());
            }

            MessageDialogFragment messageDialogFragment =
                    MessageDialogFragment.newInstance(
                            Html.fromHtml(htmlMessage), getString(R.string.last_test_information_alert_dialog_title),
                            isDefaultTest, MAIN_ACTIVITY, EMPTY_STRING);

            messageDialogFragment.show(getSupportFragmentManager(),
                    getString(R.string.activity_main_test_information_dialog_fragment_tag));

        }
    }

    @Override
    public void onRowLongClick(int position) {
        setupItemForActionMode(position);
    }


    // Helper method for tablets that displays the StudentTestResultsFragment on the detail container
    // passing the student id and name as parameters to the fragment
    private void displayStudentTestResultFragmentOnDetailsContainer(StudentEntry studentEntry) {
        StudentTestResultsFragment studentTestResultsFragment = new StudentTestResultsFragment();
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putParcelable(STUDENT_ENTRY_KEY, studentEntry);
        studentTestResultsFragment.setArguments(fragmentArguments);
        replaceFragmentOnContainer(studentTestResultsFragment);
    }


    // Helper method for tablets that displays the StudentTestResultsFragment on the detail container
    // passing the student id and name as parameters to the fragment
    private void displayAddStudentsFragmentOnDetailsContainer() {
        AddStudentFragment addStudentFragment = new AddStudentFragment();
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putInt(NUMBER_OF_STUDENTS_KEY, mAdapter.getItemCount());
        addStudentFragment.setArguments(fragmentArguments);
        replaceFragmentOnContainer(addStudentFragment);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there are students selected
        if (mAdapter.getSelectedStudentsCount() > 0) {
            outState.putParcelable(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY, mAdapter.getSelectedStudents());
        }

        // If the a dialog fragment asking for confirmation to delete a student is displayed
        if (mIsAskingToDeleteStudent) {
            outState.putBoolean(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY, mRightSwipe);
            outState.putInt(SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY, mStudentWithDeleteBackgroundPosition);
        }
    }


    // Gets the name of the added student as a result from AddStudentActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Gets the added student name from AddStudentFragment
        if (requestCode == ADD_STUDENT_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String firstName = data.getStringExtra(AddStudentActivity.STUDENT_ADDED_FIRST_NAME_KEY);
            String lastName = data.getStringExtra(AddStudentActivity.STUDENT_ADDED_LAST_NAME_KEY);
            int numberOfStudents = data.getIntExtra(AddStudentActivity.STUDENT_ADDED_NUMBER_OF_STUDENTS_KEY, 0);
            if (numberOfStudents == MAX_NUMBER_OF_STUDENTS_IN_A_CLASS) {
                showMaxNumberOfStudentsReachedMessageDialogFragment(firstName + " " + lastName);
            } else {
                Toast.makeText(this, getString(R.string.new_student_added_toast_text_1, firstName, lastName), Toast.LENGTH_LONG).show();
            }
        }
        // Gets the Uri to the document the user selected fon the "Load Students" menu option file picker
        else if (requestCode == READ_CSV_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK & data != null) {
            // A URI to the document will be contained in the return intent
            loadStudentsFromCSVUri(data.getData());
        }
    }


    private List<Byte> loadStudentsToDB(final List<String> studentNames) {

        Future<List<Byte>> loadStudentFuture = AppExecutors.getInstance().diskIO().submit(new Callable<List<Byte>>() {
            @Override
            public List<Byte> call() {
                List<Byte> loadResults = new ArrayList<>();
                String firstName;
                String lastName;
                byte loadResult;
                boolean studentNameComplete;

                if (mAppDatabase != null) {

                    int classSize = mAdapter.getItemCount();
                    // Flag that indicates if the max number of students in the class has been reached when inserting students
                    // to the Database
                    boolean maxNumberOfStudentsReached = false;

                    // Get the first name and last name of each student in the CSV file and insert the student in the DB
                    for (String studentName : studentNames) {

                        if (studentName.contains(",")) {
                            firstName = studentName.substring(0, studentName.indexOf(',')).trim();
                            lastName = studentName.substring(studentName.indexOf(',') + 1).trim();
                        } else {
                            firstName = studentName.trim();
                            lastName = EMPTY_STRING;
                        }

                        loadResult = STUDENT_NOT_LOADED_SUCCESSFULLY;
                        studentNameComplete = true;

                        if (firstName.isEmpty()) {
                            studentNameComplete = false;
                            if (lastName.isEmpty()) {
                                loadResult = STUDENT_NAME_INCOMPLETE_NO_FIRST_NAME_NO_LAST_NAME;
                            } else {
                                loadResult = STUDENT_NAME_INCOMPLETE_NO_FIRST_NAME;
                            }
                        } else if (lastName.isEmpty()) {
                            studentNameComplete = false;
                            loadResult = STUDENT_NAME_INCOMPLETE_NO_LAST_NAME;
                        }

                        if (studentNameComplete) {

                            if (classSize > MAX_NUMBER_OF_STUDENTS_IN_A_CLASS - 1) {
                                loadResult = MAX_CLASS_SIZE_EXCEEDED;
                            } else {

                                firstName = WordUtils.capitalizeFully(firstName);
                                lastName = WordUtils.capitalizeFully(lastName);

                                final StudentEntry studentEntry = new StudentEntry(firstName, lastName,
                                        STUDENT_WITH_NO_TESTS_GRADE,
                                        TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(MainActivity.this),
                                        new Date(), EMPTY_STRING);

                                try {
                                    mAppDatabase.studentDao().insertStudent(studentEntry);
                                    loadResult = STUDENT_LOADED_SUCCESSFULLY;
                                    classSize++;
                                    if (classSize == MAX_NUMBER_OF_STUDENTS_IN_A_CLASS) {
                                        maxNumberOfStudentsReached = true;
                                    }
                                } catch (SQLiteConstraintException e) {
                                    // If there was an error indicating the student is already in the DB
                                    if (e.toString().contains(SQLITE_CONSTRAINT_UNIQUE_CODE)) {
                                        loadResult = STUDENT_DUPLICATED;
                                    }
                                    Log.e(SIMPLE_NAME, Objects.requireNonNull(e.getMessage()));
                                }
                            }
                        }
                        loadResults.add(loadResult);
                    }
                    if (maxNumberOfStudentsReached) {
                        loadResults.add(MAX_CLASS_SIZE_EXCEED_FLAG);
                    }
                    return loadResults;
                } else {
                    return null;
                }
            }
        });

        List<Byte> loadResults = new ArrayList<>();
        try {
            loadResults = loadStudentFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return loadResults;
    }


    /**
     * Reads each line of the CVS file and saves it on a list of Strings
     *
     * @param uri The URI to the CSV file loaded
     * @return a list of Strings with each string containing a first name and last name
     * @throws IOException Error when reading data from the CSV file.
     */
    private List<String> getStudentsNamesFromUri(Uri uri) throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader bufferedReader = null;

        if (inputStream != null) {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));
        }

        List<String> studentNames = new ArrayList<>();
        String line;

        if (bufferedReader != null) {
            while ((line = bufferedReader.readLine()) != null) {
                studentNames.add(line);
            }
        }

        if (inputStream != null) {
            inputStream.close();
        }
        if (bufferedReader != null) {
            bufferedReader.close();
        }

        return studentNames;
    }


    // Implementation of the interface  SortStudentsDialogFragment.SortStudentsDialogFragmentListener,
    // It receives the sort criteria selected by the user on SortStudentsDialogFragment
    @Override
    public void onSortStudents(int sortCriteriaSelected) {

        int confirmationMessageId = 0;

        switch (sortCriteriaSelected) {
            case SORT_BY_FIRST_NAME_VALUE:
                confirmationMessageId = R.string.menu_main_action_sort_by_first_name_confirmation;
                break;
            case SORT_BY_LAST_NAME_VALUE:
                confirmationMessageId = R.string.menu_main_action_sort_by_last_name_confirmation;
                break;
            case SORT_BY_TEST_RESULT_VALUE:
                confirmationMessageId = R.string.menu_main_action_sort_by_test_results_confirmation;
                break;
            case SORT_BY_TEST_TYPE_VALUE:
                confirmationMessageId = R.string.menu_main_action_sort_by_test_type_confirmation;
                break;
        }

        // If the sorting criteria is different from the previous one
        if (StudentsOrderUtils.setStudentOrderValueOnSharedPreferences(this, sortCriteriaSelected)) {
            if (mAdapter.getStudentsListData() != null) {
                mAdapter.setStudentsListData(StudentsOrderUtils.
                        UpdateStudentsListOrder(this, mAdapter.getStudentsListData()));
                WidgetProvider.updateWidgetWithStudentsInformation(this);
                String studentWordSuffix = getString(R.string.lowercase_letter_s);
                if (mAdapter.getItemCount() == 1) {
                    studentWordSuffix = EMPTY_STRING;
                }
                mSortByInfoTextView.setText(getString(R.string.activity_main_sorted_by_message,
                        mAdapter.getItemCount(), studentWordSuffix, StudentsOrderUtils.getStudentsOrderString(this)));
            }
        }

        Toast.makeText(this, confirmationMessageId, Toast.LENGTH_SHORT).show();

    }

    // Implementation of the interface ShareDialogFragment.ShareDialogFragmentListener
    // It receives the share document type result selected by the user on ShareDialogFragment
    @Override
    public void onShare(int shareDocumentTypeSelected) {

        /* Checks if external storage is available for read and write */
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            ShareResult shareResult = null;
            List<StudentEntry> studentEntries = mAdapter.getStudentsListData();
            String fileNamePart1 = getString(R.string.share_class_results_text);

            switch (shareDocumentTypeSelected) {
                case PDF_SIMPLE_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareSimplePdfFile(this, studentEntries, null,
                            fileNamePart1, null, null,
                            0);
                    break;
                case PDF_DETAILED_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareDetailedPdfFile(this, studentEntries, null,
                            fileNamePart1, null, null,
                            0);
                    break;
                case CSV_SIMPLE_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareCsvFile(this, studentEntries, null,
                            false, fileNamePart1, 0);
                    break;
                case CSV_DETAILED_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareCsvFile(this, studentEntries, null,
                            true, fileNamePart1, 0);
                    break;
            }

            if (shareResult != null) {
                switch (shareResult.getCode()) {
                    case SHARE_RESULT_DATA_SHARED:
                        // Adds the file to a queue to be deleted later in the onResume or onCreate methods
                        mFilesToDelete.add(shareResult.getFile());
                        break;
                    case SHARE_RESULT_NO_APP:
                        Snackbar.make(mSnackView, R.string.activity_main_action_share_no_app_error, 4000).show();
                        break;
                    case SHARE_RESULT_NO_FILE_CREATED:
                        Snackbar.make(mSnackView, R.string.activity_main_action_share_file_creation_error, 4000).show();
                        break;
                }
            }

        } else {
            Snackbar.make(mSnackView, R.string.activity_main_action_save_storage_error, 4000).show();
        }
    }


    // Implementation of the interface ChangeTestDialogFragment.ChangeTestDialogFragmentListener
    // It receives a test type selected by the user on ChangeTestDialogFragment
    @Override
    public void onChangeTest(int testType) {

        if (testType == TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(this)) {
            Toast.makeText(this, getString(R.string.change_test_dialog_fragment_default_test_not_changed_message),
                    Toast.LENGTH_SHORT).show();

        } else {
            TestTypeUtils.setDefaultTestTypeValueOnSharedPreferences(this, testType);
            mAdapter.setDefaultTestType();
            mTitleTextView.setText(Html.fromHtml(TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(this, false)));
            WidgetProvider.updateWidgetWithStudentsInformation(this);

            Toast.makeText(this, getString(R.string.change_test_dialog_fragment_default_test_changed_message),
                    Toast.LENGTH_SHORT).show();
        }
    }


    // Implementation of the interface ConfirmationDialogFragment.ConfirmationDialogFragmentListener
    // It receives a yes or no answer from the user on ConfirmationDialogFragment
    @Override
    public void onConfirmation(boolean answerYes, final int studentToDeletePosition,
                               byte dialogFragmentCallerType) {

        switch (dialogFragmentCallerType) {
            case ConfirmationDialogFragment.MAIN_ACTIVITY_DELETE_STUDENT:
                mIsAskingToDeleteStudent = false;
                mAdapter.setStudentWithDeleteBackgroundData(-1, false);
                if (answerYes) {

                    final StudentEntry studentEntry = mAdapter.getStudentEntry(studentToDeletePosition);

                    // Delete the student from the adapter
                    mAdapter.removeStudent(studentToDeletePosition);

                    // Delete student from the students table
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mAppDatabase.studentDao().deleteStudent(studentEntry);
                        }
                    });
                    // Delete student test results from the test table
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mAppDatabase.testDao().deleteTestsById(Objects.requireNonNull(studentEntry).getId());
                        }
                    });
                    if (mTablet) {
                        replaceFragmentOnContainer(new WordListsInformationFragment());
                    }

                    Toast.makeText(this, getString(R.string.menu_main_action_delete_student_deleted,
                            studentEntry.getFirstName(), studentEntry.getLastName()), Toast.LENGTH_SHORT).show();

                } else {
                    mAdapter.notifyItemChanged(studentToDeletePosition);
                }
                break;
            case ConfirmationDialogFragment.MAIN_ACTIVITY_DELETE_STUDENTS:
                if (answerYes) {
                    List<Integer> selectedItemPositions = mAdapter.getSelectedStudentsPositions();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        final StudentEntry studentEntry = mAdapter.getStudentEntry(selectedItemPositions.get(i));

                        // Delete the student from the adapter
                        mAdapter.removeStudent(selectedItemPositions.get(i));

                        // Delete student from the students table
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mAppDatabase.studentDao().deleteStudent(studentEntry);
                            }
                        });
                        // Delete student test results from the test table
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mAppDatabase.testDao().deleteTestsById(Objects.requireNonNull(studentEntry).getId());
                            }
                        });
                    }
                    if (mTablet) {
                        replaceFragmentOnContainer(new WordListsInformationFragment());
                    }
                    actionMode.finish();
                    Toast.makeText(this, getString(R.string.activity_main_students_deleted_text,
                            selectedItemPositions.size()), Toast.LENGTH_SHORT).show();
                }
                break;
            case ConfirmationDialogFragment.MAIN_ACTIVITY_DELETE_CLASS:
                if (answerYes) {
                    // Delete all tables from the database
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mAppDatabase.clearAllTables();
                        }
                    });
                }
                break;
            case ConfirmationDialogFragment.MAIN_ACTIVITY_FEEDBACK:
                if (answerYes) {
                    // Intent to send an email with feedback
                    Intent feedbackEmailIntent = new Intent(Intent.ACTION_SENDTO);
                    feedbackEmailIntent.setData(Uri.parse(getString(R.string.menu_main_action_feedback_email_address)));
                    feedbackEmailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.menu_main_action_feedback_email_subject));
                    feedbackEmailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.menu_main_action_feedback_email_body));
                    try {
                        startActivity(feedbackEmailIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, getString(R.string.menu_main_action_feedback_no_email_app_error), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case ConfirmationDialogFragment.MAIN_ACTIVITY_DONATE:
                if (answerYes) {
                    // Intent to open app donation webpage
                    Intent showDonationWebsiteIntent = new Intent(Intent.ACTION_VIEW);
                    showDonationWebsiteIntent.setData(Uri.parse(getString(R.string.menu_main_action_donate_website_url)));
                    try {
                        startActivity(showDonationWebsiteIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, getString(R.string.menu_main_action_donate_no_browser_app_error), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case ConfirmationDialogFragment.MAIN_ACTIVITY_CREATE_SAMPLE_CLASS:
                if (answerYes) {
                    Utils.createSampleClass(this);
                }
                break;
        }
    }

    // Implementation of the interface DownloadResourcesDialogFragment.DownloadResourcesDialogFragmentListener
    // It receives a download resource type from the user on DownloadResourcesDialogFragment
    @Override
    public void onDownloadResources(int resourceTypeSelected) {

        switch (resourceTypeSelected) {

            case DownloadResourcesDialogFragment.SHARE_RESOURCES_LINK_RESOURCE_TYPE_VALUE:
                Intent intentToShareText = new Intent(Intent.ACTION_SEND);
                intentToShareText.setType("text/plain");
                intentToShareText.putExtra(Intent.EXTRA_TEXT, getString(R.string.resources_webpage_url));
                intentToShareText.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.menu_main_action_download_resources_on_other_device_share_subject));
                startActivity(Intent.createChooser(intentToShareText,
                        getString(R.string.menu_main_action_download_resources_on_other_device_chooser_title)));
                break;

            case DownloadResourcesDialogFragment.DOWNLOAD_ALL_WORDS_LISTS_TYPE_VALUE:
                downloadResource(getString(R.string.all_words_lists_url));
                break;

            case DownloadResourcesDialogFragment.DOWNLOAD_ALL_FLASH_CARDS_TYPE_VALUE:
                downloadResource(getString(R.string.all_flash_cards_url));
                break;

            default:
                String[] resourcesUrls = getResources().getStringArray(R.array.resources_urls);
                downloadResource(resourcesUrls[resourceTypeSelected]);
        }

    }


    /**
     * Implementation of the interface LoadStudentsNotificationDialogFragment.LoadStudentsDialogFragmentListener
     *
     * @param uri Uri received that points to the file selected by the user. Null if nothing was selected.
     */
    @Override
    public void onLoadStudents(Uri uri) {
        if (uri != null) {
            loadStudentsFromCSVUri(uri);
        }
    }


    /**
     * Process and loads students from file pointed by Uri
     *
     * @param uri The URI pointing to the CSV file selected by the user
     */
    private void loadStudentsFromCSVUri(Uri uri) {
        List<String> studentNames;
        try {
            studentNames = getStudentsNamesFromUri(uri);

            int numberOfStudentsLoaded = 0;
            int numberOfStudentsNotLoaded = 0;

            List<Byte> loadResultsCodes = loadStudentsToDB(studentNames);

            int i = 0;
            StringBuilder studentsLoadedInfo = new StringBuilder();
            StringBuilder studentsNotLoadedInfo = new StringBuilder();
            String firstName, lastName;

            studentsLoadedInfo.append(getString(R.string.html_line_break));
            studentsNotLoadedInfo.append(getString(R.string.html_line_break));
            for (String studentName : studentNames) {

                if (studentName.contains(",")) {
                    firstName = studentName.substring(0, studentName.indexOf(',')).trim();
                    lastName = studentName.substring(studentName.indexOf(',') + 1).trim();
                } else {
                    firstName = studentName.trim();
                    lastName = EMPTY_STRING;
                }

                firstName = WordUtils.capitalizeFully(firstName);
                lastName = WordUtils.capitalizeFully(lastName);

                studentName = firstName + " " + lastName;

                byte loadResult = loadResultsCodes.get(i);
                if (loadResult == STUDENT_LOADED_SUCCESSFULLY) {
                    numberOfStudentsLoaded++;
                    studentsLoadedInfo
                            .append(studentName.trim())
                            .append(getString(R.string.html_line_break));
                } else {
                    numberOfStudentsNotLoaded++;
                    studentsNotLoadedInfo
                            .append(studentName.trim())
                            .append(" (")
                            .append(getString(R.string.html_italics_open))
                            .append(getNotLoadedReasonString(loadResult))
                            .append(getString(R.string.html_italics_close))
                            .append(")")
                            .append(getString(R.string.html_line_break));
                }
                i++;
            }
            studentsLoadedInfo.append(getString(R.string.html_line_break));
            studentsNotLoadedInfo.append(getString(R.string.html_line_break));


            LoadStudentsResultDialogFragment loadStudentsResultDialogFragment =
                    LoadStudentsResultDialogFragment.newInstance(
                            getString(R.string.dialog_fragment_load_students_result_title),
                            numberOfStudentsLoaded, numberOfStudentsNotLoaded,
                            studentsLoadedInfo.toString(), studentsNotLoadedInfo.toString(),
                            loadResultsCodes.get(loadResultsCodes.size() - 1) == MAX_CLASS_SIZE_EXCEED_FLAG);

            loadStudentsResultDialogFragment.show(getSupportFragmentManager(),
                    getString(R.string.dialog_fragment_load_students_result_tag));

        } catch (IOException e) {
            Snackbar.make(mSnackView, R.string.activity_main_action_load_file_read_error, 4000).show();
            e.printStackTrace();
        }
    }


    private String getNotLoadedReasonString(int loadResult) {

        String notLoadedReason = EMPTY_STRING;

        switch (loadResult) {
            case STUDENT_DUPLICATED:
                notLoadedReason = getString(R.string.activity_main_not_loaded_reason_student_duplicated);
                break;
            case STUDENT_NAME_INCOMPLETE_NO_FIRST_NAME:
                notLoadedReason = getString(R.string.activity_main_not_loaded_reason_no_first_name);
                break;
            case STUDENT_NAME_INCOMPLETE_NO_LAST_NAME:
                notLoadedReason = getString(R.string.activity_main_not_loaded_reason_no_last_name);
                break;
            case STUDENT_NAME_INCOMPLETE_NO_FIRST_NAME_NO_LAST_NAME:
                notLoadedReason = getString(R.string.activity_main_not_loaded_reason_no_first_name_no_last_name);
                break;
            case MAX_CLASS_SIZE_EXCEEDED:
                notLoadedReason = getString(R.string.activity_main_not_loaded_reason_max_size_exceeded);
                break;
        }

        return notLoadedReason;
    }


    private void showShareDialogFragment() {
        DialogFragmentUtils.showShareDialogFragment(
                getString(R.string.share_class_results_dialog_fragment_title),
                getString(R.string.share_class_results_dialog_fragment_tag),
                true, true,
                getSupportFragmentManager());
    }

    // Helper method that show a DialogFragment that lets the user select how to sort students
    private void showSortDialogFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortStudentsDialogFragment sortStudentsDialogFragment =
                SortStudentsDialogFragment.newInstance(
                        getString(R.string.sort_students_dialog_fragment_title));

        sortStudentsDialogFragment.show(fragmentManager, getString(R.string.sort_students_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user to select a new default test type
    private void showChangeTestDialogFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChangeTestDialogFragment changeTestDialogFragment =
                ChangeTestDialogFragment.newInstance(
                        getString(R.string.change_default_test_dialog_fragment_title),
                        TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(this),
                        true,
                        MAIN_ACTIVITY);

        changeTestDialogFragment.show(fragmentManager, getString(R.string.change_default_test_dialog_fragment_tag));
    }

    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete the class
    private void showDeleteClassConfirmationDialogFragment() {
        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.menu_main_action_delete_confirmation_dialog_fragment_text)),
                        getString(R.string.menu_main_action_delete_confirmation_dialog_fragment_title),
                        true, 0,
                        ConfirmationDialogFragment.MAIN_ACTIVITY_DELETE_CLASS);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.menu_main_action_delete_confirmation_dialog_fragment_tag));
    }

    // Helper method that show a DialogFragment that lets the user select the type of resource to download
    private void showDownloadResourcesDialogFragment() {
        DownloadResourcesDialogFragment confirmationDialogFragment =
                DownloadResourcesDialogFragment.newInstance(
                        getString(R.string.menu_main_action_download_title));

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.menu_main_action_download_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to provide feedback for the app
    private void showFeedbackConfirmationDialogFragment() {
        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.menu_main_action_feedback_confirmation_dialog_fragment_text)),
                        getString(R.string.menu_main_action_feedback_confirmation_dialog_fragment_title),
                        true, 0,
                        ConfirmationDialogFragment.MAIN_ACTIVITY_FEEDBACK);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.menu_main_action_feedback_confirmation_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to make a donation
    private void showDonateConfirmationDialogFragment() {
        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.menu_main_action_donate_confirmation_dialog_fragment_text)),
                        getString(R.string.menu_main_action_donate_confirmation_dialog_fragment_title),
                        true, 0,
                        ConfirmationDialogFragment.MAIN_ACTIVITY_DONATE);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.menu_main_action_donate_confirmation_dialog_fragment_tag));
    }

    // Helper method that show a DialogFragment that shows info about the application
    private void showAboutMessageDialogFragment() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.about_dialog_fragment_text,
                                getString(R.string.version_name))),
                        getString(R.string.about_dialog_fragment_title), true, MAIN_ACTIVITY_ABOUT_MESSAGE, EMPTY_STRING);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.about_dialog_fragment_tag));
    }

    // Helper method that show a DialogFragment that shows info about the application
    private void showMaxNumberOfStudentsReachedMessageDialogFragment(String addedStudentName) {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.max_reached_message_text_1, MAX_NUMBER_OF_STUDENTS_IN_A_CLASS)),
                        getString(R.string.max_reached_message_title), true, MAIN_ACTIVITY_MAX_NUMBER_OF_STUDENTS_REACHED, addedStudentName);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.max_reached_message_tag));
    }

    @Override
    public void onMaxNumberOfStudentsReached(String addedStudentName) {
        Toast.makeText(this, getString(R.string.new_student_added_toast_text_2, addedStudentName), Toast.LENGTH_SHORT).show();
    }


    // Implementation of the interface CustomLinkMovement.CustomLinkMovementListener
    @Override
    public void onAddStudentsLinkClicked() {
        showAddNewStudentUI();
    }


    // Implementation of the interface CustomLinkMovement.CustomLinkMovementListener
    @Override
    public void onLoadStudentsLinkClicked() {
        showLoadStudentsNotificationDialogFragment();
    }

    // Implementation of the interface CustomLinkMovement.CustomLinkMovementListener
    @Override
    public void onCreateSampleClassLinkClicked() {
        showCreateSampleClassConfirmationDialogFragment();
    }


    // Implementation of the interface CustomLinkMovement.CustomLinkMovementListener
    @Override
    public void onHelpLinkClicked() {
        showHelpUI();
    }


    /**
     * Class that implement ActionMode callbacks to change the menu when students are selected by a
     * long click and handle the deletion of those selected students.
     */
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                showDeleteStudentsConfirmationDialogFragment();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelectedStudents();
            actionMode = null;
            setupRecyclerViewDivider(LIGHT_LINE);
        }
    }


    private void showDeleteStudentsConfirmationDialogFragment() {

        String testWordSuffix = getString(R.string.lowercase_letter_s);
        String studentWordSuffix = getString(R.string.plural_possession_suffix);
        if (mAdapter.getSelectedStudentsCount() == 1) {
            testWordSuffix = EMPTY_STRING;
            studentWordSuffix = getString(R.string.singular_possession_suffix);
        }

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(
                                R.string.delete_students_confirmation_dialog_fragment_text,
                                mAdapter.getSelectedStudentsPositions().size(), testWordSuffix,
                                studentWordSuffix)),
                        getString(R.string.delete_students_confirmation_dialog_fragment_title),
                        true, 0, ConfirmationDialogFragment.MAIN_ACTIVITY_DELETE_STUDENTS);

        confirmationDialogFragment.show(getSupportFragmentManager(), getString(R.string.delete_students_confirmation_dialog_fragment_tag));
    }

    /**
     * Enables ActionMode to show a different menu, toggles the selected value of the student selected
     * and sets the title of the action mode menu to be the number of students selected
     *
     * @param position Position of the student clicked or long clicked.
     */
    private void setupItemForActionMode(int position) {

        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
            if (mAdapter.getSelectedStudentsCount() == 0) {
                setupRecyclerViewDivider(DARK_LINE);
            }
        }

        mAdapter.toggleStudentSelectionState(position);

        int selectedStudentsCount = mAdapter.getSelectedStudentsCount();

        // If the last selected student was deselected finish action mode
        if (selectedStudentsCount == 0) {
            actionMode.finish();
            setupRecyclerViewDivider(LIGHT_LINE);
        } else {
            actionMode.setTitle(getString(R.string.action_mode_toolbar_title, selectedStudentsCount));
            actionMode.invalidate();
        }
    }

    // Sets the recycler view divider to be a light line or a dark line
    private void setupRecyclerViewDivider(boolean light) {
        int drawableId;
        if (light) {
            drawableId = R.drawable.horizontal_line_light;
        } else {
            drawableId = R.drawable.horizontal_line_dark;
        }
        mDividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable
                (getResources(), drawableId, null)));
    }

}