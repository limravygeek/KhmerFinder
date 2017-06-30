package com.hangtom.ravy.khmerfinder.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

public class ChooseLevelActivity extends AppCompatActivity {
    LinearLayout btn_easy,btn_medium,btn_hard;
    TextView txt_level_played_easy,txt_level_played_medium,txt_level_played_hard;
    TextView txt_easy,txt_medium,txt_hard;
    private SharedPreferencesFile sharedPreferencesFile;
    private int level;
    public int current_level_easy=0;
    public int current_level_medium=0;
    public int current_level_hard=0;
    private static final String TAG = PlayActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView TVmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);

        Notification();
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "Clevel");
        current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
        current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
        current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");

        btn_easy = (LinearLayout) findViewById(R.id.btn_easy);
        btn_medium = (LinearLayout) findViewById(R.id.btn_medium);
        btn_hard = (LinearLayout) findViewById(R.id.btn_hard);

        txt_level_played_easy = (TextView) findViewById(R.id.txt_level_played_easy);
        txt_level_played_medium = (TextView) findViewById(R.id.txt_level_played_medium);
        txt_level_played_hard = (TextView) findViewById(R.id.txt_level_played_hard);

        txt_easy = (TextView) findViewById(R.id.txt_easy);
        txt_medium = (TextView) findViewById(R.id.txt_medium);
        txt_hard = (TextView) findViewById(R.id.txt_hard);

        Typeface font = Typeface.createFromAsset(getAssets(), "kh_kulen.TTF");
        txt_easy.setTypeface(font);
        txt_medium.setTypeface(font);
        txt_hard.setTypeface(font);

        txt_level_played_easy.setText(current_level_easy+"/50");
        txt_level_played_medium.setText(current_level_medium+"/50");
        txt_level_played_hard.setText(current_level_hard+"/50");

        btn_easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesFile.putIntSharedPreference("level","level",1);
                Intent intent = new Intent(ChooseLevelActivity.this,LevelActivity.class);
               // intent.putExtra("Clevel","easy");
                startActivity(intent);
            }
        });

        btn_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesFile.putIntSharedPreference("level","level",2);
                Intent intent = new Intent(ChooseLevelActivity.this,LevelActivity.class);
               // intent.putExtra("Clevel","medium");
                startActivity(intent);
            }
        });


        btn_hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesFile.putIntSharedPreference("level","level",3);
                Intent intent = new Intent(ChooseLevelActivity.this, LevelActivity.class);
               // intent.putExtra("Clevel","hard" );
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
        current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
        current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");

        txt_level_played_easy.setText(current_level_easy+"/70");
        txt_level_played_medium.setText(current_level_medium+"/70");
        txt_level_played_hard.setText(current_level_hard+"/70");


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
                    final Dialog dialog = new Dialog(ChooseLevelActivity.this);
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
