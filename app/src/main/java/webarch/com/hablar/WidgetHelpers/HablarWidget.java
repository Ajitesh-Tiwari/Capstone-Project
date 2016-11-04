package webarch.com.hablar.WidgetHelpers;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import webarch.com.hablar.FeedFragment.FeedDetails;
import webarch.com.hablar.R;
import webarch.com.hablar.SyncAdapter.DataSyncAdapter;


public class HablarWidget extends AppWidgetProvider {


    public static final String SYNC_CLICKED= "webarch.com.hablar.SYNC_CLICKED";


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.hablar_widget);
        final Intent refreshIntent = new Intent(context, HablarWidget.class);
        refreshIntent.setAction(HablarWidget.SYNC_CLICKED);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.update, refreshPendingIntent);
        views.setEmptyView(R.id.widget_list, R.id.noStockWidget);

        final Intent openAppIntent = new Intent(context, FeedDetails.class);

        //openAppIntent.putExtra("feed_id",);
        PendingIntent openAppPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(openAppIntent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list, openAppPendingIntent);
        // Set up the collection

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(SYNC_CLICKED)) {
            onRefresh(context);
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final ComponentName componentName = new ComponentName(context, HablarWidget.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.widget_list);
        }
    }

    public void onRefresh(Context context){
        DataSyncAdapter.syncImmediately(context);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */



    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, HablarWidgetRemoteViewService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, HablarWidgetRemoteViewService.class));
    }

}

