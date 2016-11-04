package webarch.com.hablar.HelperClasses;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ajitesh on 2/11/16.
 */
@IgnoreExtraProperties
public class Comment {
    int avatar;
    String name,comment,userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getComment() {
        return comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
