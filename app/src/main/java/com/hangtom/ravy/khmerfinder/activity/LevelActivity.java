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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.adapter.GameAdapter;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.listener.ClickListener;
import com.hangtom.ravy.khmerfinder.listener.RecyclerItemClickListenerInFragment;
import com.hangtom.ravy.khmerfinder.model.Game;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity {

    private List<Game> gameList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GameAdapter mAdapter;
    private int current_level;
    private int current_level_easy;
    private int current_level_medium;
    private int current_level_hard;
    private boolean lock;
    private Button btn_play_next;
    private SharedPreferencesFile sharedPreferencesFile;
    private int level;
    private static final String TAG = PlayActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView TVmessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        Notification();
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
        level = sharedPreferencesFile.getIntSharedPreference("level","level");
        current_level = sharedPreferencesFile.getIntSharedPreference("current_level", "current_level");
        current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy", "current_level_easy");
        current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium", "current_level_medium");
        current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard", "current_level_hard");
        btn_play_next = (Button) findViewById(R.id.btn_play_next);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new GameAdapter(gameList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(level==1){
            prepareGameDataEasy();
        }else if(level==2){
            prepareGameDataMedium();
        }else{
            prepareGameDataHard();
        }


        btn_play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                if(level==1){
                    if(current_level_easy<50){
                         intent.putExtra("Game", current_level_easy+1+"");
                    }else{
                         intent.putExtra("Game", current_level_easy+"");
                    }
                }else if(level==2){
                    if(current_level_medium<50){
                        intent.putExtra("Game", current_level_medium+1+"");
                    }else{
                        intent.putExtra("Game", current_level_medium+"");
                    }
                }else{
                    if(current_level_hard<50){
                        intent.putExtra("Game", current_level_hard+1+"");
                    }else{
                        intent.putExtra("Game", current_level_hard+"");
                    }
                }
                startActivity(intent);
            }
        });

        recyclerView
                .addOnItemTouchListener(new RecyclerItemClickListenerInFragment(getApplicationContext(), recyclerView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {


                        if(level==1){
                            Game game = gameList.get(position);
                            int number_level_click = game.getTag();
                            if(number_level_click > current_level_easy){
                                //Toast.makeText(getApplicationContext(), "can not!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                                intent.putExtra("Game", number_level_click+1+"");
                                startActivity(intent);
                            }
                        }else if(level==2){
                            Game game = gameList.get(position);
                            int number_level_click = game.getTag();
                            if(number_level_click > current_level_medium){
                                //Toast.makeText(getApplicationContext(), "can not!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                                intent.putExtra("Game", number_level_click+1+"");
                                startActivity(intent);
                            }
                        }else{
                            Game game = gameList.get(position);
                            int number_level_click = game.getTag();
                            if(number_level_click > current_level_hard){
                                //Toast.makeText(getApplicationContext(), "can not!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Intent intent = new Intent(LevelActivity.this, PlayActivity.class);
                                intent.putExtra("Game", number_level_click+1+"");
                                startActivity(intent);
                            }
                        }
                    }
                    @Override
                    public void onLongClick(final View view, final int position) {

                    }
                }));

    }



    //--------------easy game-----------------
    private void prepareGameDataEasy() {
    Integer[] arr ={
                R.drawable.image_easy_1,
                R.drawable.image_easy_2,
                R.drawable.image_easy_3,
                R.drawable.image_easy_4,
                R.drawable.image_easy_5,
                R.drawable.image_easy_6,
                R.drawable.image_easy_7,
                R.drawable.image_easy_8,
                R.drawable.image_easy_9,
                R.drawable.image_easy_10,
                R.drawable.image_easy_11,
                R.drawable.image_easy_12,
                R.drawable.image_easy_13,
                R.drawable.image_easy_14,
                R.drawable.image_easy_15,
                R.drawable.image_easy_16,
                R.drawable.image_easy_17,
                R.drawable.image_easy_18,
                R.drawable.image_easy_19,
                R.drawable.image_easy_20,
                R.drawable.image_easy_21,
                R.drawable.image_easy_22,
                R.drawable.image_easy_23,
                R.drawable.image_easy_24,
                R.drawable.image_easy_25,
                R.drawable.image_easy_26,
                R.drawable.image_easy_27,
                R.drawable.image_easy_28,
                R.drawable.image_easy_29,
                R.drawable.image_easy_30,
                R.drawable.image_easy_31,
                R.drawable.image_easy_32,
                R.drawable.image_easy_33,
                R.drawable.image_easy_34,
                R.drawable.image_easy_35,
                R.drawable.image_easy_36,
                R.drawable.image_easy_37,
                R.drawable.image_easy_38,
                R.drawable.image_easy_39,
                R.drawable.image_easy_40,
                R.drawable.image_easy_41,
                R.drawable.image_easy_42,
                R.drawable.image_easy_43,
                R.drawable.image_easy_44,
                R.drawable.image_easy_45,
                R.drawable.image_easy_46,
                R.drawable.image_easy_47,
                R.drawable.image_easy_48,
                R.drawable.image_easy_49,
                R.drawable.image_easy_50,
                R.drawable.image_easy_51,
                R.drawable.image_easy_52,
                R.drawable.image_easy_53,
                R.drawable.image_easy_54,
                R.drawable.image_easy_55,
                R.drawable.image_easy_56,
                R.drawable.image_easy_57,
                R.drawable.image_easy_58,
                R.drawable.image_easy_59,
                R.drawable.image_easy_60,
                R.drawable.image_easy_61,
                R.drawable.image_easy_62,
                R.drawable.image_easy_63,
                R.drawable.image_easy_64,
                R.drawable.image_easy_65,
                R.drawable.image_easy_66,
                R.drawable.image_easy_67,
                R.drawable.image_easy_68,
                R.drawable.image_easy_69,
                R.drawable.image_easy_70
    };

    for(int i= 0 ; i < arr.length; i++){
        if(i> current_level_easy){
            lock = true;
        }else{
            lock = false;
        }
        Game movie = new Game(i, arr[i],lock);
        gameList.add(movie);
    }
    mAdapter.notifyDataSetChanged();
}





    //----------------for medium game ----------------------

    private void prepareGameDataMedium() {

        Integer[] arr ={
                R.drawable.image_medium_1,
                R.drawable.image_medium_2,
                R.drawable.image_medium_3,
                R.drawable.image_medium_4,
                R.drawable.image_medium_5,
                R.drawable.image_medium_6,
                R.drawable.image_medium_7,
                R.drawable.image_medium_8,
                R.drawable.image_medium_9,
                R.drawable.image_medium_10,
                R.drawable.image_medium_11,
                R.drawable.image_medium_12,
                R.drawable.image_medium_13,
                R.drawable.image_medium_14,
                R.drawable.image_medium_15,
                R.drawable.image_medium_16,
                R.drawable.image_medium_17,
                R.drawable.image_medium_18,
                R.drawable.image_medium_19,
                R.drawable.image_medium_20,
                R.drawable.image_medium_21,
                R.drawable.image_medium_22,
                R.drawable.image_medium_23,
                R.drawable.image_medium_24,
                R.drawable.image_medium_25,
                R.drawable.image_medium_26,
                R.drawable.image_medium_27,
                R.drawable.image_medium_28,
                R.drawable.image_medium_29,
                R.drawable.image_medium_30,
                R.drawable.image_medium_31,
                R.drawable.image_medium_32,
                R.drawable.image_medium_33,
                R.drawable.image_medium_34,
                R.drawable.image_medium_35,
                R.drawable.image_medium_36,
                R.drawable.image_medium_37,
                R.drawable.image_medium_38,
                R.drawable.image_medium_39,
                R.drawable.image_medium_40,
                R.drawable.image_medium_41,
                R.drawable.image_medium_42,
                R.drawable.image_medium_43,
                R.drawable.image_medium_44,
                R.drawable.image_medium_45,
                R.drawable.image_medium_46,
                R.drawable.image_medium_47,
                R.drawable.image_medium_48,
                R.drawable.image_medium_49,
                R.drawable.image_medium_50,
                R.drawable.image_medium_51,
                R.drawable.image_medium_52,
                R.drawable.image_medium_53,
                R.drawable.image_medium_54,
                R.drawable.image_medium_55,
                R.drawable.image_medium_56,
                R.drawable.image_medium_57,
                R.drawable.image_medium_58,
                R.drawable.image_medium_59,
                R.drawable.image_medium_60,
                R.drawable.image_medium_61,
                R.drawable.image_medium_62,
                R.drawable.image_medium_63,
                R.drawable.image_medium_64,
                R.drawable.image_medium_65,
                R.drawable.image_medium_66,
                R.drawable.image_medium_67,
                R.drawable.image_medium_68,
                R.drawable.image_medium_69,
                R.drawable.image_medium_70
        };

        for(int i= 0 ; i < arr.length; i++){
            if(i> current_level_medium){
                lock = true;
            }else{
                lock = false;
            }
            Game movie = new Game(i, arr[i],lock);
            gameList.add(movie);
        }
        mAdapter.notifyDataSetChanged();
    }


    //----------------------hard

    private void prepareGameDataHard() {

        Integer[] arr ={

                R.drawable.image_hard_1,
                R.drawable.image_hard_2,
                R.drawable.image_hard_3,
                R.drawable.image_hard_4,
                R.drawable.image_hard_5,
                R.drawable.image_hard_6,
                R.drawable.image_hard_7,
                R.drawable.image_hard_8,
                R.drawable.image_hard_9,
                R.drawable.image_hard_10,
                R.drawable.image_hard_11,
                R.drawable.image_hard_12,
                R.drawable.image_hard_13,
                R.drawable.image_hard_14,
                R.drawable.image_hard_15,
                R.drawable.image_hard_16,
                R.drawable.image_hard_17,
                R.drawable.image_hard_18,
                R.drawable.image_hard_19,
                R.drawable.image_hard_20,
                R.drawable.image_hard_21,
                R.drawable.image_hard_22,
                R.drawable.image_hard_23,
                R.drawable.image_hard_24,
                R.drawable.image_hard_25,
                R.drawable.image_hard_26,
                R.drawable.image_hard_27,
                R.drawable.image_hard_28,
                R.drawable.image_hard_29,
                R.drawable.image_hard_30,
                R.drawable.image_hard_31,
                R.drawable.image_hard_32,
                R.drawable.image_hard_33,
                R.drawable.image_hard_34,
                R.drawable.image_hard_35,
                R.drawable.image_hard_36,
                R.drawable.image_hard_37,
                R.drawable.image_hard_38,
                R.drawable.image_hard_39,
                R.drawable.image_hard_40,
                R.drawable.image_hard_41,
                R.drawable.image_hard_42,
                R.drawable.image_hard_43,
                R.drawable.image_hard_44,
                R.drawable.image_hard_45,
                R.drawable.image_hard_46,
                R.drawable.image_hard_47,
                R.drawable.image_hard_48,
                R.drawable.image_hard_49,
                R.drawable.image_hard_50,
                R.drawable.image_hard_51,
                R.drawable.image_hard_52,
                R.drawable.image_hard_53,
                R.drawable.image_hard_54,
                R.drawable.image_hard_55,
                R.drawable.image_hard_56,
                R.drawable.image_hard_57,
                R.drawable.image_hard_58,
                R.drawable.image_hard_59,
                R.drawable.image_hard_60,
                R.drawable.image_hard_61,
                R.drawable.image_hard_62,
                R.drawable.image_hard_63,
                R.drawable.image_hard_64,
                R.drawable.image_hard_65,
                R.drawable.image_hard_66,
                R.drawable.image_hard_67,
                R.drawable.image_hard_68,
                R.drawable.image_hard_69,
                R.drawable.image_hard_70
        };

        for(int i= 0 ; i < arr.length; i++){
            if(i> current_level_hard){
                lock = true;
            }else{
                lock = false;
            }
            Game movie = new Game(i, arr[i],lock);
            gameList.add(movie);
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        current_level = sharedPreferencesFile.getIntSharedPreference("current_level", "current_level");
        current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy", "current_level_easy");
        current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium", "current_level_medium");
        current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard", "current_level_hard");
        mAdapter.clearData();

        if(level==1){
            prepareGameDataEasy();
        }else if(level==2){
            prepareGameDataMedium();
        }else{
            prepareGameDataHard();
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
                    final Dialog dialog = new Dialog(LevelActivity.this);
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