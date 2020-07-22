package com.weebly.hectorjorozco.sightwordstest.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.PointLabeler;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import com.weebly.hectorjorozco.sightwordstest.R;

import com.weebly.hectorjorozco.sightwordstest.adapters.TestsListAdapter;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.database.TestEntry;
import com.weebly.hectorjorozco.sightwordstest.executors.AppExecutors;
import com.weebly.hectorjorozco.sightwordstest.models.ShareResult;
import com.weebly.hectorjorozco.sightwordstest.models.SparseBooleanArrayParcelable;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ChangeTestDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ConfirmationDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.DeleteOldestStudentResultsDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.EditStudentNameDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ShareDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.utils.DateConverter;
import com.weebly.hectorjorozco.sightwordstest.utils.DialogFragmentUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.ShareUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;
import com.weebly.hectorjorozco.sightwordstest.viewmodels.StudentTestResultsViewModel;
import com.weebly.hectorjorozco.sightwordstest.viewmodels.StudentTestResultsViewModelFactory;
import com.weebly.hectorjorozco.sightwordstest.widget.WidgetProvider;

import java.io.File;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.CSV_DETAILED_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.CSV_SIMPLE_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.FOUR_SECONDS;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.PDF_DETAILED_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.PDF_SIMPLE_SHARE_DOCUMENT_TYPE;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_RESULT_DATA_SHARED;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_RESULT_NO_APP;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.SHARE_RESULT_NO_FILE_CREATED;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULTS_FRAGMENT_TEST_INFO;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_NO_TESTS;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_TESTS;
import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_TEST_RESULT_FRAGMENT_MAX_NUMBER_OF_TESTS_REACHED;
import static com.weebly.hectorjorozco.sightwordstest.ui.TestActivity.STUDENT_INFO_TO_PASS_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.TestActivity.TEST_ADDED_DATE_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.TestActivity.TEST_ADDED_GRADE_KEY;
import static com.weebly.hectorjorozco.sightwordstest.ui.TestActivity.TEST_ADDED_UNKNOWN_WORDS_KEY;
import static com.weebly.hectorjorozco.sightwordstest.utils.ShareUtils.deleteFilesFromExternalStoragePrivateDocumentsDirectory;


/**
 * Fragment that will show student test results
 */
