package webarch.com.hablar.HelperClasses;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ajitesh on 11/10/16.
 */
@IgnoreExtraProperties
public class Feed {
    User user;
    String comment;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
