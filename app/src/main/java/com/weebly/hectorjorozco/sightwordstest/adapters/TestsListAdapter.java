package com.weebly.hectorjorozco.sightwordstest.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.TestEntry;
import com.weebly.hectorjorozco.sightwordstest.models.SparseBooleanArrayParcelable;
import com.weebly.hectorjorozco.sightwordstest.ui.MainActivity;
import com.weebly.hectorjorozco.sightwordstest.utils.Utils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This Adapter creates and binds ViewHolders, that hold the date and grade of a test, to a
 * RecyclerView to efficiently display data.
 */
public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.TestViewHolder> {

    // Format used to display the date in the student list
    private static final String DATE_FORMAT = "MM/dd/yyy hh:mm aaa";
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    final private ItemClickListener mItemClickListener;
    private List<TestEntry> mTestEntries;
    private final Context mContext;
    private boolean mIsDefaultTest;
    private int mTestType;

    // Array that saves the selected state of tests (true if selected, false otherwise)
    private SparseBooleanArrayParcelable mSelectedTests;

    private int mTestWithDeleteBackgroundPosition = -1;
    private boolean mTestWithDeleteBackgroundRightSwiped = false;

    // Interface implemented in StudentTestResultsFragment.java to handle clicks and long clicks on a test item
    public interface ItemClickListener {
        void onItemClick(TestEntry testEntry, int position);

        void onItemLongClick(int position);
    }

    // The adapter constructor
    public TestsListAdapter(Context context, ItemClickListener itemClickListener, boolean isDefaultTest, int testType) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mIsDefaultTest = isDefaultTest;
        mTestType = testType;
        mSelectedTests = new SparseBooleanArrayParcelable();
    }


    // Called when ViewHolders are created to fill the RecyclerView.
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.test_list_item, parent, false);

        return new TestViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {

        TestEntry testEntry = mTestEntries.get(position);

        String date = mSimpleDateFormat.format(testEntry.getDate());
        int grade = testEntry.getGrade();

        int studentPassedColor;
        int dateAndGradeTextColor;
        if (mIsDefaultTest) {
            studentPassedColor = mContext.getResources().getColor(R.color.colorPrimaryUltraLight);
            dateAndGradeTextColor = (mContext.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            studentPassedColor = mContext.getResources().getColor(R.color.colorSecondaryUltraLight);
            dateAndGradeTextColor = (mContext.getResources().getColor(R.color.colorSecondaryDark));
        }

        holder.dateTextView.setTextColor(dateAndGradeTextColor);
        holder.gradeTextView.setTextColor(dateAndGradeTextColor);
        holder.dateTextView.setText(date);
        holder.gradeTextView.setText(String.valueOf(grade));

        // If the student has been selected
        if (mSelectedTests.get(position, false)) {
            holder.viewForeground.setBackgroundColor(mContext.getResources().getColor(R.color.colorRowActivated));
        } else {
            // If the student knows all words on this test and there are no tests selected
            if (getSelectedTestsCount() ==0 && grade == WordUtils.getNumberOfWordsOnList(mTestType)) {
                holder.viewForeground.setBackgroundColor(studentPassedColor);
            } else {
                holder.viewForeground.setBackgroundColor(mContext.getResources().getColor(R.color.appBackground));
            }
        }

        // If the test was swiped to delete, the app is asking for confirmation and the device's config changed then:
        if (mTestWithDeleteBackgroundPosition == position) {
            holder.viewForeground.setVisibility(View.INVISIBLE);
            if (mTestWithDeleteBackgroundRightSwiped) {
                holder.viewRightSwipeBackground.setVisibility(View.VISIBLE);
                holder.viewLeftSwipeBackground.setVisibility(View.INVISIBLE);
            } else {
                holder.viewRightSwipeBackground.setVisibility(View.INVISIBLE);
                holder.viewLeftSwipeBackground.setVisibility(View.VISIBLE);
            }
        } else {
            holder.viewForeground.setVisibility(View.VISIBLE);
        }

    }


    // Returns the number of items to display.
    @Override
    public int getItemCount() {
        if (mTestEntries == null) {
            return 0;
        }
        return mTestEntries.size();
    }


    // Returns the list of test Entries.
    public List<TestEntry> getTestsListData() {
        return mTestEntries;
    }


    public TestEntry getTestEntry(int position){
        return mTestEntries.get(position);
    }

    public void removeTest(int position) {
        mTestEntries.remove(position);
        notifyItemRemoved(position);
    }

    // When data changes, this method updates the list of Test Entries
    // and notifies the adapter to use the new values on it
    public void setTestsListData(List<TestEntry> testEntries) {
        mTestEntries = testEntries;
        notifyDataSetChanged();
    }


    public void setTestType(int testType) {
        mTestType = testType;
    }


    public void setIsDefaultTest(boolean isDefaultTest) {
        mIsDefaultTest = isDefaultTest;
    }


    public void toggleTestSelectionState(int position) {
        if (mSelectedTests.get(position, false)) {
            mSelectedTests.delete(position);
        } else {
            mSelectedTests.put(position, true);
        }
        // If this is the first test selected update all tests to disable any backgrounds
        if (getSelectedTestsCount() == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemChanged(position);
        }
    }

    public void clearSelectedTests() {
        mSelectedTests.clear();
        notifyDataSetChanged();
    }

    public int getSelectedTestsCount() {
        return mSelectedTests.size();
    }

    public List<Integer> getSelectedTestsPositions() {
        List<Integer> selectedTests = new ArrayList<>(mSelectedTests.size());
        for (int i = 0; i < mSelectedTests.size(); i++) {
            selectedTests.add(mSelectedTests.keyAt(i));
        }
        return selectedTests;
    }

    public void setSelectedTests(SparseBooleanArrayParcelable selectedTests) {
        mSelectedTests = selectedTests;
    }

    public SparseBooleanArrayParcelable getSelectedTests() {
        return mSelectedTests;
    }

    public void setTestWithDeleteBackgroundData(int position, boolean rightSwiped) {
        mTestWithDeleteBackgroundPosition = position;
        mTestWithDeleteBackgroundRightSwiped = rightSwiped;
    }

    // Inner class for creating ViewHolders
    public class TestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final LinearLayout viewForeground;
        final TextView dateTextView;
        final TextView gradeTextView;
        public final RelativeLayout viewLeftSwipeBackground;
        public final RelativeLayout viewRightSwipeBackground;

        TestViewHolder(View view) {
            super(view);

            dateTextView = view.findViewById(R.id.test_list_item_date_text_view);
            gradeTextView = view.findViewById(R.id.test_list_item_grade_text_view);
            viewLeftSwipeBackground = view.findViewById(R.id.test_list_item_left_swipe_background);
            viewRightSwipeBackground = view.findViewById(R.id.test_list_item_right_swipe_background);
            viewForeground = view.findViewById(R.id.test_list_item_foreground);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // If there are no tests selected pass the clicked test and position 0 (position parameter is not needed)
            if (mSelectedTests.size() == 0) {
                mItemClickListener.onItemClick(mTestEntries.get(getAdapterPosition()), 0);
            } else {
                // If there are tests selected then action mode is enabled, pass the test position to
                // select it (pass null for testEntry because it is not needed)
                mItemClickListener.onItemClick(null, getAdapterPosition());
            }


        }

        @Override
        public boolean onLongClick(View v) {
            mItemClickListener.onItemLongClick(getAdapterPosition());
            Utils.vibrate(mContext, MainActivity.LONG_PRESS_VIBRATION_TIME_IN_MILLISECONDS);
            return true;
        }
    }
}