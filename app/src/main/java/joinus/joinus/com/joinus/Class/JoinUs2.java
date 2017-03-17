package joinus.joinus.com.joinus.Class;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Romi on 18/10/2016.
 */

public class JoinUs2 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
