package com.hangtom.ravy.khmerfinder.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

public class PassedLevelActivity extends AppCompatActivity {

    ImageView img_level;
    Button btn_play_next_level,btn_choose_level_play;
    private SharedPreferencesFile sharedPreferencesFile;
    private int level;
    private String TAG = PassedLevelActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView TVmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passed_level);
        Notification();
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "");
        level = sharedPreferencesFile.getIntSharedPreference("level","level");

        img_level = (ImageView) findViewById(R.id.img_level);
        btn_play_next_level = (Button) findViewById(R.id.btn_play_next_level);
        btn_choose_level_play =(Button) findViewById(R.id.btn_choose_level);

        if(level==1){
            img_level.setImageResource(R.drawable.success_level_1);
        }else if(level==2){
            img_level.setImageResource(R.drawable.success_level_2);
        }

        btn_play_next_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassedLevelActivity.this, PlayActivity.class);
                if(level==1){
                    sharedPreferencesFile.putIntSharedPreference("level","level",2);
                    intent.putExtra("Game", 1+"");
                }else if(level==2){
                    sharedPreferencesFile.putIntSharedPreference("level","level",3);
                    intent.putExtra("Game", 1+"");
                }
                startActivity(intent);
            }
        });


        btn_choose_level_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassedLevelActivity.this,ChooseLevelActivity.class);
                startActivity(intent);
            }
        });
    }

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
                    final Dialog dialog = new Dialog(PassedLevelActivity.this);
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
