package com.hangtom.ravy.khmerfinder.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnReset,btn_yes_reset,btn_no_reset,btnMusic,btnSound,btnVibrate;
    private TextView txtResetsuccess;
    private LinearLayout layout_confirm_reset;
    private SharedPreferencesFile sharedPreferencesFile;
    private int current_level;
    private int current_level_easy, current_level_medium,current_level_hard;
    private boolean is_music,is_sound,is_vibrate;
    private int[] arrRate;
    private int level;
    private String TAG = SettingActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView TVmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Notification();
        arrRate = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                            0};
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
        level = sharedPreferencesFile.getIntSharedPreference("level","level");
        current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy", "current_level_easy");
        current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium", "current_level_medium");
        current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard", "current_level_hard");

        is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
        is_vibrate = sharedPreferencesFile.getBooleanSharedPreference("IS_VIBRATE");


        btnReset = (Button) findViewById(R.id.btnReset);
        layout_confirm_reset = (LinearLayout) findViewById(R.id.layout_confirm_reset);
        btn_yes_reset = (Button) findViewById(R.id.btn_yes_reset);
        btn_no_reset = (Button) findViewById(R.id.btn_no_reset);
        txtResetsuccess = (TextView) findViewById(R.id.txtResetsuccess);
        btnMusic = (Button) findViewById(R.id.btnMusic);
        btnSound = (Button) findViewById(R.id.btnSound);
        btnVibrate = (Button) findViewById(R.id.btnVibrate);

        if(is_vibrate){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_vibrate);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnVibrate.setBackground(draw);
            }
        }else{
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_vibrate_disable );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnVibrate.setBackground(draw);
            }
        }

        if(current_level_easy>0|| current_level_medium>0|| current_level_hard>0){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_reset);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnReset.setBackground(draw);
            }
        }else{
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_reset_disable );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnReset.setBackground(draw);
            }
        }

        if(is_music){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_music);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnMusic.setBackground(draw);
            }
        }else{
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_music_disable );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnMusic.setBackground(draw);
            }
        }

        if(is_sound){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_sound);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnSound.setBackground(draw);
            }
        }else{
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.button_sound_disable );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnSound.setBackground(draw);
            }
        }

        btnReset.setOnClickListener(this);
        btn_yes_reset.setOnClickListener(this);
        btn_no_reset.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        btnSound.setOnClickListener(this);
        btnVibrate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnMusic:
                is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
                if(is_music){
                    sharedPreferencesFile.putBooleanSharedPreference("IS_MUSIC",false);
                    Resources res = getResources();
                    Drawable draw = res.getDrawable( R.drawable.button_music_disable );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnMusic.setBackground(draw);
                    }
                }else{
                    sharedPreferencesFile.putBooleanSharedPreference("IS_MUSIC",true);
                    Resources res = getResources();
                    Drawable draw = res.getDrawable( R.drawable.button_music);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnMusic.setBackground(draw);
                    }
                }
                break;

            case R.id.btnSound:
                is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                if(is_sound){
                    sharedPreferencesFile.putBooleanSharedPreference("IS_SOUND",false);
                    Resources res = getResources();
                    Drawable draw = res.getDrawable( R.drawable.button_sound_disable );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnSound.setBackground(draw);
                    }
                }else{
                    sharedPreferencesFile.putBooleanSharedPreference("IS_SOUND",true);
                    Resources res = getResources();
                    Drawable draw = res.getDrawable( R.drawable.button_sound);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnSound.setBackground(draw);
                    }
                }
                break;

            case R.id.btnReset:
                current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy", "current_level_easy");
                current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium", "current_level_medium");
                current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard", "current_level_hard");
                if(current_level_easy>0|| current_level_medium>0|| current_level_hard>0){
                    layout_confirm_reset.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_yes_reset:
                storeIntArrayEasy(arrRate);
                storeIntArrayMedium(arrRate);
                storeIntArrayhard(arrRate);
                layout_confirm_reset.setVisibility(View.GONE);
                sharedPreferencesFile.putIntSharedPreference("level", "level", 1);
                sharedPreferencesFile.putIntSharedPreference("current_level", "current_level", 0);
                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", 0);
                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", 0);
                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", 0);
                txtResetsuccess.setVisibility(View.VISIBLE);
                Resources res = getResources();
                Drawable draw = res.getDrawable( R.drawable.button_reset_disable );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnReset.setBackground(draw);
                }
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        txtResetsuccess.animate().alpha(0.0f).setDuration(2000);
                        txtResetsuccess.setVisibility(View.GONE);
                    }
                }, 3000);
                break;
            case R.id.btn_no_reset:
                layout_confirm_reset.setVisibility(View.GONE);
                break;
            case R.id.btnVibrate:
                is_vibrate = sharedPreferencesFile.getBooleanSharedPreference("IS_VIBRATE");
                if(is_vibrate){
                    sharedPreferencesFile.putBooleanSharedPreference("IS_VIBRATE",false);
                    Resources res1 = getResources();
                    Drawable draw1 = res1.getDrawable( R.drawable.button_vibrate_disable );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnVibrate.setBackground(draw1);
                    }
                }else{
                    sharedPreferencesFile.putBooleanSharedPreference("IS_VIBRATE",true);
                    Resources res1 = getResources();
                    Drawable draw1 = res1.getDrawable( R.drawable.button_vibrate);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        btnVibrate.setBackground(draw1);
                    }
                }
                break;
        }
    }


    public void storeIntArrayEasy(int[] array){
        SharedPreferences.Editor edit  = getApplicationContext().getSharedPreferences("RATE_EASY", getApplicationContext().MODE_PRIVATE).edit();
        edit.putInt("Count", array.length);
        int count = 0;
        for (int i: array){
            edit.putInt("IntValue_" + count++, 0);
        }
        edit.commit();
    }

    public void storeIntArrayMedium(int[] array){
        SharedPreferences.Editor edit  = getApplicationContext().getSharedPreferences("RATE_MEDIUM", getApplicationContext().MODE_PRIVATE).edit();
        edit.putInt("Count", array.length);
        int count = 0;
        for (int i: array){
            edit.putInt("IntValue_" + count++, 0);
        }
        edit.commit();
    }

    public void storeIntArrayhard(int[] array){
        SharedPreferences.Editor edit  = getApplicationContext().getSharedPreferences("RATE_HARD", getApplicationContext().MODE_PRIVATE).edit();
        edit.putInt("Count", array.length);
        int count = 0;
        for (int i: array){
            edit.putInt("IntValue_" + count++, 0);
        }
        edit.commit();
    }


    //-------------

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        // NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void Notification(){

        //------------------------------------Notifications---------------------------

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    //------------------
                    final Dialog dialog = new Dialog(SettingActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setContentView(R.layout.alert_dialog_message);
                    TVmessage = (TextView) dialog.findViewById(R.id.tvMessage);
                    TVmessage.setText(message);
                    TVmessage.setMovementMethod(new ScrollingMovementMethod());
                    dialog.show();

                    dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder"));
                            startActivity(i);
                            dialog.dismiss();
                        }
                    });
                    //--------
                }
            }
        };

        displayFirebaseRegId();

        //-------------------end notification------------------
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Firebase Reg Id: " + regId);
        }else {
            // txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }


}
