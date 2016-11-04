package webarch.com.hablar.ContentProvider;

/**
 * Created by ajitesh on 4/11/16.
 */
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DataProvider extends ContentProvider {
    private static final String LOG_TAG = DataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int FEED = 100;
    private static final int FEED_WITH_ID = 200;


    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, DataContract.DataEntry.TABLE_FEED, FEED);
        matcher.addURI(authority, DataContract.DataEntry.TABLE_FEED + "/#", FEED_WITH_ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        Log.d("Database","onCreate()");
        mOpenHelper = new DataDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FEED: {
                return DataContract.DataEntry.CONTENT_DIR_TYPE_FEED;
            }
            case FEED_WITH_ID: {
                return DataContract.DataEntry.CONTENT_ITEM_TYPE_FEED;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // All Flavors selected
            case FEED: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.DataEntry.TABLE_FEED,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case FEED_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.DataEntry.TABLE_FEED,
                        projection,
                        DataContract.DataEntry.FEED_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case FEED: {
                long _id = db.insert(DataContract.DataEntry.TABLE_FEED, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = DataContract.DataEntry.buildFlavorsUri(uri,_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case FEED:
                numDeleted = db.delete(
                        DataContract.DataEntry.TABLE_FEED, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        DataContract.DataEntry.TABLE_FEED + "'");
                break;
            case FEED_WITH_ID:
                numDeleted = db.delete(DataContract.DataEntry.TABLE_FEED,
                        DataContract.DataEntry.FEED_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        DataContract.DataEntry.TABLE_FEED+ "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FEED:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInsertedAnnouncement = 0;
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(DataContract.DataEntry.TABLE_FEED,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG, "Attempting to insert " +
                                    value.getAsString(
                                            DataContract.DataEntry.FEED_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInsertedAnnouncement++;
                        }
                    }
                    if (numInsertedAnnouncement > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInsertedAnnouncement > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInsertedAnnouncement;
            default:
                getContext().getContentResolver().notifyChange(uri, null);
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case FEED: {
                numUpdated = db.update(DataContract.DataEntry.TABLE_FEED,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FEED_WITH_ID: {
                numUpdated = db.update(DataContract.DataEntry.TABLE_FEED,
                        contentValues,
                        DataContract.DataEntry.FEED_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}