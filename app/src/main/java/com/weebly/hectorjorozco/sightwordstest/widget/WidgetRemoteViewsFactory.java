package com.weebly.hectorjorozco.sightwordstest.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.database.AppDatabase;
import com.weebly.hectorjorozco.sightwordstest.database.StudentEntry;
import com.weebly.hectorjorozco.sightwordstest.utils.StudentsOrderUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.WordUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.STUDENT_ENTRY_KEY;


/**
 * Factory that populates the list of student entries on the widget
 */
class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    // Format used to display the date in the student list
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final int STUDENT_WITH_NO_TESTS_GRADE = -1;

    private final Context mContext;
    private List<StudentEntry> mStudentEntries;
    private int mDefaultTestType;


    WidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }


    /**
     * Populates one student entry of the widget ListView
     *
     * @param position Position of the student entry on the list
     * @return a RemoteViews that contains an student entry information
     */
    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews widgetListItemRemoteViews;

        widgetListItemRemoteViews =
                new RemoteViews(mContext.getPackageName(), R.layout.widget_list_student_item);

        StudentEntry studentEntry = mStudentEntries.get(position);

        int grade = studentEntry.getGrade();
        int testType = studentEntry.getTestType();
        String testTypeString = TestTypeUtils.getTestTypeString(mContext, testType);
        int numberOfWords = WordUtils.getNumberOfWordsOnList(testType);
        String lastTestDateFullString;
        String lastDateString;

        if (grade == STUDENT_WITH_NO_TESTS_GRADE) {
            lastDateString = mContext.getString(R.string.student_with_no_tests_text);
            widgetListItemRemoteViews.setViewVisibility(R.id.widget_grade_text_view, View.INVISIBLE);
        } else {
            lastDateString = mSimpleDateFormat.format(studentEntry.getLastTestDate());
            widgetListItemRemoteViews.setViewVisibility(R.id.widget_grade_text_view, View.VISIBLE);
        }

        int studentPassedColor;

        if (testType == mDefaultTestType) {
            widgetListItemRemoteViews.setTextColor(R.id.widget_student_name_text_view,
                    mContext.getResources().getColor(R.color.colorPrimaryDark));
            widgetListItemRemoteViews.setTextColor(R.id.widget_last_test_date_text_view,
                    mContext.getResources().getColor(R.color.colorPrimaryLight));
            widgetListItemRemoteViews.setTextColor(R.id.widget_grade_text_view,
                    mContext.getResources().getColor(R.color.colorPrimaryDark));
            lastTestDateFullString = lastDateString;
            studentPassedColor = mContext.getResources().getColor(R.color.colorPrimaryUltraLight);
        } else {
            widgetListItemRemoteViews.setTextColor(R.id.widget_student_name_text_view,
                    mContext.getResources().getColor(R.color.colorSecondaryDark));
            widgetListItemRemoteViews.setTextColor(R.id.widget_last_test_date_text_view,
                    mContext.getResources().getColor(R.color.colorSecondaryLight));
            widgetListItemRemoteViews.setTextColor(R.id.widget_grade_text_view,
                    mContext.getResources().getColor(R.color.colorSecondaryDark));
            lastTestDateFullString = mContext.getString(R.string.test_type_and_last_date_for_no_default_test,
                    testTypeString, numberOfWords, lastDateString);
            studentPassedColor = mContext.getResources().getColor(R.color.colorSecondaryUltraLight);
        }

        if (grade == WordUtils.getNumberOfWordsOnList(testType)) {
            widgetListItemRemoteViews.setInt(R.id.widget_list_student_item, "setBackgroundColor",
                    studentPassedColor);
        } else {
            widgetListItemRemoteViews.setInt(R.id.widget_list_student_item, "setBackgroundColor",
                    Color.WHITE);
        }

        widgetListItemRemoteViews.setTextViewText(R.id.widget_student_name_text_view,
                studentEntry.getFirstName() + " " + studentEntry.getLastName());
        widgetListItemRemoteViews.setTextViewText(R.id.widget_last_test_date_text_view,
                lastTestDateFullString);
        widgetListItemRemoteViews.setTextViewText(R.id.widget_grade_text_view, String.valueOf(grade));

        // Sets filling intent to open the application's StudentTestResultsActivity (on a phone) or show the
        // StudentTestResultsFragment (on a tablet detail container) when this widget list item is clicked.
        Intent intent = new Intent();
        intent.putExtra(STUDENT_ENTRY_KEY, studentEntry);
        widgetListItemRemoteViews.setOnClickFillInIntent(R.id.widget_list_student_item, intent);


        return widgetListItemRemoteViews;
    }


    @Override
    public void onDataSetChanged() {
        // Loads student data from the Room Database and orders it to display it on the Widget
        mStudentEntries = StudentsOrderUtils.UpdateStudentsListOrder(
                mContext, AppDatabase.getInstance(mContext).studentDao().loadAllStudentsData());
        mDefaultTestType = TestTypeUtils.getDefaultTestTypeValueFromSharedPreferences(mContext);
    }


    @Override
    public int getCount() {
        if (mStudentEntries == null) {
            return 0;
        } else if (mStudentEntries.isEmpty()) {
            return 0;
        } else {
            return mStudentEntries.size();
        }
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public void onCreate() {
    }


    @Override
    public void onDestroy() {
    }
}
