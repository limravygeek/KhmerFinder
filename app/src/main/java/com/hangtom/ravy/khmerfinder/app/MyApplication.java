package com.hangtom.ravy.khmerfinder.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hangtom.ravy.khmerfinder.service.MyFirebaseMessagingService;

/**
 * Created by Ravy on 1/13/2017.
 */

public class MyApplication extends Application {

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static boolean activityResumed() {
        activityVisible = true;
        return true;
    }

    public static boolean activityPaused() {
        activityVisible = false;
        return false;
    }

    private static boolean activityVisible;
}
