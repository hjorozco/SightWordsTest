package com.weebly.hectorjorozco.sightwordstest.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.RemoteViews;

import com.weebly.hectorjorozco.sightwordstest.R;
import com.weebly.hectorjorozco.sightwordstest.ui.MainActivity;
import com.weebly.hectorjorozco.sightwordstest.ui.StudentTestResultsActivity;
import com.weebly.hectorjorozco.sightwordstest.utils.NumberOfStudentsUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.StudentsOrderUtils;
import com.weebly.hectorjorozco.sightwordstest.utils.TestTypeUtils;

import static com.weebly.hectorjorozco.sightwordstest.ui.MainActivity.EMPTY_STRING;

// Class that provides the methods to update the widget

public class WidgetProvider extends AppWidgetProvider {

    private static final int ZERO = 0;

    /**
     * Called in the application when the students table information is modified in the database
     */
    public static void updateWidgetWithStudentsInformation(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        updateWidgets(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Updates all the app widgets when the widget provider receiver calls it.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Updates all the app widgets when students table information is modified in the database.
     */
    private static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }


    /**
     * Updates a single Widget
     *
     * @param context          The application context
     * @param appWidgetManager The app widget manager
     * @param appWidgetId      The ID of the widget that will be updated
     */
    private static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                                     int appWidgetId) {


        // Gets the RemoteViews object for the widget
        RemoteViews widgetRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

        // Set the title of the widget.
        widgetRemoteViews.setTextViewText(R.id.widget_title_text_view, Html.fromHtml(
                TestTypeUtils.getDefaultTestTypeTitleFromSharedPreferences(context, true)));

        // Show the sorted by information.
        int numberOfStudents = NumberOfStudentsUtils.getNumberOfStudentsFromSharedPreferences(context);
        int standardPadding = (int) context.getResources().getDimension(R.dimen.application_standard_padding);
        int smallPadding = (int) context.getResources().getDimension(R.dimen.application_small_padding);

        String studentWordSuffix = context.getString(R.string.lowercase_letter_s);
        if (numberOfStudents == 1) {
            studentWordSuffix = EMPTY_STRING;
        }
        widgetRemoteViews.setViewPadding(R.id.widget_title_text_view,
                smallPadding, standardPadding, smallPadding, ZERO);
        widgetRemoteViews.setViewVisibility(R.id.widget_sorted_by_info_text_view, View.VISIBLE);
        widgetRemoteViews.setTextViewText(R.id.widget_sorted_by_info_text_view,
                context.getString(R.string.activity_main_sorted_by_message,
                        numberOfStudents, studentWordSuffix, StudentsOrderUtils.getStudentsOrderString(context)));


        // Sets pending intent to open the application's Main Activity when the widget title is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, MainActivity.class), 0);
        widgetRemoteViews.setOnClickPendingIntent(R.id.widget_main_layout, pendingIntent);

        // Sets pending intent template to open the application StudentTestResultsActivity (on a phone)
        // or the MainActivity (on a tablet)  when a list item in the widget list view is clicked.
        Intent intentToShowStudentTestResults;
        if (context.getResources().getBoolean(R.bool.tablet)) {
            intentToShowStudentTestResults = new Intent(context, MainActivity.class);
        } else {
            intentToShowStudentTestResults = new Intent(context, StudentTestResultsActivity.class);
        }
        PendingIntent startStudentTestResultsActivityPendingIntent =
                PendingIntent.getActivity(context, 0, intentToShowStudentTestResults, 0);
        widgetRemoteViews.setPendingIntentTemplate(R.id.widget_students_list_view,
                startStudentTestResultsActivityPendingIntent);

        // Sets the intent to populate the list of ingredients on the widget
        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetRemoteViews.setRemoteAdapter(R.id.widget_students_list_view, intent);

        // Sets the widget empty view.
        widgetRemoteViews.setTextViewText(R.id.widget_empty_classroom_text_view, Html.fromHtml(context.getString(R.string.empty_classroom_message_widget)));
        widgetRemoteViews.setEmptyView(R.id.widget_students_list_view, R.id.widget_empty_classroom_text_view);

        appWidgetManager.updateAppWidget(appWidgetId, widgetRemoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.widget_students_list_view);

    }

}

