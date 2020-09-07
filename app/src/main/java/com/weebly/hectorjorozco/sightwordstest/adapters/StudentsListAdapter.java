package com.weebly.hectorjorozco.sightwordstest.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.models.SparseBooleanArrayParcelable;
import com.weebly.hectorjorozco.sightwordstest.ui.MainActivity;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.Utils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_WITH_NO_TESTS_GRADE;

/**
 * This Adapter creates and binds ViewHolders, that hold the complete name,
 * date of last test, and grade of a student, to a RecyclerView to efficiently display data.
 */
public class StudentsListAdapter extends RecyclerView.Adapter<StudentsListAdapter.StudentViewHolder> {

    // Format used to display the date in the student list
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    final private StudentsListAdapterListener mStudentsListAdapterListener;
    private List<StudentEntry> mStudentEntries;
    private final Context mContext;
    private int mDefaultTestType;

    // Array that saves the selected state of students (true if selected, false otherwise)
    private SparseBooleanArrayParcelable mSelectedStudents;

    private int mStudentWithDeleteBackgroundPosition = -1;
    private boolean mStudentWithDeleteBackgroundRightSwiped = false;

    // Interface implemented in MainActivity.java to handle clicks and long clicks
    public interface StudentsListAdapterListener {
        void onNameDateClick(StudentEntry studentEntry, int position);

        void onGradeClick(StudentEntry studentEntry, int position);

        void onRowLongClick(int position);
    }

    // The adapter constructor
    public StudentsListAdapter(Context context, StudentsListAdapterListener studentsListAdapterListener) {
        mContext = context;
        mStudentsListAdapterListener = studentsListAdapterListener;
        mSelectedStudents = new SparseBooleanArrayParcelable();
    }


