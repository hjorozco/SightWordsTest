package com.weebly.hectorjorozco.sightwordstest.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.adapters.WordsListAdapter;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.database.TestEntry;
import com.weebly.hectorjorozco.sightwordstest.models.Word;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.ConfirmationDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.sightwordstest.utils.DateConverter;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.TEST_ACTIVITY;

public class TestActivity extends AppCompatActivity implements ConfirmationDialogFragment.ConfirmationDialogFragmentListener {

    public static final String STUDENT_INFO_TO_PASS_KEY = "student_info_to_pass_key";
    private static final String WORD_LIST_KEY = "word_list_key";

    public static final String TEST_ADDED_DATE_KEY = "test_added_date_key";
    public static final String TEST_ADDED_GRADE_KEY = "test_added_grade_key";
    public static final String TEST_ADDED_UNKNOWN_WORDS_KEY = "test_added_unknown_words_key";

    private static final String ACTION_MARK_WORDS_FLAG_KEY = "action_mark_words_flag_key";

    private static final int TABLET_GRID_NUMBER_OF_COLUMNS = 4;
    private static final int PHONE_GRID_NUMBER_OF_COLUMNS = 2;

    private static final String UNKNOWN_WORDS_SEPARATOR = ", ";
    private static final String EMPTY_STRING = "";
    private static final char COMMA_CHAR = ',';

    private WordsListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private StudentEntry mStudentEntry;

    private boolean mIsDefaultTest;
    private boolean mActionMarkWords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        List<Word> words;

