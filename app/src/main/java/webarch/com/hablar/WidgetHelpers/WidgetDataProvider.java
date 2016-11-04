package webarch.com.hablar.WidgetHelpers;

/**
 * Created by ajitesh on 4/11/16.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.lang.Override;
import java.lang.String;

import webarch.com.hablar.ContentProvider.DataContract;
import webarch.com.hablar.R;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    Context mContext = null;
    private Cursor data = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;

    }

    @Override
    public void onCreate() {
    }


    public void onDataSetChanged() {

        initData();

    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
        }
    }

    @Override
    public int getCount() {
        if (data==null)
            return 0;
        else
            return data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        if (position == AdapterView.INVALID_POSITION ||data == null || !data.moveToPosition(position)) {
            return null;
        }

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.hablar_widget_item);

        String userName = data.getString(data.getColumnIndex(DataContract.DataEntry.FEED_USER_NAME));
        String feed = data.getString(data.getColumnIndex(DataContract.DataEntry.FEED_MSG));
        view.setImageViewResource(R.id.ivProfile, mContext.getResources().getIdentifier("avatar_"+data.getInt(data.getColumnIndex(DataContract.DataEntry.FEED_USER_IMG)), "drawable", mContext.getPackageName()));
        view.setTextViewText(R.id.name, userName);
        view.setTextViewText(R.id.feed, feed);


        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra("feed_id", data.getString(data.getColumnIndex(DataContract.DataEntry.FEED_ID)));
        view.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {

        if (data != null) {
            data.close();
        }

        final long token = Binder.clearCallingIdentity();
        data = mContext.getContentResolver().query(DataContract.DataEntry.CONTENT_URI_FEED,
                new String[]{DataContract.DataEntry.FEED_ID,DataContract.DataEntry.FEED_USER_NAME,DataContract.DataEntry.FEED_MSG,DataContract.DataEntry.FEED_USER_IMG},
                null, null, DataContract.DataEntry._ID+" DESC");
        Binder.restoreCallingIdentity(token);

    }


}