    // Called when ViewHolders are created to fill the RecyclerView.
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.student_list_item, parent, false);

        return new StudentViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {

        StudentEntry studentEntry = mStudentEntries.get(position);
        String firstName = studentEntry.getFirstName();
        String lastName = studentEntry.getLastName();

        int grade = studentEntry.getGrade();
        int testType = studentEntry.getTestType();
        String testTypeString = TestTypeUtils.getTestTypeString(mContext, testType);
        int numberOfWords = WordUtils.getNumberOfWordsOnList(testType);
        String lastTestDateFullString;
        String lastDateString;

        if (grade == STUDENT_WITH_NO_TESTS_GRADE) {
            lastDateString = mContext.getString(R.string.student_with_no_tests_text);
            holder.gradeTextView.setVisibility(View.GONE);

        } else {
            lastDateString = mSimpleDateFormat.format(studentEntry.getLastTestDate());
            holder.gradeTextView.setVisibility(View.VISIBLE);
        }

        int studentPassedColor;

        // If the student is being tested in the default test type
        if (testType == mDefaultTestType) {
            holder.studentNameTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.lastTestDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
            holder.gradeTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            lastTestDateFullString = lastDateString;
            studentPassedColor = mContext.getResources().getColor(R.color.colorPrimaryUltraLight);
        } else {
            holder.studentNameTextView.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryDark));
            holder.lastTestDateTextView.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryLight));
            holder.gradeTextView.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryDark));
            lastTestDateFullString = mContext.getString(R.string.test_type_and_last_date_for_no_default_test,
                    testTypeString, numberOfWords, lastDateString);
            studentPassedColor = mContext.getResources().getColor(R.color.colorSecondaryUltraLight);
        }

        holder.studentNameTextView.setText(mContext.getString(R.string.student_complete_name, firstName, lastName));
        holder.lastTestDateTextView.setText(lastTestDateFullString);
        holder.gradeTextView.setText(String.valueOf(grade));

        // If the student has been selected
        if (mSelectedStudents.get(position, false)) {
            holder.viewForeground.setBackgroundColor(mContext.getResources().getColor(R.color.colorRowActivated));
        } else {
            // If the student knows all the words and there are no students selected
            if (getSelectedStudentsCount() == 0 && grade == WordUtils.getNumberOfWordsOnList(testType)) {
                holder.viewForeground.setBackgroundColor(studentPassedColor);
            } else {
                holder.viewForeground.setBackgroundColor(mContext.getResources().getColor(R.color.appBackground));
            }
        }

        // If the student was swiped to delete, the app is asking for confirmation and the device's config changed then:
        if (mStudentWithDeleteBackgroundPosition == position) {
            holder.viewForeground.setVisibility(View.INVISIBLE);
            if (mStudentWithDeleteBackgroundRightSwiped) {
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
        if (mStudentEntries == null) {
            return 0;
        }
        return mStudentEntries.size();
    }


    // Returns the list of Student Entries.
    public List<StudentEntry> getStudentsListData() {
        return mStudentEntries;
    }


    // When data changes, this method updates the list of Student Entries
    // and notifies the adapter to use the new values on it
    public void setStudentsListData(List<StudentEntry> studentEntries) {
        mStudentEntries = studentEntries;
        notifyDataSetChanged();
    }


    public StudentEntry getStudentEntry(int position) {
        return mStudentEntries.get(position);
    }

    public void removeStudent(int position) {
        mStudentEntries.remove(position);
        notifyItemRemoved(position);
    }

    // Sets the default test type used to display items in the RecyclerView
    public void setDefaultTestType() {
        mDefaultTestType = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mContext);
        if (mStudentEntries != null && !mStudentEntries.isEmpty()) {
            notifyDataSetChanged();
        }
    }


    public void toggleStudentSelectionState(int position) {
        if (mSelectedStudents.get(position, false)) {
            mSelectedStudents.delete(position);
        } else {
            mSelectedStudents.put(position, true);
        }
        // If this is the first student selected update all students to disable any backgrounds
        if (getSelectedStudentsCount() == 1) {
            notifyDataSetChanged();
        } else {
            notifyItemChanged(position);
        }
    }

    public void clearSelectedStudents() {
        mSelectedStudents.clear();
        notifyDataSetChanged();
    }

    public int getSelectedStudentsCount() {
        return mSelectedStudents.size();
    }

    public List<Integer> getSelectedStudentsPositions() {
        List<Integer> selectedStudents = new ArrayList<>(mSelectedStudents.size());
        for (int i = 0; i < mSelectedStudents.size(); i++) {
            selectedStudents.add(mSelectedStudents.keyAt(i));
        }
        return selectedStudents;
    }

    public void setSelectedStudents(SparseBooleanArrayParcelable selectedStudents) {
        mSelectedStudents = selectedStudents;
    }

    public SparseBooleanArrayParcelable getSelectedStudents() {
        return mSelectedStudents;
    }

    public void setStudentWithDeleteBackgroundData(int position, boolean rightSwiped) {
        mStudentWithDeleteBackgroundPosition = position;
        mStudentWithDeleteBackgroundRightSwiped = rightSwiped;
    }

    /**
     * Inner class for creating ViewHolders
     */
    public class StudentViewHolder extends RecyclerView.ViewHolder {

        final TextView studentNameTextView;
        final TextView lastTestDateTextView;
        final LinearLayout studentListItemLinearLayout;
        final TextView gradeTextView;
        public final RelativeLayout viewLeftSwipeBackground;
        public final RelativeLayout viewRightSwipeBackground;
        public final ConstraintLayout viewForeground;

        StudentViewHolder(View view) {
            super(view);
            studentListItemLinearLayout = view.findViewById(R.id.student_list_item_linear_layout);
            studentNameTextView = view.findViewById(R.id.student_name_text_view);
            lastTestDateTextView = view.findViewById(R.id.last_test_date_text_view);
            gradeTextView = view.findViewById(R.id.grade_text_view);
            viewLeftSwipeBackground = view.findViewById(R.id.student_list_item_left_swipe_background);
            viewRightSwipeBackground = view.findViewById(R.id.student_list_item_right_swipe_background);
            viewForeground = view.findViewById(R.id.student_list_item_foreground);

            studentListItemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If there are no students selected pass the student and position 0 (not needed)
                    if (mSelectedStudents.size() == 0) {
                        mStudentsListAdapterListener.onNameDateClick(mStudentEntries.get(getAdapterPosition()), 0);
                    } else {
                        // If there are students selected then action mode is enabled, pass the student position to
                        // select it (pass null for studentEntry because it is not needed)
                        mStudentsListAdapterListener.onNameDateClick(null, getAdapterPosition());
                    }
                }
            });

            gradeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If there are no students selected pass the student and position 0 (position parameter is not needed)
                    if (mSelectedStudents.size() == 0) {
                        mStudentsListAdapterListener.onGradeClick(mStudentEntries.get(getAdapterPosition()), 0);
                    } else {
                        // If there are students selected then action mode is enabled, pass the student position to
                        // select it (pass null for studentEntry because it is not needed as a parameter)
                        mStudentsListAdapterListener.onGradeClick(null, getAdapterPosition());
                    }
                }
            });

            studentListItemLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mStudentsListAdapterListener.onRowLongClick(getAdapterPosition());
                    Utils.vibrate(mContext, MainActivity.LONG_PRESS_VIBRATION_TIME_IN_MILLISECONDS);
                    return true;
                }
            });

            gradeTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mStudentsListAdapterListener.onRowLongClick(getAdapterPosition());
                    Utils.vibrate(mContext, MainActivity.LONG_PRESS_VIBRATION_TIME_IN_MILLISECONDS);
                    return true;
                }
            });

        }

    }


}