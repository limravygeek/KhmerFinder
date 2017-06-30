package com.hangtom.ravy.khmerfinder.app;

import android.app.Activity;

/**
 * Created by Ravy on 1/13/2017.
 */

public class ActivityBase extends Activity {

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }
}
