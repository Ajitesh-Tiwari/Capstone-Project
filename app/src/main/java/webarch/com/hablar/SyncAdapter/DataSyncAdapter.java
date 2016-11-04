package webarch.com.hablar.SyncAdapter;

/**
 * Created by ajitesh on 4/11/16.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.appwidget.AppWidgetManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import webarch.com.hablar.ContentProvider.DataContract;
import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.R;
import webarch.com.hablar.WidgetHelpers.HablarWidget;

/**
 * Created by ajitesh on 20/2/16.
 */
public class DataSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = DataSyncAdapter.class.getSimpleName();
    Context context;
    private DatabaseReference mDatabase;
    FirebaseUser firebaseUser;


    public DataSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context=context;
        mDatabase= FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        storeFeed(provider);
    }

    public void storeFeed(final ContentProviderClient contentProviderClient){
        addEventListener(contentProviderClient);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }

    public void addEventListener(final ContentProviderClient contentProviderClient){
        try {
            contentProviderClient.delete(DataContract.DataEntry.CONTENT_URI_FEED, null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
                    Feed feed = dataSnapshot.getValue(Feed.class);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataContract.DataEntry.FEED_ID,feed.getKey());
                    contentValues.put(DataContract.DataEntry.FEED_MSG,feed.getComment());
                    contentValues.put(DataContract.DataEntry.FEED_USER_NAME, feed.getUser().getUsername());
                    contentValues.put(DataContract.DataEntry.FEED_USER_IMG, feed.getUser().getAvatar());

                    contentProviderClient.insert(DataContract.DataEntry.CONTENT_URI_FEED,contentValues);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(context, databaseError.getMessage().toString(),
                //Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=firebaseUser.getEmail();
        mDatabase.child("feed").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).addChildEventListener(childEventListener);
    }
}
