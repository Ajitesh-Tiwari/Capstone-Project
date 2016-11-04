package webarch.com.hablar.HelperClasses;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ajitesh on 12/10/16.
 */

public class MyApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
