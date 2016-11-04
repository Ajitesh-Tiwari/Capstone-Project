package webarch.com.hablar.HelperClasses;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ajitesh on 2/11/16.
 */
@IgnoreExtraProperties
public class Chat {
    String name;
    String message;


    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }
}
