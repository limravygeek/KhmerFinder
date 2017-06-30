package com.hangtom.ravy.khmerfinder.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends Activity {
    private Button btn_play,btn_level,btn_setting,btn_sharefb,btn_gethint;
    private SharedPreferencesFile sharedPreferencesFile;
    private int current_level;
    private int current_level_easy;
    private int current_level_medium;
    private int current_level_hard;
    public int level;
    private static final String TAG = PlayActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public TextView TVmessage;
    private boolean first_installed;
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
        current_level = sharedPreferencesFile.getIntSharedPreference("current_level", "current_level");
        level = sharedPreferencesFile.getIntSharedPreference("level","level");
        first_installed = sharedPreferencesFile.getBooleanSharedPreference("first_installed");



        //--------------share code------------
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                int hint_number;
                hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                hint_number = hint_number + 5;
                sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",hint_number);
            }

            @Override
            public void onCancel() {
                //Toast.makeText(AchievementActivity.this, "share cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                //Toast.makeText(AchievementActivity.this, "share error", Toast.LENGTH_SHORT).show();
            }
        });

        //----------end share code----



        if(first_installed){
            sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",5);
            sharedPreferencesFile.putBooleanSharedPreference("first_installed", false);
        }
        if(getIntent().getExtras() != null) {
            String str = String.valueOf(getIntent().getExtras());
            Log.i("str", str);
            if (str.equals("Bundle[mParcelledData.dataSize=328]")){
            //Toast.makeText(this, "str :" + str, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder"));
            startActivity(i);
             }
        }
        Notification();
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_level = (Button) findViewById(R.id.btn_level);
        btn_setting = (Button) findViewById(R.id.btnSetting);
        btn_sharefb = (Button) findViewById(R.id.btn_sharefb);
        btn_gethint = (Button) findViewById(R.id.btn_getHint);

        if(level==0){
            sharedPreferencesFile.putIntSharedPreference("level","level",1);
        }
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy", "current_level_easy");
                current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium", "current_level_medium");
                current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard", "current_level_hard");
                if(level==1){
                    Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                    intent.putExtra("Game", current_level_easy+1+"");
                    startActivity(intent);
                }else if(level==2){
                    Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                    intent.putExtra("Game", current_level_medium+1+"");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                    intent.putExtra("Game", current_level_hard+1+"");
                    startActivity(intent);
                }
            }
        });

        btn_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,ChooseLevelActivity.class);
                startActivity(intent);
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btn_sharefb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sharedVia("com.facebook.katana");

                LoginManager.getInstance().logInWithPublishPermissions(
                        HomeActivity.this,
                        Arrays.asList("publish_actions"));

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setQuote("ទាញយកហ្គេមដ៍សប្បាយលេងនៅទីនេះ")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder"))
                        .build();
                shareDialog.show(content);
            }
        });

        btn_gethint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AchievementActivity.class);
                startActivity(intent);
            }
        });


    }

    private void sharedVia(String packageName) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(packageName);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder");
        try {
            startActivity(sharingIntent);
        }catch (ActivityNotFoundException e) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("សូមអភ័យទោស")
                    .setContentText("ទូរស័ព្ទរបស់អ្នកមិនមានកម្មវិធីហ្វេសប៊ុកទេ។")
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        level = sharedPreferencesFile.getIntSharedPreference("level","level");
        current_level = sharedPreferencesFile.getIntSharedPreference("current_level", "current_level");
        if(level==0){
            sharedPreferencesFile.putIntSharedPreference("level","level",1);
        }


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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void Notification(){

        //------------------------------------Notifications---------------------------

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    final Dialog dialog = new Dialog(HomeActivity.this);
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
