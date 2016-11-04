package webarch.com.hablar.ContentProvider;

/**
 * Created by ajitesh on 4/11/16.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {
    public static final String CONTENT_AUTHORITY = "webarch.com.hablar.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class DataEntry implements BaseColumns {
        // table name
        public static final String TABLE_FEED = "feed";
        public static final String _ID = "_id";
        public static final String FEED_ID= "fid";
        public static final String FEED_USER_NAME = "username";
        public static final String FEED_MSG = "message";
        public static final String FEED_USER_IMG = "userimg";


        // create content uri
        public static final Uri CONTENT_URI_FEED = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FEED).build();

        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE_FEED =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FEED;

        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE_FEED =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FEED;


        // for building URIs on insertion
        public static Uri buildFlavorsUri(Uri uri, long id) {
            return ContentUris.withAppendedId(uri, id);
        }
    }
}