        // Gets the student info passed to this activity.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(STUDENT_INFO_TO_PASS_KEY)) {
            mStudentEntry = bundle.getParcelable(STUDENT_INFO_TO_PASS_KEY);
        }

        // If this is the first time the activity is created load the list of words from util

        if (savedInstanceState == null) {
            int testType = -1;
            if (mStudentEntry != null) {
                testType = mStudentEntry.getTestType();
            }
            words = WordUtils.getListOfWords(testType);

            mActionMarkWords = true;
        } else {
            // After rotation get the saved list of words
            words = savedInstanceState.getParcelableArrayList(WORD_LIST_KEY);

            mActionMarkWords = savedInstanceState.getBoolean(ACTION_MARK_WORDS_FLAG_KEY);
        }

        if (mStudentEntry != null) {
            mIsDefaultTest = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(this)
                    == mStudentEntry.getTestType();
        }

        mRecyclerView = findViewById(R.id.activity_test_recycler_view);
        TextView noTestTextView = findViewById(R.id.activity_test_no_test_text_view);

        // If the list of words is empty
        if (words == null) {
            mRecyclerView.setVisibility(View.GONE);
            noTestTextView.setVisibility(View.VISIBLE);
        } else {
            // If the list of words is not empty
            mRecyclerView.setVisibility(View.VISIBLE);
            noTestTextView.setVisibility(View.GONE);

            // Set up adapter
            mAdapter = new WordsListAdapter(this, mIsDefaultTest);
            mAdapter.setWordsListData(words);

            setUpRecyclerView();
        }

        setUpFab();

        setUpToolbar();
    }


    // Set up RecyclerView
    private void setUpRecyclerView() {
        int gridNumberOfColumns;
        if (getResources().getBoolean(R.bool.tablet)) {
            gridNumberOfColumns = TABLET_GRID_NUMBER_OF_COLUMNS;
        } else {
            gridNumberOfColumns = PHONE_GRID_NUMBER_OF_COLUMNS;
        }
        mRecyclerView.addItemDecoration(new ItemOffsetDecoration(this));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), gridNumberOfColumns));
        mRecyclerView.setAdapter(mAdapter);
    }

    // Set up Floating Action Button to add the test result to the database when clicked.
    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.activity_test_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTestResultToDatabase();
            }
        });
        TooltipCompat.setTooltipText(fab, getString(R.string.activity_test_fab_tooltip));
    }

    private void setUpToolbar() {
        Toolbar testActivityToolbar = findViewById(R.id.activity_test_tool_bar);
        testActivityToolbar.setTitle(TestTypeUtils.getTestTypeString(this,
                mStudentEntry.getTestType()));
        setSupportActionBar(testActivityToolbar);
        testActivityToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        testActivityToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void saveTestResultToDatabase() {

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.add_test_alert_dialog_text,
                                String.valueOf(calculateTestResult()),
                                String.valueOf(WordUtils.getNumberOfWordsOnList(mStudentEntry.getTestType())))),
                        getString(R.string.student_complete_name, mStudentEntry.getFirstName(), mStudentEntry.getLastName()),
                        mIsDefaultTest, 0,
                        ConfirmationDialogFragment.TEST_ACTIVITY_SAVE_TEST);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.add_test_alert_dialog_tag));
    }


    // Calculates the test result from the student
    private int calculateTestResult() {
        List<Word> words = mAdapter.getWordsListData();
        int testResult = words.size();

        for (Word word : words) {
            if (word.getPressed()) testResult--;
        }

        return testResult;
    }

    // Gets the words the student did not recognize
    private String getUnknownWordsString() {

        StringBuilder stringBuilder = new StringBuilder();
        List<Word> words = mAdapter.getWordsListData();

        for (Word word : words) {
            if (word.getPressed())
                stringBuilder.append(word.getWord()).append(UNKNOWN_WORDS_SEPARATOR);
        }

        String unknownWords = stringBuilder.toString();
        unknownWords = unknownWords.trim();

        if (!unknownWords.isEmpty()) {
            if (unknownWords.length() == 1) {
                unknownWords = EMPTY_STRING;
            } else if (unknownWords.charAt(unknownWords.length() - 1) == COMMA_CHAR) {
                unknownWords = unknownWords.substring(0, unknownWords.length() - 1);
            }
        }

        return unknownWords;
    }


    // Implementation of the interface ConfirmationDialogFragment.ConfirmationDialogFragmentListener
    // It receives a yes or no answer from the user on ConfirmationDialogFragment
    @Override
    public void onConfirmation(boolean answerYes, int studentToDeletePosition, byte dialogFragmentCallerType) {
        if (answerYes) {

            int testResult = calculateTestResult();
            String unknownWords = getUnknownWordsString();

            final TestEntry testEntry = new TestEntry(mStudentEntry.getId(), new Date(), testResult, unknownWords);
            final StudentEntry studentEntry = new StudentEntry(mStudentEntry.getFirstName(),
                    mStudentEntry.getLastName(), testResult, mStudentEntry.getTestType(), new Date(), unknownWords);
            studentEntry.setId(mStudentEntry.getId());

            // Save the test result to the test table using an ASYNCTASK instead of an EXECUTOR
            new InsertTestAsyncTask(testEntry).execute(getApplicationContext());

            // Update the student entry with the test result sing an ASYNCTASK instead of an EXECUTOR
            new UpdateStudentAsyncTask(studentEntry).execute(getApplicationContext());

            // Sends the added test information back to the calling fragment (StudentTestResultsFragment)
            Intent resultIntent = new Intent();
            resultIntent.putExtra(TEST_ADDED_DATE_KEY, DateConverter.dateToTimestamp(testEntry.getDate()));
            resultIntent.putExtra(TEST_ADDED_GRADE_KEY, testEntry.getGrade());
            resultIntent.putExtra(TEST_ADDED_UNKNOWN_WORDS_KEY, testEntry.getUnknownWords());
            setResult(Activity.RESULT_OK, resultIntent);

            // Finish this activity (TestActivity.java)
            finish();

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Sets up a separation between elements of a RecyclerView
     */
    static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private final int mItemOffset;

        private ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        ItemOffsetDecoration(@NonNull Context context) {
            this(context.getResources().getDimensionPixelSize(R.dimen.recycler_view_words_separation));
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }

    }


    // Saves the list of word objects on rotation to preserve their pressed state.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        List<Word> words = mAdapter.getWordsListData();
        outState.putParcelableArrayList(WORD_LIST_KEY, new ArrayList<>(words));

        outState.putBoolean(ACTION_MARK_WORDS_FLAG_KEY, mActionMarkWords);
    }


    /**
     * ASYNCTASK that inserts a test entry in the test table of the DB.
     */
    private static class InsertTestAsyncTask extends AsyncTask<Context, Void, Void> {

        final TestEntry mTestEntry;

        InsertTestAsyncTask(TestEntry testEntry) {
            mTestEntry = testEntry;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            AppDatabase appDatabase = AppDatabase.getInstance(contexts[0]);
            if (appDatabase != null) {
                appDatabase.testDao().insertTest(mTestEntry);
            }
            return null;
        }
    }


    /**
     * ASYNCTASK that updates a student entry with the test result
     */
    private static class UpdateStudentAsyncTask extends AsyncTask<Context, Void, Void> {

        private final StudentEntry mStudentEntry;

        UpdateStudentAsyncTask(StudentEntry studentEntry) {
            mStudentEntry = studentEntry;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            AppDatabase appDatabase = AppDatabase.getInstance(contexts[0]);
            if (appDatabase != null) {
                appDatabase.studentDao().updateStudent(mStudentEntry);
            }
            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        String disabledItemColorHexValue = Integer.toHexString(getResources().getColor(R.color.colorDisabledMenuItem) & 0x00ffffff);
        MenuItem markUnMarkWordsMenuItem = menu.findItem(R.id.menu_test_action_mark_unmark_words);

        // If the student does not have any unknown word on its last test disable the mark words menu item.
        if (mStudentEntry.getUnknownWords().isEmpty()) {
            markUnMarkWordsMenuItem.setEnabled(false);
            markUnMarkWordsMenuItem.setIcon(R.drawable.ic_grid_grey_24dp);
            markUnMarkWordsMenuItem.setTitle(Html.fromHtml(getString(R.string.menu_title_with_color,
                    disabledItemColorHexValue, getString(R.string.menu_test_action_mark_words_title))));
        } else {
            // If next action for graph menu item is to show the graph then set its icon white and text blue,
            // icon blue and text clear blue otherwise
            markUnMarkWordsMenuItem.setEnabled(true);
            if (mActionMarkWords) {
                markUnMarkWordsMenuItem.setIcon(R.drawable.ic_grid_white_24dp);
                markUnMarkWordsMenuItem.setTitle(Html.fromHtml(getString(R.string.menu_test_action_mark_words_title)));
            } else {
                markUnMarkWordsMenuItem.setIcon(R.drawable.ic_grid_blue_24dp);
                markUnMarkWordsMenuItem.setTitle(Html.fromHtml(getString(R.string.menu_test_action_unmark_words_title)));
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_test_action_mark_unmark_words:
                markUnmarkWordsNotReadCorrectlyOnLastTest(item);
                break;

            case R.id.menu_test_action_info:
                MessageDialogFragment infoMessageDialogFragment =
                        MessageDialogFragment.newInstance(
                                Html.fromHtml(getString(R.string.test_info_dialog_fragment_text,
                                        mStudentEntry.getFirstName(), mStudentEntry.getLastName(),
                                        calculateTestResult(), WordUtils.getNumberOfWordsOnList(mStudentEntry.getTestType()))),
                                getString(R.string.test_info_dialog_fragment_title), mIsDefaultTest, TEST_ACTIVITY, EMPTY_STRING);

                infoMessageDialogFragment.show(getSupportFragmentManager(),
                        getString(R.string.test_info_dialog_fragment_tag));
                break;

            case R.id.menu_test_action_help:
                MessageDialogFragment helpMessageDialogFragment =
                        MessageDialogFragment.newInstance(
                                Html.fromHtml(getString(R.string.test_help_dialog_fragment_text)),
                                getString(R.string.test_help_dialog_fragment_title), mIsDefaultTest, TEST_ACTIVITY, EMPTY_STRING);

                helpMessageDialogFragment.show(getSupportFragmentManager(),
                        getString(R.string.test_help_dialog_fragment_tag));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Helper method that changes the mark-unmark words menu item icon depending on its pressed state and
     * marks or unmarks the unknown words.
     * @param menuItem the menu item that will be updated according to its pressed state.
     */
    private void markUnmarkWordsNotReadCorrectlyOnLastTest(MenuItem menuItem) {
        if (mActionMarkWords) {
            menuItem.setIcon(R.drawable.ic_grid_blue_24dp);
            menuItem.setTitle(Html.fromHtml(
                    getString(R.string.menu_test_action_unmark_words_title)));
            markUnmarkWords(true);
        } else {
            menuItem.setIcon(R.drawable.ic_grid_white_24dp);
            menuItem.setTitle(Html.fromHtml(
                    getString(R.string.menu_test_action_mark_words_title)));
            markUnmarkWords(false);
        }
        mActionMarkWords = !mActionMarkWords;
    }


    /**
     * Helper method that marks or unmarks the unknown words
     * @param markWords true if the unknown words have to be marked, false otherwise.
     */
    private void markUnmarkWords(boolean markWords) {

        List<Word> words = mAdapter.getWordsListData();
        List<String> unknownWords = getUnknownWordsList();

        for (Word word: words) {
            if (unknownWords.contains(word.getWord())) {
                word.setPressed(markWords);
            }
        }

        mAdapter.setWordsListData(words);
        mAdapter.notifyDataSetChanged();

    }


    // Helper method that gets the unknown words from the student's last test and put them in a List of Strings
    private List<String> getUnknownWordsList(){

        String unknownWordsString = mStudentEntry.getUnknownWords();
        char letter;
        List<String> unknownWordsList = new ArrayList<>();
        StringBuilder unknownWord = new StringBuilder();

        for (int i = 0; i < unknownWordsString.length(); i++) {
            letter = unknownWordsString.charAt(i);
            if (letter == ',') {
                unknownWordsList.add(unknownWord.toString());
                unknownWord.setLength(0);
                i++;
            } else {
                unknownWord.append(letter);
                if (i == unknownWordsString.length()-1) {
                    unknownWordsList.add(unknownWord.toString());
                }
            }
        }

        return unknownWordsList;
    }

}
