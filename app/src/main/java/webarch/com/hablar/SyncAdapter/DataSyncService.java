package webarch.com.hablar.SyncAdapter;

/**
 * Created by ajitesh on 4/11/16.
 */


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DataSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static DataSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new DataSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}
