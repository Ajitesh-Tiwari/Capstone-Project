package webarch.com.hablar.ContentProvider;

/**
 * Created by ajitesh on 4/11/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DataDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "feed.db";
    private static final int DATABASE_VERSION = 1;

    public DataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FEED_TABLE = "CREATE TABLE " +
                DataContract.DataEntry.TABLE_FEED + "(" +
                DataContract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.DataEntry.FEED_ID + " TEXT NOT NULL, " +
                DataContract.DataEntry.FEED_MSG + " TEXT NOT NULL, "+
                DataContract.DataEntry.FEED_USER_IMG+" INTEGER NOT NULL, "+
                DataContract.DataEntry.FEED_USER_NAME +" TEXT NOT NULL);";

        try{
            Log.d("Database","creatingDatabase");
            sqLiteDatabase.execSQL(SQL_CREATE_FEED_TABLE);
        }
        catch (Exception e){
            Log.d("Database",e.toString());
        }

    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataContract.DataEntry.TABLE_FEED);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                DataContract.DataEntry.TABLE_FEED+ "'");


        // re-create database
        onCreate(sqLiteDatabase);
    }
}