public class StudentTestResultsFragment extends Fragment implements TestsListAdapter.ItemClickListener,
        ShareDialogFragment.ShareDialogFragmentListener,
        ConfirmationDialogFragment.ConfirmationDialogFragmentListener,
        EditStudentNameDialogFragment.EditStudentNameDialogFragmentListener,
        ChangeTestDialogFragment.ChangeTestDialogFragmentListener,
        MessageDialogFragment.MaxNumberOfTestsReachedListener,
        DeleteOldestStudentResultsDialogFragment.DeleteOldestStudentResultsDialogFragmentListener {

    private static final int GRAPH_LOWER_BOUNDARY = 0;
    private static final String SEVEN_SPACES = "       ";
    private static final String FIVE_SPACES = "     ";
    private static final String TWO_SPACES = "  ";
    private static final String EMPTY_STRING = "";
    private static final int STUDENT_WITH_NO_TESTS_GRADE = -1;
    private static final float GRAPH_POINT_LABELS_TEXT_SIZE = 14;
    private static final byte MAX_NUMBER_OF_TESTS_PER_STUDENT = 100;

    // Format used to display the date
    private static final String GRAPH_DATE_FORMAT = "MM/dd/yy";
    private static final String TEST_INFORMATION_ALERT_DIALOG_DATE_FORMAT = "MM/dd/yyy hh:mm aaa";
    private static final String ACTION_SHOW_GRAPH_FLAG_KEY = "action_show_graph_flag_key";
    private static final String STUDENT_ENTRY_KEY = "student_entry_key";

    private static final int TEST_ACTIVITY_REQUEST_CODE = 0;

    // Holds the student info passed from MainActivity
    private StudentEntry mStudentEntry;

    private XYPlot mXYPlot;
    private FragmentActivity mFragmentActivity;
    private RecyclerView mRecyclerView;
    private TextView mTitleTextView;
    private TextView mNumberOfTestsTextView;
    private TextView mNoTestsInstructionsTextView;
    private TestsListAdapter mAdapter;
    private FloatingActionButton mFab;
    private AppDatabase mAppDatabase;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Menu mMenu;
    private DividerItemDecoration mDividerItemDecoration;
    private FrameLayout mSnackView;

    private boolean mTablet;
    private boolean mIsDefaultTest;
    private boolean mActionShowGraph;
    private final List<File> mFilesToDelete = new ArrayList<>();

    // Used for action mode
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    // Used to save the swiped to delete test on rotation
    private boolean mIsAskingToDeleteTest;
    private int mTestWithDeleteBackgroundPosition;
    private boolean mRightSwipe;


    public StudentTestResultsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(
                R.layout.fragment_student_test_results, container, false);

        // Gets the activity that contains this fragment
        mFragmentActivity = getActivity();

        // Gets an instance of the application database.
        if (mFragmentActivity != null) {
            // Gets an instance of the Database
            mAppDatabase = AppDatabase.getInstance(mFragmentActivity.getApplicationContext());
        }

        deleteFilesFromExternalStoragePrivateDocumentsDirectory(Objects.requireNonNull(mFragmentActivity));

        // Assigns views in the rootView layout to their variables.
        mXYPlot = rootView.findViewById(R.id.fragment_student_test_results_plot);
        mFab = rootView.findViewById(R.id.fragment_student_test_results_fab);
        mTitleTextView = rootView.findViewById(R.id.fragment_student_test_results_title_text_view);
        mNumberOfTestsTextView = rootView.findViewById(R.id.fragment_student_test_results_number_of_tests_text_view);
        mNoTestsInstructionsTextView = rootView.findViewById(R.id.fragment_student_test_results_no_tests_instructions_text_view);
        mRecyclerView = rootView.findViewById(R.id.fragment_student_test_results_recycler_view);
        mSnackView = rootView.findViewById(R.id.fragment_student_test_results_snack_view);

        // True if it is a tablet, false otherwise
        mTablet = getResources().getBoolean(R.bool.tablet);

        mToolbar = rootView.findViewById(R.id.fragment_student_test_results_tool_bar);
        mCollapsingToolbarLayout = rootView.findViewById(R.id.fragment_student_test_results_collapsing_tool_bar_layout);

        // If on a tablet
        if (mTablet) {
            // If the fragment was just created from MainActivity get the StudentEntry object from
            // the arguments passed to this fragment.
            if (savedInstanceState == null) {
                Bundle arguments = getArguments();
                if (arguments != null) {
                    if (arguments.containsKey(MainActivity.STUDENT_ENTRY_KEY)) {
                        mStudentEntry = arguments.getParcelable(MainActivity.STUDENT_ENTRY_KEY);
                    }
                }
            } else {
                // After rotation restore the StudentEntry object
                mStudentEntry = savedInstanceState.getParcelable(STUDENT_ENTRY_KEY);
            }
            // Set the toolbar title to the student name from the arguments passed to this fragment
            if (mStudentEntry != null) {
                mToolbar.setTitle(mStudentEntry.getFirstName() + " " + mStudentEntry.getLastName());
            }

        } else {
            // If on a phone, if the fragment was just created from MainActivity get the StudentEntry
            // object from the container activity (StudentTestResultsActivity).
            if (savedInstanceState == null) {
                StudentTestResultsActivity activity = (StudentTestResultsActivity) getActivity();
                if (activity != null) {
                    if (activity.studentEntry != null) {
                        mStudentEntry = activity.studentEntry;
                    }
                }
            } else {
                // After rotation restore the StudentEntry object
                mStudentEntry = savedInstanceState.getParcelable(STUDENT_ENTRY_KEY);
            }

            if (mStudentEntry != null) {
                mToolbar.setTitle(mStudentEntry.getFirstName() + " " + mStudentEntry.getLastName());
            }

            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity fragmentActivity = getActivity();
                    if (fragmentActivity != null) {
                        getActivity().onBackPressed();
                    }
                }
            });

        }

        if (mStudentEntry != null) {
            mIsDefaultTest = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity)
                    == mStudentEntry.getTestType();
        }

        // Create test list adapter
        mAdapter = new TestsListAdapter(mFragmentActivity, this, mIsDefaultTest,
                Objects.requireNonNull(mStudentEntry).getTestType());

        actionModeCallback = new ActionModeCallback();
        mIsAskingToDeleteTest = false;
        mRightSwipe = false;

        setupToolbarMenu();

        setUpFab();

        setUpRecyclerView();

        setUpViewModel();

        // If this is the first time the fragment is created, sets the flag that indicates that the action
        // to take is to show the graph when the graph menu item is pressed to true.
        // After rotation get the value of that flag
        if (savedInstanceState == null) {
            mActionShowGraph = true;
        } else {
            if (savedInstanceState.containsKey(ACTION_SHOW_GRAPH_FLAG_KEY)) {
                mActionShowGraph = savedInstanceState.getBoolean(ACTION_SHOW_GRAPH_FLAG_KEY);
            }
            if (savedInstanceState.containsKey(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY)) {
                SparseBooleanArrayParcelable selectedTests = savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY);
                if (selectedTests != null && selectedTests.size() > 0) {

                    if (mTablet) {
                        MainActivity activity = (MainActivity) getActivity();
                        if (activity != null) {
                            actionMode = activity.startSupportActionMode(actionModeCallback);
                        }
                    } else {
                        StudentTestResultsActivity activity = (StudentTestResultsActivity) getActivity();
                        if (activity != null) {
                            actionMode = activity.startSupportActionMode(actionModeCallback);
                        }
                    }

                    if (actionMode != null) {
                        actionMode.setTitle(getString(R.string.action_mode_toolbar_title, selectedTests.size()));
                    }
                    mAdapter.setSelectedTests(selectedTests);
                    mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_line_dark));
                }
            }
            if (savedInstanceState.containsKey(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY) &&
                    savedInstanceState.containsKey(SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY)) {
                int position = savedInstanceState.getInt(SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY);
                boolean rightSwiped = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY);
                mAdapter.setTestWithDeleteBackgroundData(position, rightSwiped);
                mAdapter.notifyItemChanged(position);
                mIsAskingToDeleteTest = true;
                mTestWithDeleteBackgroundPosition = position;
                mRightSwipe = rightSwiped;
            }
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Delete the oldest CSV temporary file created and leave the newest one.
        if (mFilesToDelete.size() == 2) {
            boolean deleted = mFilesToDelete.get(0).delete();
            if (deleted) {
                mFilesToDelete.remove(0);
            }
        }

        // If next action for graph menu item is to show the graph then set its icon white and text blue,
        // icon blue and text clear blue otherwise
        MenuItem graphMenuItem = mMenu.findItem(R.id.menu_student_test_results_action_show_hide_graph);
        if (mActionShowGraph) {
            graphMenuItem.setIcon(R.drawable.ic_line_chart_outline_white_24dp);
            graphMenuItem.setTitle(Html.fromHtml(getString(R.string.menu_student_test_results_action_show_graph_title)));
        } else {
            graphMenuItem.setIcon(R.drawable.ic_line_chart_outline_blue_24dp);
            graphMenuItem.setTitle(Html.fromHtml(getString(R.string.menu_student_test_results_action_hide_graph_title)));
        }

    }


    private void setupToolbarMenu() {
        mToolbar.inflateMenu(R.menu.menu_student_test_results);

        mMenu = mToolbar.getMenu();

        MenuCompat.setGroupDividerEnabled(mMenu, true);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.menu_student_test_results_action_show_hide_graph:
                        setupGraphVisibility(menuItem);
                        break;
                    case R.id.menu_student_test_results_action_share:
                        showShareDialogFragment();
                        break;
                    case R.id.menu_student_test_results_action_delete:
                        showDeleteAllTestsConfirmationDialogFragment();
                        break;
                    case R.id.menu_student_test_results_action_delete_old_results:
                        showDeleteOldestTestsDialogFragment();
                        break;
                    case R.id.menu_student_test_results_action_edit:
                        showEditStudentNameDialogFragment();
                        break;
                    case R.id.menu_student_test_results_action_change:
                        showChangeTestDialogFragment();
                        break;
                }

                return true;
            }
        });
    }


    private void setUpFab() {
        // Set up Floating Action Button to launch TestActivity when clicked.
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getItemCount() == MAX_NUMBER_OF_TESTS_PER_STUDENT) {
                    Toast.makeText(mFragmentActivity,
                            getString(R.string.fragment_student_test_results_max_number_of_tests_text, MAX_NUMBER_OF_TESTS_PER_STUDENT),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mFragmentActivity, TestActivity.class);
                    intent.putExtra(STUDENT_INFO_TO_PASS_KEY, mStudentEntry);
                    startActivityForResult(intent, TEST_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        TooltipCompat.setTooltipText(mFab, getString(R.string.fragment_student_test_results_fab_tooltip));
    }

    private void setUpRecyclerView() {
        // Set up RecyclerView
        mDividerItemDecoration = new DividerItemDecoration(mFragmentActivity.getApplicationContext(), VERTICAL);
        mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_line_light));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentActivity));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);

        // Add a touch helper to the RecyclerView to recognize when a user swipes to delete a test.
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
                    getDefaultUIUtil().onSelected(((TestsListAdapter.TestViewHolder) viewHolder).viewForeground);
                }
            }

            // Sets the foreground view to be moving when swiped
            @Override
            public void onChildDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDrawOver(canvas, recyclerView, ((TestsListAdapter.TestViewHolder) viewHolder).viewForeground,
                        dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDraw(c, recyclerView, ((TestsListAdapter.TestViewHolder) viewHolder).viewForeground,
                        dX, dY, actionState, isCurrentlyActive);

                // Right swipe
                if (dX > 0) {
                    mRightSwipe = true;
                    ((TestsListAdapter.TestViewHolder) viewHolder).viewRightSwipeBackground.setVisibility(View.VISIBLE);
                    ((TestsListAdapter.TestViewHolder) viewHolder).viewLeftSwipeBackground.setVisibility(View.INVISIBLE);
                } else {
                    // Left swipe
                    mRightSwipe = false;
                    ((TestsListAdapter.TestViewHolder) viewHolder).viewRightSwipeBackground.setVisibility(View.INVISIBLE);
                    ((TestsListAdapter.TestViewHolder) viewHolder).viewLeftSwipeBackground.setVisibility(View.VISIBLE);
                }

            }

            // If the student is not deleted sets the view again to foreground
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                getDefaultUIUtil().clearView(((TestsListAdapter.TestViewHolder) viewHolder).viewForeground);
            }

            // Disables swipe when Action Mode is enabled
            @Override
            public boolean isItemViewSwipeEnabled() {
                return (actionMode == null);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                showDeleteTestConfirmationDialogFragment(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(mRecyclerView);
    }


    // Sets up a View Model attached to the lifecycle of this fragment that observes changes to
    // the TESTS table db data an updates the RecyclerView and Widget when the data changes.
    private void setUpViewModel() {
        // Creates a View Model Factory that will provide the View Model
        StudentTestResultsViewModelFactory viewModelFactory =
                new StudentTestResultsViewModelFactory(mAppDatabase, mStudentEntry.getId());

        final StudentTestResultsViewModel viewModel
                = ViewModelProviders.of(this, viewModelFactory).get(StudentTestResultsViewModel.class);

        // Observe the LiveData object in the ViewModel
        viewModel.getTestEntries().observe(this, new Observer<List<TestEntry>>() {
            @Override
            public void onChanged(@Nullable List<TestEntry> testEntries) {
                if (testEntries != null) {
                    mAdapter.setTestsListData(testEntries);
                    WidgetProvider.updateWidgetWithStudentsInformation(mFragmentActivity);

                    // If there are not tests disable share, and delete menu items. Also hide graph
                    if (testEntries.isEmpty()) {
                        showEmptyListMessage(true, testEntries.size());
                        mXYPlot.setVisibility(View.GONE);
                        if (mMenu != null) showShareAndDeleteMenuItems(false);
                    } else {
                        // If there are tests enable share and delete menu items.
                        showEmptyListMessage(false, testEntries.size());
                        if (mMenu != null) showShareAndDeleteMenuItems(true);
                        // If only one test hide graph
                        if (testEntries.size() == 1) {
                            mXYPlot.setVisibility(View.GONE);
                            disableDeleteOldestTestsMenuItem();
                        } else {
                            // If more than one test and the next action of the graph menu item is
                            // to hide the graph then show the graph
                            if (!mActionShowGraph) {
                                mXYPlot.setVisibility(View.VISIBLE);
                            }
                            setGraphData(testEntries);
                        }
                    }
                }
            }
        });
    }


    private void showEmptyListMessage(boolean empty, int numberOfTests) {
        if (mIsDefaultTest) {
            mTitleTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            mNumberOfTestsTextView.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            mNoTestsInstructionsTextView.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        } else {
            mTitleTextView.setTextColor(getResources().getColor(R.color.colorSecondaryDark));
            mNumberOfTestsTextView.setTextColor(getResources().getColor(R.color.colorSecondaryLight));
            mNoTestsInstructionsTextView.setTextColor(getResources().getColor(R.color.colorSecondaryLight));
        }
        mTitleTextView.setText(Html.fromHtml(TestTypeUtils.getTestTypeTitleForStudentTestResultsFragment
                (mFragmentActivity, mStudentEntry.getTestType(), mIsDefaultTest)));
        if (empty) {
            mNumberOfTestsTextView.setText(getString(R.string.fragment_student_test_results_no_tests_text));
            mNoTestsInstructionsTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            String testWordSuffix = getString(R.string.lowercase_letter_s);
            if (numberOfTests == 1) {
                testWordSuffix = EMPTY_STRING;
            }
            mNumberOfTestsTextView.setText(getString(R.string.fragment_student_test_results_number_of_tests_text,
                    numberOfTests, testWordSuffix));
            mNoTestsInstructionsTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Enables the share and delete menu items when there is at least one tests on the list. Disables them otherwise.
     *
     * @param show True when the two menu items will be enabled. False when they will be disabled.
     */
    private void showShareAndDeleteMenuItems(boolean show) {
        mMenu.findItem(R.id.menu_student_test_results_action_share).setVisible(show);
        mMenu.findItem(R.id.menu_student_test_results_action_delete).setVisible(show);
        mMenu.findItem(R.id.menu_student_test_results_action_delete_old_results).setVisible(show);
    }


    private void disableDeleteOldestTestsMenuItem() {
        mMenu.findItem(R.id.menu_student_test_results_action_delete_old_results).setVisible(false);
    }


    /**
     * Sets up the graph of the test results
     *
     * @param testEntries the List of TestEntry objects that will be displayed in the graph.
     */
    private void setGraphData(final List<TestEntry> testEntries) {

        int graphColor;
        if (mIsDefaultTest) {
            graphColor = mFragmentActivity.getResources().getColor(R.color.colorPrimaryDark);
        } else {
            graphColor = mFragmentActivity.getResources().getColor(R.color.colorSecondaryDark);
        }

        // Clears any previous data from the plot and redraws it
        mXYPlot.clear();
        mXYPlot.redraw();

        // Sets the color of the graph border
        Paint paint = new Paint();
        paint.setColor(graphColor);
        mXYPlot.setBorderPaint(paint);

        // Used to format the dates displayed as domain labels in the graph
        SimpleDateFormat formatter = new SimpleDateFormat(GRAPH_DATE_FORMAT, Locale.getDefault());

        // Array of x-values labels (student test dates):
        final String[] domainLabels = new String[testEntries.size()];
        // Array of y-values to plot (student test results):
        Number[] rangeNumbers = new Number[testEntries.size()];

        // Calculate the graph upper boundary and the graph range step based on the test type
        final int numberOfWordsOnList = WordUtils.getNumberOfWordsOnList(mStudentEntry.getTestType());
        int graphRangeStep;
        if (numberOfWordsOnList < 11) {
            graphRangeStep = 2;
        } else if (numberOfWordsOnList < 26) {
            graphRangeStep = 5;
        } else if (numberOfWordsOnList < 51) {
            graphRangeStep = 10;
        } else if (numberOfWordsOnList < 101) {
            graphRangeStep = 20;
        } else if (numberOfWordsOnList < 251) {
            graphRangeStep = 50;
        } else if (numberOfWordsOnList < 501) {
            graphRangeStep = 100;
        } else if (numberOfWordsOnList < 1001) {
            graphRangeStep = 200;
        } else {
            graphRangeStep = 500;
        }
        int graphUpperBoundary = numberOfWordsOnList + graphRangeStep;

        // Populate the previous arrays with the values on testEntries list
        for (int i = 0; i < testEntries.size(); i++) {
            domainLabels[i] = formatter.format(testEntries.get(i).getDate());
            rangeNumbers[i] = testEntries.get(i).getGrade();
        }

        // turn the rangeNumbers array into XYSeries:
        XYSeries series = new SimpleXYSeries(
                Arrays.asList(rangeNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                getString(R.string.series_title));

        // create formatters to use for drawing a series using LineAndPointRenderer and configure
        // them from xml:
        LineAndPointFormatter lineAndPointFormatter;
        if (mIsDefaultTest) {
            lineAndPointFormatter = new LineAndPointFormatter(mFragmentActivity.getApplicationContext(),
                    R.xml.line_point_formatter_with_labels_primary_color);
        } else {
            lineAndPointFormatter = new LineAndPointFormatter(mFragmentActivity.getApplicationContext(),
                    R.xml.line_point_formatter_with_labels_secondary_color);
        }
        PointLabelFormatter pointLabelFormatter = new PointLabelFormatter();
        pointLabelFormatter.getTextPaint().setTextSize(PixelUtils.spToPix(GRAPH_POINT_LABELS_TEXT_SIZE));
        pointLabelFormatter.getTextPaint().setFakeBoldText(true);
        pointLabelFormatter.getTextPaint().setColor(graphColor);
        lineAndPointFormatter.setPointLabelFormatter(pointLabelFormatter);
        lineAndPointFormatter.setPointLabeler(new PointLabeler() {
            @Override
            public String getLabel(XYSeries series, int index) {
                // if this is the first label add spaces before it, if it is the last label add
                // spaces after it.
                int yValue = series.getY(index).intValue();
                String spaces = FIVE_SPACES;
                if (yValue > 99) {
                    spaces = SEVEN_SPACES;
                }
                if (index == 0) {
                    return spaces + yValue;
                } else if (index == testEntries.size() - 1) {
                    return yValue + spaces;
                } else {
                    return String.valueOf(yValue);
                }
            }
        });

        // add a new series' to the xy plot and set its formatter:
        mXYPlot.addSeries(series, lineAndPointFormatter);

        // setup domain (X) and range (Y)
        mXYPlot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
        mXYPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, graphRangeStep);
        mXYPlot.setRangeBoundaries(GRAPH_LOWER_BOUNDARY, graphUpperBoundary, BoundaryMode.FIXED);

        XYGraphWidget graphWidget = mXYPlot.getGraph();

        // The format of the domain (x) labels.
        graphWidget.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        // The format of the range (y) labels
        graphWidget.getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                // obj contains the raw Number value representing the position of the label being drawn.
                // customize the labeling however you want here:
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(i).append(TWO_SPACES);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                // unused
                return null;
            }
        });

        // Sets the background of the graph
        paint = new Paint();
        paint.setColor(mFragmentActivity.getResources().getColor(R.color.appBackground));
        graphWidget.setBackgroundPaint(paint);


        float fontScale = getResources().getConfiguration().fontScale;

        if (fontScale > 1.35) {
            graphWidget.setPaddingLeft(
                    getResources().getDimension(R.dimen.test_results_graph_padding_left_huge));
            graphWidget.setPaddingBottom(
                    getResources().getDimension(R.dimen.test_results_graph_padding_bottom_huge));
        } else if (fontScale > 1.2) {
            graphWidget.setPaddingLeft(
                    getResources().getDimension(R.dimen.test_results_graph_padding_left_big));
            graphWidget.setPaddingBottom(
                    getResources().getDimension(R.dimen.test_results_graph_padding_bottom_big));
        } else if (fontScale > 1.1) {
            graphWidget.setPaddingLeft(
                    getResources().getDimension(R.dimen.test_results_graph_padding_left_medium));
            graphWidget.setPaddingBottom(
                    getResources().getDimension(R.dimen.test_results_graph_padding_bottom_medium));
        } else if (fontScale > 1.0) {
            graphWidget.setPaddingLeft(
                    getResources().getDimension(R.dimen.test_results_graph_padding_left_small));
            graphWidget.setPaddingBottom(
                    getResources().getDimension(R.dimen.test_results_graph_padding_bottom_small));
        } else if (fontScale > 0.9) {
            if (numberOfWordsOnList > 999) {
                graphWidget.setPaddingLeft(
                        getResources().getDimension(R.dimen.test_results_graph_padding_left_small));
            }
        }

        // Removes the series legend from the graph
        mXYPlot.getLayoutManager().remove(mXYPlot.getLegend());

        // Lets the graph pan and zoom on the set limits
        PanZoom.attach(mXYPlot, PanZoom.Pan.BOTH, PanZoom.Zoom.STRETCH_HORIZONTAL);

        // Sets the graph limits for panning and zooming
        mXYPlot.getOuterLimits().set(0, testEntries.size() - 1, 0, graphUpperBoundary);


    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete
    // all the student's test results
    private void showDeleteAllTestsConfirmationDialogFragment() {

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.menu_student_test_results_action_delete_confirmation_dialog_fragment_text,
                                mStudentEntry.getFirstName(), mStudentEntry.getLastName())),
                        getString(R.string.menu_student_test_results_action_delete_confirmation_dialog_fragment_title),
                        mIsDefaultTest, 0,
                        ConfirmationDialogFragment.STUDENT_TEST_RESULTS_FRAGMENT_DELETE_ALL_TESTS);

        // Sets StudentTestResultsFragment as the target fragment to get results from ConfirmationDialogFragment
        confirmationDialogFragment.setTargetFragment(this, 100);

        confirmationDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                getString(R.string.menu_student_test_results_action_delete_confirmation_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user indicate how many of the oldest tests he
    // wants to delete and confirm this action
    private void showDeleteOldestTestsDialogFragment() {
        DeleteOldestStudentResultsDialogFragment deleteOldestStudentResultsDialogFragment =
                DeleteOldestStudentResultsDialogFragment.newInstance(
                        mStudentEntry.getFirstName() + " " + mStudentEntry.getLastName(),
                        mAdapter.getItemCount(), mIsDefaultTest);

        // Sets StudentTestResultsFragment as the target fragment to get results from DeleteOldestStudentResultsDialogFragment
        deleteOldestStudentResultsDialogFragment.setTargetFragment(this, 200);

        deleteOldestStudentResultsDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                getString(R.string.menu_student_test_results_action_delete_oldest_student_results_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete
    // a particular test
    private void showDeleteTestConfirmationDialogFragment(int position) {

        // Indicates that the delete test confirmation dialog fragment is being shown and saves the position
        // of the swiped test to restore its delete background in case of a configuration change
        mIsAskingToDeleteTest = true;
        mTestWithDeleteBackgroundPosition = position;

        // Used to format the date displayed in the alert dialog message
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                (TEST_INFORMATION_ALERT_DIALOG_DATE_FORMAT, Locale.getDefault());
        String message;

        TestEntry testEntry = mAdapter.getTestEntry(position);
        int grade = testEntry.getGrade();
        String gradeString = String.valueOf(grade);
        if (grade == WordUtils.getNumberOfWordsOnList(mStudentEntry.getTestType())) {
            gradeString = "All";
        }

        message = getString(R.string.confirmation_dialog_fragment_delete_test_text,
                mStudentEntry.getFirstName(), mStudentEntry.getLastName(),
                TestTypeUtils.getTestTypeTitle(mFragmentActivity, mStudentEntry.getTestType(), mIsDefaultTest),
                simpleDateFormat.format(testEntry.getDate()), gradeString);

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(message), getString(R.string.confirmation_dialog_fragment_delete_test_title),
                        mIsDefaultTest, position, ConfirmationDialogFragment.STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TEST);

        // Sets StudentTestResultsFragment as the target fragment to get results from ConfirmationDialogFragment
        confirmationDialogFragment.setTargetFragment(this, 300);

        confirmationDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                getString(R.string.confirmation_dialog_fragment_delete_test_tag));
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete
    // the selected student's test results
    private void showDeleteTestsConfirmationDialogFragment() {

        String testWordSuffix = getString(R.string.lowercase_letter_s);
        if (mAdapter.getSelectedTestsCount() == 1) {
            testWordSuffix = EMPTY_STRING;
        }

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(
                                R.string.delete_tests_confirmation_dialog_fragment_text,
                                mAdapter.getSelectedTestsPositions().size(), testWordSuffix,
                                mStudentEntry.getFirstName(), mStudentEntry.getLastName())),
                        getString(R.string.delete_tests_confirmation_dialog_fragment_title),
                        mIsDefaultTest, 0, ConfirmationDialogFragment.STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TESTS);

        // Sets StudentTestResultsFragment as the target fragment to get results from ConfirmationDialogFragment
        confirmationDialogFragment.setTargetFragment(this, 400);

        if (getFragmentManager() != null) {
            confirmationDialogFragment.show(getFragmentManager(), getString(R.string.delete_tests_confirmation_dialog_fragment_tag));
        }
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete
    // all the student's test results
    private void showEditStudentNameDialogFragment() {

        EditStudentNameDialogFragment editStudentNameDialogFragment =
                EditStudentNameDialogFragment.newInstance(
                        getString(R.string.menu_student_test_results_action_edit_dialog_fragment_title),
                        mStudentEntry.getFirstName(), mStudentEntry.getLastName(), mIsDefaultTest);

        // Sets this fragment as the target fragment to get results from editStudentNameDialogFragment
        editStudentNameDialogFragment.setTargetFragment(this, 500);

        editStudentNameDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                getString(R.string.menu_student_test_results_action_edit_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user to select a new test type for the
    // student
    private void showChangeTestDialogFragment() {

        byte caller;
        if (mAdapter.getItemCount() == 0) {
            caller = STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_NO_TESTS;
        } else {
            caller = STUDENT_TEST_RESULTS_FRAGMENT_CHANGE_TEST_FOR_STUDENT_WITH_TESTS;
        }

        ChangeTestDialogFragment changeTestDialogFragment =
                ChangeTestDialogFragment.newInstance(
                        getString(R.string.change_student_test_dialog_fragment_title),
                        mStudentEntry.getTestType(), mIsDefaultTest, caller);

        // Sets StudentTestResultsFragment as the target fragment to get results from changeTestDialogFragment
        changeTestDialogFragment.setTargetFragment(this, 600);

        changeTestDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                getString(R.string.change_student_test_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user share the test results of the student
    private void showShareDialogFragment() {
        DialogFragmentUtils.showShareDialogFragment(
                getString(R.string.share_student_results_dialog_fragment_title),
                getString(R.string.share_student_results_dialog_fragment_tag),
                false, mIsDefaultTest,
                mFragmentActivity.getSupportFragmentManager());
    }


    /**
     * Helper method that deletes the selected tests on Action Mode
     */
    private void deleteTests() {

        Date lastTestDateBeforeDeletion = mAdapter.getTestEntry(mAdapter.getItemCount() - 1).getDate();

        List<Integer> selectedItemPositions = mAdapter.getSelectedTestsPositions();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            final TestEntry testEntry = mAdapter.getTestEntry(selectedItemPositions.get(i));

            // Delete the test from the adapter
            mAdapter.removeTest(selectedItemPositions.get(i));

            // Delete test from the tests table
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mAppDatabase.testDao().deleteTest(testEntry);
                }
            });

        }

        // Updates the student entry in the DB with the last test left after the deletion
        int testResult = 0;
        Date date = null;
        String unknownWords = EMPTY_STRING;
        boolean updateStudentWithLastTestResult;
        int numberOfTests = mAdapter.getItemCount();

        // If there are not test results left
        if (numberOfTests == 0) {
            updateStudentWithLastTestResult = true;
            testResult = STUDENT_WITH_NO_TESTS_GRADE;
            date = new Date();
            unknownWords = EMPTY_STRING;
        } else {
            // If there are tests left, check if the last test was deleted
            Date lastTestDateAfterDeletion = mAdapter.getTestEntry(mAdapter.getItemCount() - 1).getDate();
            if (lastTestDateBeforeDeletion == lastTestDateAfterDeletion) {
                updateStudentWithLastTestResult = false;
            } else {
                updateStudentWithLastTestResult = true;
                TestEntry testEntry = mAdapter.getTestEntry(numberOfTests - 1);
                testResult = testEntry.getGrade();
                date = testEntry.getDate();
                unknownWords = testEntry.getUnknownWords();
            }
        }

        if (updateStudentWithLastTestResult) {

            // Update student in the DB
            final StudentEntry studentEntry = new StudentEntry(mStudentEntry.getFirstName(),
                    mStudentEntry.getLastName(), testResult, mStudentEntry.getTestType(), date, unknownWords);
            studentEntry.setId(mStudentEntry.getId());

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mAppDatabase != null) {
                        mAppDatabase.studentDao().updateStudent(studentEntry);
                    }
                }
            });

            // Updates the member StudentEntry variable used to update the database.
            mStudentEntry.setGrade(testResult);
            mStudentEntry.setLastTestDate(date);
            mStudentEntry.setUnknownWords(unknownWords);
        }


        actionMode.finish();
        Toast.makeText(mFragmentActivity, getString(R.string.fragment_student_test_results_tests_deleted_confirmation_message,
                selectedItemPositions.size()), Toast.LENGTH_SHORT).show();
    }

    // Helper method that deletes all rows for this student from the "tests" table in the database
    // and updates the student row on the "students" table
    private void deleteAllTests() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mAppDatabase.testDao().deleteTestsById(mStudentEntry.getId());
            }
        });
        updateStudentWithNoTests();
        Toast.makeText(mFragmentActivity, getString(R.string.menu_student_test_results_action_delete_all_tests_deleted),
                Toast.LENGTH_SHORT).show();
    }


    /**
     * Helper method that deletes one test
     *
     * @param position The position of the test to delete in the list of tests
     */
    private void deleteTest(int position) {

        List<TestEntry> testEntries = mAdapter.getTestsListData();
        final TestEntry testEntry = testEntries.get(position);
        // Delete a test entry from the test table
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAppDatabase.testDao().deleteTest(testEntry);
                }
            }
        });

        // If the test deleted is the last one in the list, update the student entry with
        // the information of the previous test in the list.
        if (position == testEntries.size() - 1) {

            testEntries.remove(position);

            int testResult;
            Date date;
            String unknownWords;

            // If there are not test results
            if (testEntries.size() == 0) {
                testResult = STUDENT_WITH_NO_TESTS_GRADE;
                date = new Date();
                unknownWords = EMPTY_STRING;
            } else {
                testResult = testEntries.get(testEntries.size() - 1).getGrade();
                date = testEntries.get(testEntries.size() - 1).getDate();
                unknownWords = testEntries.get(testEntries.size() - 1).getUnknownWords();
            }

            final StudentEntry studentEntry = new StudentEntry(mStudentEntry.getFirstName(),
                    mStudentEntry.getLastName(), testResult, mStudentEntry.getTestType(), date, unknownWords);
            studentEntry.setId(mStudentEntry.getId());

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mAppDatabase != null) {
                        mAppDatabase.studentDao().updateStudent(studentEntry);
                    }
                }
            });

            // Updates the member StudentEntry variable used to update the database.
            mStudentEntry.setGrade(testResult);
            mStudentEntry.setLastTestDate(date);
            mStudentEntry.setUnknownWords(unknownWords);
        }

        Toast.makeText(mFragmentActivity, getString(R.string.menu_student_test_results_action_delete_test_deleted),
                Toast.LENGTH_SHORT).show();
    }


    // Helper method that updates the student row on the "students" table with no tests
    private void updateStudentWithNoTests() {
        final StudentEntry studentEntry = new StudentEntry(mStudentEntry.getFirstName(),
                mStudentEntry.getLastName(), STUDENT_WITH_NO_TESTS_GRADE,
                mStudentEntry.getTestType(), new Date(), EMPTY_STRING);

        studentEntry.setId(mStudentEntry.getId());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mAppDatabase != null) {
                    mAppDatabase.studentDao().updateStudent(studentEntry);
                }
            }
        });
    }


    // Helper method that sets the graph visibility and the color of the graph menu icon based on the
    // value of the variable mActionShowGraph
    private void setupGraphVisibility(MenuItem menuItem) {

        if (mActionShowGraph) {
            menuItem.setIcon(R.drawable.ic_line_chart_outline_blue_24dp);
            menuItem.setTitle(Html.fromHtml(
                    getString(R.string.menu_student_test_results_action_hide_graph_title)));
            if (mAdapter.getItemCount() < 2) {
                mXYPlot.setVisibility(View.GONE);
                Toast.makeText(mFragmentActivity,
                        getString(R.string.menu_student_test_results_action_show_hide_graph_will_be_show_message),
                        Toast.LENGTH_SHORT).show();
            } else {
                mXYPlot.setVisibility(View.VISIBLE);
            }
        } else {
            menuItem.setIcon(R.drawable.ic_line_chart_outline_white_24dp);
            menuItem.setTitle(Html.fromHtml(
                    getString(R.string.menu_student_test_results_action_show_graph_title)));
            mXYPlot.setVisibility(View.GONE);
        }
        mActionShowGraph = !mActionShowGraph;
    }


    // Displays an AlertDialog with information about the test
    @Override
    public void onItemClick(TestEntry testEntry, int position) {

        // If there are tests selected (action mode is enabled) set up action mode
        if (mAdapter.getSelectedTestsCount() > 0) {
            setupItemForActionMode(position);
        } else {
            // If there are no tests selected show a message with information about the clicked test
            // Used to format the date displayed in the alert dialog message
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                    (TEST_INFORMATION_ALERT_DIALOG_DATE_FORMAT, Locale.getDefault());
            String htmlMessage;

            int grade = testEntry.getGrade();
            int numberOfWords = WordUtils.getNumberOfWordsOnList(mStudentEntry.getTestType());

            if (grade == numberOfWords) {
                htmlMessage = getString(R.string.test_information_without_incorrect_words_alert_dialog_text,
                        mStudentEntry.getFirstName(), mStudentEntry.getLastName(),
                        TestTypeUtils.getTestTypeTitle(mFragmentActivity, mStudentEntry.getTestType(), mIsDefaultTest),
                        simpleDateFormat.format(testEntry.getDate()));
            } else {
                htmlMessage = getString(R.string.test_information_with_incorrect_words_alert_dialog_text,
                        mStudentEntry.getFirstName(), mStudentEntry.getLastName(),
                        TestTypeUtils.getTestTypeTitle(mFragmentActivity, mStudentEntry.getTestType(), mIsDefaultTest),
                        simpleDateFormat.format(testEntry.getDate()), grade,
                        testEntry.getUnknownWords());
            }

            MessageDialogFragment messageDialogFragment =
                    MessageDialogFragment.newInstance(
                            Html.fromHtml(htmlMessage), getString(R.string.test_information_alert_dialog_title),
                            mIsDefaultTest, STUDENT_TEST_RESULTS_FRAGMENT_TEST_INFO, EMPTY_STRING);

            messageDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                    getString(R.string.fragment_student_test_results_test_info_dialog_fragment_tag));
        }
    }


    @Override
    public void onItemLongClick(int position) {
        setupItemForActionMode(position);
    }


    // Implementation of the interface ShareDialogFragment.ShareDialogFragmentListener
    // It receives the share document type result selected by the user on ShareDialogFragment
    @Override
    public void onShare(int shareDocumentTypeSelected) {

        /* Checks if external storage is available for read and write */
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            ShareResult shareResult = null;
            List<TestEntry> testEntries = mAdapter.getTestsListData();
            int studentTestType = mStudentEntry.getTestType();
            int numberOfWordsOnStudentTest = WordUtils.getNumberOfWordsOnList(studentTestType);
            String fileNamePart1 = mStudentEntry.getFirstName() + mStudentEntry.getLastName() + "_" +
                    TestTypeUtils.getTestTypeString(mFragmentActivity, studentTestType)
                            .replace(" ", EMPTY_STRING)
                            .replace("list", EMPTY_STRING)
                            .replace("Pre-K", "PreK")
                            .replace("grade", "Grade")
                            .replace("nouns", "Nouns") +
                    numberOfWordsOnStudentTest + "Words";
            String studentNameForStudentResults = mStudentEntry.getFirstName() + " " +
                    mStudentEntry.getLastName();

            String testWordSuffix = getString(R.string.lowercase_letter_s);
            if (mAdapter.getItemCount() == 1) {
                testWordSuffix = EMPTY_STRING;
            }
            String testTitleForStudentResults = getString(R.string.fragment_student_test_results_share_pdf_title_second_line,
                    TestTypeUtils.getTestTypeTitle(mFragmentActivity, mStudentEntry.getTestType(), mIsDefaultTest),
                    mAdapter.getItemCount(), testWordSuffix);

            switch (shareDocumentTypeSelected) {
                case PDF_SIMPLE_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareSimplePdfFile(mFragmentActivity, null, testEntries,
                            fileNamePart1, studentNameForStudentResults, testTitleForStudentResults,
                            mStudentEntry.getTestType());
                    break;
                case PDF_DETAILED_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareDetailedPdfFile(mFragmentActivity, null, testEntries,
                            fileNamePart1, studentNameForStudentResults, testTitleForStudentResults,
                            mStudentEntry.getTestType());
                    break;
                case CSV_SIMPLE_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareCsvFile(mFragmentActivity, null, testEntries,
                            false, fileNamePart1, numberOfWordsOnStudentTest);
                    break;
                case CSV_DETAILED_SHARE_DOCUMENT_TYPE:
                    shareResult = ShareUtils.shareCsvFile(mFragmentActivity, null, testEntries,
                            true, fileNamePart1, numberOfWordsOnStudentTest);
                    break;
            }

            if (shareResult != null) {
                switch (shareResult.getCode()) {
                    case SHARE_RESULT_DATA_SHARED:
                        // Adds the file to a queue to be deleted later in the onResume or onCreate methods
                        mFilesToDelete.add(shareResult.getFile());
                        break;
                    case SHARE_RESULT_NO_APP:
                        Snackbar.make(mSnackView, R.string.activity_main_action_share_no_app_error, FOUR_SECONDS).show();
                        break;
                    case SHARE_RESULT_NO_FILE_CREATED:
                        Snackbar.make(mSnackView, R.string.activity_main_action_share_file_creation_error, FOUR_SECONDS).show();
                        break;
                }
            }

        } else {
            Snackbar.make(mSnackView, R.string.activity_main_action_save_storage_error, FOUR_SECONDS).show();
        }
    }


    // Implementation of the interface ConfirmationDialogFragment.ConfirmationDialogFragmentListener
    // It receives a yes or no answer from the user on ConfirmationDialogFragment
    @Override
    public void onConfirmation(boolean answerYes, int itemToDeletePosition, byte dialogFragmentCallerType) {

        switch (dialogFragmentCallerType) {
            case ConfirmationDialogFragment.STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TEST:
                mIsAskingToDeleteTest = false;
                mAdapter.setTestWithDeleteBackgroundData(-1, false);
                if (answerYes) {
                    deleteTest(itemToDeletePosition);
                } else {
                    mAdapter.notifyItemChanged(itemToDeletePosition);
                }
                break;
            case ConfirmationDialogFragment.STUDENT_TEST_RESULTS_FRAGMENT_DELETE_TESTS:
                if (answerYes) {
                    deleteTests();
                }
                break;
            case ConfirmationDialogFragment.STUDENT_TEST_RESULTS_FRAGMENT_DELETE_ALL_TESTS:
                // if the user is deleting all the tests for this student
                if (answerYes) {
                    deleteAllTests();
                }
                break;
        }
    }


    /**
     * Implementation of the interface EditStudentNameDialogFragment.EditStudentNameDialogFragmentListener
     *
     * @param firstName A String that contains the first name entered by the user on EditStudentNameDialogFragment
     * @param lastName  A String that contains the last name entered by the user on EditStudentNameDialogFragment
     */
    @Override
    public void onEditStudentName(final String firstName, final String lastName) {

        // If the changed student name is the same it was before show a message and don't update the StudentEntry
        if (mStudentEntry.getFirstName().equals(firstName) && mStudentEntry.getLastName().equals(lastName)) {
            Toast.makeText(mFragmentActivity, getString(
                    R.string.menu_student_test_results_action_edit_student_not_changed_message),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Create the student entry that will be updated whit the new name
            final StudentEntry studentEntry = new StudentEntry(firstName, lastName, mStudentEntry.getGrade(),
                    mStudentEntry.getTestType(), mStudentEntry.getLastTestDate(), mStudentEntry.getUnknownWords());
            studentEntry.setId(mStudentEntry.getId());

            // Update the StudentEntry in the "students" table of the Database
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mAppDatabase.studentDao().updateStudent(studentEntry);
                }
            });

            // Update the StudentEntry variable that holds the updated student information on this fragment,
            // the toolbar title, and the Widget.
            mStudentEntry.setFirstName(firstName);
            mStudentEntry.setLastName(lastName);
            mCollapsingToolbarLayout.setTitle(mStudentEntry.getFirstName() + " " + mStudentEntry.getLastName());
            WidgetProvider.updateWidgetWithStudentsInformation(mFragmentActivity);
            Toast.makeText(mFragmentActivity,
                    getString(R.string.menu_student_test_results_action_edit_student_changed_message),
                    Toast.LENGTH_SHORT).show();
        }
    }


    // Implementation of the interface ChangeTestDialogFragment.ChangeTestDialogFragmentListener
    // It receives a test type selected by the user on ChangeTestDialogFragment
    @Override
    public void onChangeTest(int testType) {

        // If the changed student test is the same it was before show a message and don't update the StudentEntry
        if (testType == mStudentEntry.getTestType()) {
            Toast.makeText(mFragmentActivity, getString(
                    R.string.change_test_dialog_fragment_student_test_not_changed_message),
                    Toast.LENGTH_SHORT).show();
        } else {

            // Update the StudentEntry variable that holds the updated student information on this fragment
            mStudentEntry.setTestType(testType);

            mIsDefaultTest = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mFragmentActivity)
                    == mStudentEntry.getTestType();

            // If the list of tests is empty change the test title and update the Widget.
            if (mAdapter.getItemCount() == 0) {
                updateStudentWithNoTests();
                showEmptyListMessage(true, mAdapter.getItemCount());
                WidgetProvider.updateWidgetWithStudentsInformation(mFragmentActivity);
            } else {
                // If the test list is not empty delete it and update the student row on the "students" table
                deleteAllTests();
            }

            // Update the test list adapter
            mAdapter.setIsDefaultTest(mIsDefaultTest);
            mAdapter.setTestType(mStudentEntry.getTestType());
            mAdapter.notifyDataSetChanged();

            Toast.makeText(mFragmentActivity,
                    getString(R.string.change_test_dialog_fragment_student_test_changed_message),
                    Toast.LENGTH_SHORT).show();

        }

    }

    // Get the added test information from TestActivity and update the member StudentEntry variable
    // that is used to update the database when editing the student name or changing his test type.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEST_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mStudentEntry.setLastTestDate(DateConverter.timestampToDate(data.getLongExtra(TEST_ADDED_DATE_KEY, 0)));
            mStudentEntry.setGrade(data.getIntExtra(TEST_ADDED_GRADE_KEY, 0));
            mStudentEntry.setUnknownWords(data.getStringExtra(TEST_ADDED_UNKNOWN_WORDS_KEY));

            // If the maximum number of tests was reached after adding this one show a message
            if (mAdapter.getItemCount() == MAX_NUMBER_OF_TESTS_PER_STUDENT - 1) {
                MessageDialogFragment messageDialogFragment =
                        MessageDialogFragment.newInstance(
                                Html.fromHtml(getString(R.string.fragment_student_test_results_max_number_of_tests_reached_text, MAX_NUMBER_OF_TESTS_PER_STUDENT)),
                                getString(R.string.fragment_student_test_results_max_number_of_tests_reached_title),
                                mIsDefaultTest, STUDENT_TEST_RESULT_FRAGMENT_MAX_NUMBER_OF_TESTS_REACHED, EMPTY_STRING);

                // Sets StudentTestResultsFragment as the target fragment to get results from ConfirmationDialogFragment
                messageDialogFragment.setTargetFragment(this, 800);

                messageDialogFragment.show(mFragmentActivity.getSupportFragmentManager(),
                        getString(R.string.fragment_student_test_results_max_number_of_tests_reached_tag));
            } else {
                Toast.makeText(mFragmentActivity, getString(R.string.activity_test_saved_message),
                        Toast.LENGTH_SHORT).show();
            }


        }
    }

    /**
     * Enables ActionMode to show a different menu, toggles the selected value of the test selected
     * and sets the title of the action mode menu to be the number of tests selected
     *
     * @param position Position of the student clicked or long clicked.
     */
    private void setupItemForActionMode(int position) {

        if (actionMode == null) {
            if (mTablet) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    actionMode = activity.startSupportActionMode(actionModeCallback);
                }
            } else {
                StudentTestResultsActivity activity = (StudentTestResultsActivity) getActivity();
                if (activity != null) {
                    actionMode = activity.startSupportActionMode(actionModeCallback);
                }
            }

            if (mAdapter.getSelectedTestsCount() == 0) {
                mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_line_dark));
            }
        }

        mAdapter.toggleTestSelectionState(position);

        int selectedTestsCount = mAdapter.getSelectedTestsCount();

        // If the last selected test was deselected finish action mode
        if (selectedTestsCount == 0) {
            actionMode.finish();
            mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_line_light));
        } else {
            actionMode.setTitle(getString(R.string.action_mode_toolbar_title, selectedTestsCount));
            actionMode.invalidate();
        }
    }

    @Override
    public void onMaxNumberOfTestsReached() {
        Toast.makeText(mFragmentActivity, getString(R.string.activity_test_saved_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteOldestStudentResults(int numberOfTestsToDelete) {

        // Delete numberOfTestsToDelete starting from the oldest one

        for (int position = 0; position < numberOfTestsToDelete; position++) {
            final int finalPosition = position;
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAppDatabase.testDao().deleteTest(mAdapter.getTestEntry(finalPosition));
                    }
                }
            });
        }

        Toast.makeText(mFragmentActivity, getString(R.string.fragment_student_test_results_tests_deleted_confirmation_message,
                numberOfTestsToDelete), Toast.LENGTH_SHORT).show();

    }


    /**
     * Class that implement ActionMode callbacks to change the menu when tests are selected by a
     * long click and handle the deletion of those selected tests.
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
                showDeleteTestsConfirmationDialogFragment();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelectedTests();
            actionMode = null;
            mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_line_light));
        }
    }


    // On rotation saves the action show graph flag and the StudentEntry variable that holds the most
    // recent information of the student
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTION_SHOW_GRAPH_FLAG_KEY, mActionShowGraph);
        outState.putParcelable(STUDENT_ENTRY_KEY, mStudentEntry);

        // If there are tests selected
        if (mAdapter.getSelectedTestsCount() > 0) {
            outState.putParcelable(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY, mAdapter.getSelectedTests());
        }

        // If the a dialog fragment asking for confirmation to delete a test is displayed
        if (mIsAskingToDeleteTest) {
            outState.putBoolean(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY, mRightSwipe);
            outState.putInt(SAVED_INSTANCE_STATE_TEST_WITH_DELETE_BACKGROUND_POSITION_KEY, mTestWithDeleteBackgroundPosition);
        }
    }

}

