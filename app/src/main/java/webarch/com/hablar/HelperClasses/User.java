package webarch.com.hablar.HelperClasses;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ajitesh on 26/9/16.
 */
@IgnoreExtraProperties
public class User {
    public String fcmid;
    public String username;
    public String registerID;
    public String email;
    public String uid;
    public int avatar;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String uid,String username, String registerID, String email) {
        this.username = username;
        this.email = email;
        this.registerID=registerID;
        this.uid=uid;
    }

    public String getFcmid() {
        return fcmid;
    }

    public void setFcmid(String fcmid) {
        this.fcmid = fcmid;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getRegisterID() {
        return registerID;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRegisterID(String registerID) {
        this.registerID = registerID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
