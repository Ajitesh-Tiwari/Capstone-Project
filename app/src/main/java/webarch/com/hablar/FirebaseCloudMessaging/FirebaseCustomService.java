package webarch.com.hablar.FirebaseCloudMessaging;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajitesh on 1/11/16.
 */

public class FirebaseCustomService extends FirebaseInstanceIdService {
    FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Test", "Refreshed token: " + refreshedToken);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        // TODO: Implement this method to send any registration to your app's servers.
        if(firebaseUser!=null)
            sendRegistrationToServer(refreshedToken);
    }
    public void sendRegistrationToServer(String token){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String emailStr=firebaseUser.getEmail();
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(firebaseUser.getUid()).child("fcmid").setValue(token);
    }
}
