package com.weebly.hectorjorozco.sightwordstest.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;


/**
 * Service that provides a RemoteViewsFactory for the widget. That factory will populate the list
 * of student entries on the widget.
 */
public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        return new WidgetRemoteViewsFactory(getApplicationContext());
    }

}