package webarch.com.hablar.SyncAdapter;

/**
 * Created by ajitesh on 4/11/16.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;
    public AuthenticatorService() {
    }

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
