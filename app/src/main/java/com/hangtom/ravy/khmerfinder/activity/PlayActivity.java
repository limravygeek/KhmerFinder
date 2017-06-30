package com.hangtom.ravy.khmerfinder.activity;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.ActivityBase;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.app.MyApplication;
import com.hangtom.ravy.khmerfinder.service.BackgroundSoundService;
import com.hangtom.ravy.khmerfinder.service.JobSchedulerService;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends ActivityBase {

    private int marginLeft;
    private int marginTop;
    private ImageView img_readygo;
    private ImageView imgOriginal;
    private ImageView imgCopy;
    private ImageView img_heart;
    private ImageView btn_sound;
    private RelativeLayout rlGame;
    private RelativeLayout rlGame1;
    private String[] arGamePosition;
    private String[] arGamePosition1;

    private int widthScreen = -1;
    private int heightScreen = -1;

    private TextView txtTime;
    private TextView txtPoint;
    private TextView txtLevel;
    private TextView txtScore;
    private long time = 4 * 60 * 100;
    private Timer timer;
    private TimerTask timerTask;
    private String tmp;
    private int wrong_click = 0;
    private int point = 0;
    private int score = 0;
    private boolean is_music,is_sound,is_vibrate;
    private MediaPlayer mp;
    private MediaPlayer readygo;
    private Intent svc;
    private int[] arrRate_easy;
    private int[] arrRate_medium;
    private int[] arrRate_hard;
    private long backKeyPressedTime = 0;
    private Toast toast;
    Handler handler = new Handler();
    public int level;
    private int current_level;
    private ArrayList<ImageView> arMarker = new ArrayList<ImageView>();
    private SharedPreferencesFile sharedPreferencesFile;
    private static final String TAG = PlayActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView TVmessage;

    private  ImageView marker_1;
    private  ImageView marker_2;
    private  ImageView marker_3;
    private  ImageView marker_4;
    private  ImageView marker1_1;
    private  ImageView marker1_2;
    private  ImageView marker1_3;
    private  ImageView marker1_4;
    private  Button btnHint;
    private int hint_no;
    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        try {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }catch (NoClassDefFoundError e){e.printStackTrace();}
        Notification();
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "");
        level = sharedPreferencesFile.getIntSharedPreference("level","level");
        hint_no = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");

        if(level==1){
            getFromPrefsEasy();
            storeIntArrayEasy(arrRate_easy);
        }else if(level==2){
            getFromPrefsMedium();
            storeIntArrayMedium(arrRate_medium);
        }else{
            getFromPrefsHard();
            storeIntArrayHard(arrRate_hard);
        }

        mp = MediaPlayer.create(getApplicationContext(), R.raw.success);
        readygo = MediaPlayer.create(getApplicationContext(), R.raw.readygo);
        svc = new Intent(this, BackgroundSoundService.class);
        if (is_music) {
            startService(svc);
        }
        initUI();

        btnHint = (Button) findViewById(R.id.btnHINT);
        btnHint.setText(hint_no+"");
        if(hint_no<=0){
            Drawable d = getResources().getDrawable(R.drawable.button_hint_disabled);
            btnHint.setBackgroundResource(R.drawable.button_hint_disabled);
        }
        btnHint.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                hint_no = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                if(hint_no<=0){
                    AlertDialog.Builder alert = new AlertDialog.Builder(PlayActivity.this);
                    alert.setIcon(R.drawable.ic_help);
                    alert.setTitle("ទទួលបានHINT");
                    alert.setMessage("តើអ្នកចង់ស្វែងរកHINTបន្ថែមទេ?");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PlayActivity.this, AchievementActivity.class);
                            startActivity(intent);
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();

                }else{
                    if(!img_readygo.isEnabled() && point != 4) {
                        hint();
                    }
                }
            }
        });

        //------------impact  animation

        img_readygo = (ImageView) findViewById(R.id.img_readygo);
        StartAnimations();
        new Handler().postDelayed(new Runnable(){
            public void run() {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setDuration(1000);
                img_readygo.startAnimation(fadeOut);
                img_readygo.setVisibility(View.GONE);
            }
        }, 3000);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                img_readygo.setEnabled(false);
            }
        }, 4000);

        //------------impact animation

    }
    //--------------------------------
    public void initUI(){
        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
        level = sharedPreferencesFile.getIntSharedPreference("level","level");
        is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
        is_vibrate = sharedPreferencesFile.getBooleanSharedPreference("IS_VIBRATE");
        tmp = getIntent().getStringExtra("Game");

        // Get different position from resource
        if(level==1){
            arGamePosition = getResources().getStringArray(getResources().getIdentifier("GameEasy_" + getIntent().getStringExtra("Game"), "array", getPackageName()));
            arGamePosition1 = getResources().getStringArray(getResources().getIdentifier("GameEasy1_" + getIntent().getStringExtra("Game"), "array", getPackageName()));

        }else if(level==2){
            arGamePosition = getResources().getStringArray(getResources().getIdentifier("GameMedium_" + getIntent().getStringExtra("Game"), "array", getPackageName()));
            arGamePosition1 = getResources().getStringArray(getResources().getIdentifier("GameMedium1_" + getIntent().getStringExtra("Game"), "array", getPackageName()));

        }else {
            arGamePosition = getResources().getStringArray(getResources().getIdentifier("GameHard_" + getIntent().getStringExtra("Game"), "array", getPackageName()));
            arGamePosition1 = getResources().getStringArray(getResources().getIdentifier("GameHard1_" + getIntent().getStringExtra("Game"), "array", getPackageName()));
        }

        point = arGamePosition.length;

        txtScore = (TextView)findViewById(R.id.txtScore);
        txtScore.setVisibility(View.INVISIBLE);
        txtTime = (TextView)findViewById(R.id.txtTime);
        txtPoint = (TextView)findViewById(R.id.txtPoint);
        txtLevel = (TextView)findViewById(R.id.txtLevel);
        txtLevel.setText("វគ្គ "+tmp);

        btn_sound = (ImageView) findViewById(R.id.btn_sound);
        img_heart = (ImageView) findViewById(R.id.img_heart);
        imgOriginal = (ImageView)findViewById(R.id.imgOriginal);
        imgCopy = (ImageView)findViewById(R.id.imgCopy);

        if(level==1){
            imgOriginal.setImageResource(getResources().getIdentifier("image_easy_" + getIntent().getStringExtra("Game"), "drawable", getPackageName()));
            imgCopy.setImageResource(getResources().getIdentifier("image_easy_" + getIntent().getStringExtra("Game") + "_copy", "drawable", getPackageName()));

        }else if(level==2){
            imgOriginal.setImageResource(getResources().getIdentifier("image_medium_" + getIntent().getStringExtra("Game"), "drawable", getPackageName()));
            imgCopy.setImageResource(getResources().getIdentifier("image_medium_" + getIntent().getStringExtra("Game") + "_copy", "drawable", getPackageName()));
        }else {
            imgOriginal.setImageResource(getResources().getIdentifier("image_hard_" + getIntent().getStringExtra("Game"), "drawable", getPackageName()));
            imgCopy.setImageResource(getResources().getIdentifier("image_hard_" + getIntent().getStringExtra("Game") + "_copy", "drawable", getPackageName()));
        }

        //---------------------------------------------------------

        imgOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled() && point != 4) {

                    if (is_vibrate) {
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                    }
                    wrong_click += 1;

                    if (wrong_click == 0) {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.full_heart);
                        img_heart.setImageDrawable(draw);
                    } else if (wrong_click == 1) {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.heart2);
                        img_heart.setImageDrawable(draw);
                    } else if (wrong_click == 2) {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.heart1);
                        img_heart.setImageDrawable(draw);
                    } else {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.heart0);
                        img_heart.setImageDrawable(draw);
                    }

                    if (wrong_click >= 3) {

//                        if(level==1){
//                            sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
//                        }else if(level==2){
//                            sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
//                        }else{
//                            sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
//                        }
                        Intent intent = new Intent(PlayActivity.this, GameOverActivity.class);
                        intent.putExtra("Game", tmp);
                        startActivityForResult(intent, 1);
                    }
                }

            }
        });

        //----------------------------------------------------------
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled() && point != 4){
                    if (is_vibrate) {
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                        vibrator.vibrate(500);
                    }

                    wrong_click += 1;

                    if (wrong_click == 0) {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.full_heart);
                        img_heart.setImageDrawable(draw);
                    } else if (wrong_click == 1) {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.heart2);
                        img_heart.setImageDrawable(draw);
                    } else if (wrong_click == 2) {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.heart1);
                        img_heart.setImageDrawable(draw);
                    } else {
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.heart0);
                        img_heart.setImageDrawable(draw);
                    }

                    if (wrong_click >= 3) {
//                            if(level==1){
//                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
//                            }else if(level==2){
//                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
//                            }else{
//                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
//                            }

                        Intent intent = new Intent(PlayActivity.this, GameOverActivity.class);
                        intent.putExtra("Game", tmp);
                        startActivityForResult(intent, 1);
                    }
                }

            }

        });

        rlGame = (RelativeLayout)findViewById(R.id.rlGame);
        rlGame1 = (RelativeLayout)findViewById(R.id.rlGame1);

        rlGame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                rlGame.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                widthScreen = rlGame.getWidth();
                heightScreen = rlGame.getHeight();
                initGame();

            }
        });

        rlGame1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                rlGame1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                widthScreen = rlGame1.getWidth();
                heightScreen = rlGame1.getHeight();
            }
        });

        if(is_sound){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.speaker );
            btn_sound.setImageDrawable(draw);
        }else if(is_music){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.speaker );
            btn_sound.setImageDrawable(draw);
        } else{
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.no_speaker );
            btn_sound.setImageDrawable(draw);
        }



        btn_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                    if (is_sound) {
                        sharedPreferencesFile.putBooleanSharedPreference("IS_SOUND", false);
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.no_speaker);
                        btn_sound.setImageDrawable(draw);

                        is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
                        if (is_music) {
                            readygo.stop();
                            stopService(svc);
                        }

                    } else {
                        sharedPreferencesFile.putBooleanSharedPreference("IS_SOUND", true);
                        is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
                        if (is_music) {
                            startService(svc);
                        }
                        //-----------------------------
                        Resources res = getResources();
                        Drawable draw = res.getDrawable(R.drawable.speaker);
                        btn_sound.setImageDrawable(draw);
                    }
                }

            }
        });
    }


    //-------------------------------------
    // Init position of different
    public void initGame() {

        for(int i = 0 ; i < arMarker.size(); i++) {
            rlGame.removeView(arMarker.get(i));
        }

        arMarker.clear();

        point = 0;
        time = 4 * 60 * 100;
        txtTime.setText("4:00:00");

        try {
            int i = 0;
            String rateLeft = arGamePosition[i].split(" # ")[0];
            String rateTop = arGamePosition[i].split(" # ")[1];

            marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
            marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

            Log.e("Position " + i, marginLeft + " / " + marginTop);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

            marker_1 = new ImageView(getApplicationContext());
            marker_1.setImageResource(android.R.color.transparent);
            lp.setMargins(marginLeft, marginTop, 0, 0);
            marker_1.setLayoutParams(lp);

            marker1_1 = new ImageView(getApplicationContext());
            marker1_1.setImageResource(android.R.color.transparent);
            lp1.setMargins(marginLeft, marginTop, 0, 0);
            marker1_1.setLayoutParams(lp1);

            rlGame.addView(marker_1);
            rlGame1.addView(marker1_1);

        }catch (Exception e){

        }

        try{
            int i = 1;
            String rateLeft = arGamePosition[i].split(" # ")[0];
            String rateTop = arGamePosition[i].split(" # ")[1];

            marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
            marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

            Log.e("Position " + i, marginLeft + " / " + marginTop);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

            marker_2 = new ImageView(getApplicationContext());
            marker_2.setImageResource(android.R.color.transparent);
            lp.setMargins(marginLeft, marginTop, 0, 0);
            marker_2.setLayoutParams(lp);

            marker1_2 = new ImageView(getApplicationContext());
            marker1_2.setImageResource(android.R.color.transparent);
            lp1.setMargins(marginLeft, marginTop, 0, 0);
            marker1_2.setLayoutParams(lp1);

            rlGame.addView(marker_2);
            rlGame1.addView(marker1_2);

        }catch (Exception e){}



        try{
            int i = 2;
            String rateLeft = arGamePosition[i].split(" # ")[0];
            String rateTop = arGamePosition[i].split(" # ")[1];

            marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
            marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

            Log.e("Position " + i, marginLeft + " / " + marginTop);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

            marker_3 = new ImageView(getApplicationContext());
            marker_3.setImageResource(android.R.color.transparent);
            lp.setMargins(marginLeft, marginTop, 0, 0);
            marker_3.setLayoutParams(lp);

            marker1_3 = new ImageView(getApplicationContext());
            marker1_3.setImageResource(android.R.color.transparent);
            lp1.setMargins(marginLeft, marginTop, 0, 0);
            marker1_3.setLayoutParams(lp1);

            rlGame.addView(marker_3);
            rlGame1.addView(marker1_3);

        }catch (Exception e){}


        try{
            int i = 3;
            String rateLeft = arGamePosition[i].split(" # ")[0];
            String rateTop = arGamePosition[i].split(" # ")[1];

            marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
            marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

            Log.e("Position " + i, marginLeft + " / " + marginTop);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

            marker_4 = new ImageView(getApplicationContext());
            marker_4.setImageResource(android.R.color.transparent);
            lp.setMargins(marginLeft, marginTop, 0, 0);
            marker_4.setLayoutParams(lp);

            marker1_4 = new ImageView(getApplicationContext());
            marker1_4.setImageResource(android.R.color.transparent);
            lp1.setMargins(marginLeft, marginTop, 0, 0);
            marker1_4.setLayoutParams(lp1);

            rlGame.addView(marker_4);
            rlGame1.addView(marker1_4);

        }catch (Exception e){}




        marker_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker_1.getTag() == null && v.getTag()==null) {
                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_1;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_1;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });



        //-----------marker 2

        marker_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker_2.getTag() == null && v.getTag()==null) {

                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_2;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_2;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });




        //-----------marker 3

        marker_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker_3.getTag() == null && v.getTag()==null) {

                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_3;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_3;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });



        //-----------marker 4

        marker_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker_4.getTag() == null && v.getTag()==null) {

                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_4;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_4;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });

        //--------------------------------------------------------





        marker1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker1_1.getTag() == null && v.getTag()==null) {
                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_1;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_1;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });



        //-----------marker 2

        marker1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker1_2.getTag() == null && v.getTag()==null) {

                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_2;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_2;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });




        //-----------marker 3

        marker1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker1_3.getTag() == null && v.getTag()==null) {

                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_3;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_3;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });



        //-----------marker 4

        marker1_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!img_readygo.isEnabled()) {
                    // TODO Auto-generated method stub
                    if (marker1_4.getTag() == null && v.getTag()==null) {

                        point += 1;

                        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                        if (is_sound) {
                            mp.start();
                        }
                    }
                    Log.i("point :", point + "");
                    txtPoint.setText(point + "/4");

                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                    anim.reset();


                    ImageView img = (ImageView) marker_4;
                    img.setImageResource(R.drawable.mark_star);
                    img.clearAnimation();
                    img.startAnimation(anim);
                    img.setTag("selected");

                    ImageView img1 = (ImageView) marker1_4;
                    img1.setImageResource(R.drawable.mark_star);
                    img1.clearAnimation();
                    img1.startAnimation(anim);
                    img1.setTag("selected");

                    if (point == arGamePosition.length) {
                        timer.cancel();
                        int num = Integer.parseInt(tmp);
                        if (num >= 70) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                                startActivity(in);

                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                                txtTime.setText("0:00:00");
                                timer.cancel();
                                finish();
                                Intent in = new Intent(PlayActivity.this, LastActivity.class);
                                startActivity(in);
                            }

                        } else {
                            if(level==1){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                            }else if(level==2){
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                            }else{
                                current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                            }

                            if (current_level < num) {
                                if(level==1){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                                }else if(level==2){
                                    sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                                }else{
                                    sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                                }

                            }
                            txtTime.setText("0:00:00");
                            timer.cancel();

                            if(level==1){
                                updateDataArrayEasy(num,score);
                                storeIntArrayEasy(arrRate_easy);
                            }else if(level==2){
                                updateDataArrayMedium(num,score);
                                storeIntArrayMedium(arrRate_medium);
                            }else{
                                updateDataArrayHard(num,score);
                                storeIntArrayHard(arrRate_hard);
                            }

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                    intent.putExtra("Game", tmp);
                                    intent.putExtra("score",score);
                                    startActivityForResult(intent, 1);
                                }
                            }, 1000);
                        }
                    }
                }

            }
        });





        handler.postDelayed(new Runnable(){
            public void run() {
                reScheduleTimer();
                if(!MyApplication.isActivityVisible()){
                    timer.cancel();
                }
            }
        }, 4000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_CANCELED) {
                finish();
            } else if(resultCode == RESULT_OK) {
                initGame();
            }
        }
    }
    // Scheduler
    public void reScheduleTimer() {
        timer = new Timer();
        timerTask = new myTimerTask();
        timer.schedule(timerTask, 0, 10);
    }

    private class myTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            time -= 1;
            updateLabel.sendEmptyMessage(0);
        }
    }

    // Update text Time
    private Handler updateLabel = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg);
            if (time >= 0) {
                int miliseconds = (int) time % 100;
                int s = (int) (time / 100) % 60;
                int m = (int) (time / (100 * 60)) % 60;
                txtTime.setText(String.format("%2d:%02d:%02d", m, s, miliseconds));
                Log.e("time :",m+"");
                if(m==3){
                    score = 400;
                }else if(m==2){
                    score = 300;
                }else if(m==1){
                    score = 200;
                }else if(m==0){
                    score =100;
                }
            } else {
                Log.e("lose","lose");
                timer.cancel();
                if(MyApplication.activityResumed()) {

//                   if(level==1){
//                       sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
//                   }else if(level==2){
//                       sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
//                   }else{
//                       sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
//                   }

                    finish();
                    Intent intent = new Intent(PlayActivity.this, GameOverActivity.class);
                    intent.putExtra("Game", getIntent().getStringExtra("Game"));
                    startActivityForResult(intent, 1);
                }
            }
        }
    };




    @Override
    protected void onResume() {
        super.onResume();
        try{
            timer.cancel();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
        if(is_music){
            readygo.start();
        }

        is_music = sharedPreferencesFile.getBooleanSharedPreference("IS_MUSIC");
        if (is_music){
            startService(svc);
        }

        is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
        if(is_sound){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.speaker );
            btn_sound.setImageDrawable(draw);
        }else if(is_music){
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.speaker );
            btn_sound.setImageDrawable(draw);
        }else{
            Resources res = getResources();
            Drawable draw = res.getDrawable( R.drawable.no_speaker );
            btn_sound.setImageDrawable(draw);
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
    protected void onDestroy() {
        super.onDestroy();
        try{
            handler.removeCallbacksAndMessages(null);
            timer.cancel();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        try{
            handler.removeCallbacksAndMessages(null);
            timer.cancel();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        stopService(svc);
        readygo.stop();
    }

    public class Main extends ActivityBase {
        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
        }
    }




    //------------------for easy level -----------


    public void updateDataArrayEasy(int level,int score){
        for (int i = 0; i < arrRate_easy.length; i++)
        {
            if (i == level)
            {
                arrRate_easy[i-1] = score;
            }
        }
    }

    public void storeIntArrayEasy(int[] array){
        SharedPreferences.Editor edit= getApplicationContext().getSharedPreferences("RATE_EASY", getApplicationContext().MODE_PRIVATE).edit();
        edit.putInt("Count", array.length);
        int count = 0;
        for (int i: array){
            edit.putInt("IntValue_" + count++, i);
        }
        edit.commit();
    }


    public int[] getFromPrefsEasy(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("RATE_EASY", getApplicationContext().MODE_PRIVATE);
        int count = prefs.getInt("Count", 0);
        arrRate_easy = new int[51];   // more than array 1 (if 10 is 11)
        for (int i = 0; i < 51; i++){ // more than array 1 (if 10 is 11)
            arrRate_easy[i] = prefs.getInt("IntValue_"+ i, i);
            Log.i("arr :"+i,arrRate_easy[i]+"");
        }
        return arrRate_easy;
    }



    //------------------for medium level -----------


    public void updateDataArrayMedium(int level,int score){
        for (int i = 0; i < arrRate_medium.length; i++)
        {
            if (i == level)
            {
                arrRate_medium[i-1] = score;
            }
        }
    }

    public void storeIntArrayMedium(int[] array){
        SharedPreferences.Editor edit= getApplicationContext().getSharedPreferences("RATE_MEDIUM", getApplicationContext().MODE_PRIVATE).edit();
        edit.putInt("Count", array.length);
        int count = 0;
        for (int i: array){
            edit.putInt("IntValue_" + count++, i);
        }
        edit.commit();
    }


    public int[] getFromPrefsMedium(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("RATE_MEDIUM", getApplicationContext().MODE_PRIVATE);
        int count = prefs.getInt("Count", 0);
        arrRate_medium = new int[51];   // more than array 1 (if 10 is 11)
        for (int i = 0; i < 51; i++){ // more than array 1 (if 10 is 11)
            arrRate_medium[i] = prefs.getInt("IntValue_"+ i, i);
            Log.i("arr :"+i,arrRate_medium[i]+"");
        }
        return arrRate_medium;
    }



    //------------------for medium level -----------


    public void updateDataArrayHard(int level,int score){
        for (int i = 0; i < arrRate_hard.length; i++)
        {
            if (i == level)
            {
                arrRate_hard[i-1] = score;
            }
        }
    }

    public void storeIntArrayHard(int[] array){
        SharedPreferences.Editor edit= getApplicationContext().getSharedPreferences("RATE_HARD", getApplicationContext().MODE_PRIVATE).edit();
        edit.putInt("Count", array.length);
        int count = 0;
        for (int i: array){
            edit.putInt("IntValue_" + count++, i);
        }
        edit.commit();
    }


    public int[] getFromPrefsHard(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("RATE_HARD", getApplicationContext().MODE_PRIVATE);
        int count = prefs.getInt("Count", 0);
        arrRate_hard = new int[51];   // more than array 1 (if 10 is 11)
        for (int i = 0; i < 51; i++){ // more than array 1 (if 10 is 11)
            arrRate_hard[i] = prefs.getInt("IntValue_"+ i, i);
            Log.i("arr :"+i,arrRate_hard[i]+"");
        }
        return arrRate_hard;
    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView l = (ImageView) findViewById(R.id.img_readygo);
        l.clearAnimation();
        l.startAnimation(anim);
    }



    //Enable backward
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK: onBackPressed();
            }
        } catch (NullPointerException e) {

        } catch (Exception e1) {

        }
        return false;
    }

    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            handler.removeCallbacksAndMessages(null);
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(this, "សូមចុចម្តងទៀតដើម្បីចាកចេញ", Toast.LENGTH_SHORT);
        toast.show();
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
                    final Dialog dialog = new Dialog(PlayActivity.this);
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


    private void hint() {
        int  hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
        hint_number = hint_number - 1;
        sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",hint_number);
        if(hint_number>0) {
            btnHint.setText(hint_number+"");
        }else{
            btnHint.setText("0");
            btnHint.setBackgroundResource(R.drawable.button_hint_disabled);

            JobInfo.Builder builder = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                builder = new JobInfo.Builder( 1,
                                new ComponentName( getPackageName(), JobSchedulerService.class.getName() ) );
                    // builder.setPeriodic(TimeUnit.HOURS.toMillis(1));
                builder.setPeriodic(TimeUnit.HOURS.toMillis(3));
                sharedPreferencesFile.putBooleanSharedPreference("jobschedule_delay", true);
                if( mJobScheduler.schedule( builder.build() ) <= 0 ) {
                        //If something goes wrong
                    }
            }
        }

        if (marker_1.getTag() == null) {

            if(!img_readygo.isEnabled()) {
                point += 1;
                is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                if (is_sound) {
                    mp.start();
                }
                Log.i("point :", point + "");
                txtPoint.setText(point + "/4");

                int i = 0;
                String rateLeft = arGamePosition[i].split(" # ")[0];
                String rateTop = arGamePosition[i].split(" # ")[1];

                marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
                marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

                Log.e("Position " + i, marginLeft + " / " + marginTop);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

                marker_1 = new ImageView(getApplicationContext());
                marker_1.setImageResource(android.R.color.transparent);
                lp.setMargins(marginLeft, marginTop, 0, 0);
                marker_1.setLayoutParams(lp);

                marker1_1 = new ImageView(getApplicationContext());
                marker1_1.setImageResource(android.R.color.transparent);
                lp1.setMargins(marginLeft, marginTop, 0, 0);
                marker1_1.setLayoutParams(lp1);

                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                anim.reset();

                ImageView img = marker_1;
                img.setImageResource(R.drawable.mark_hint);
                img.clearAnimation();
                img.startAnimation(anim);
                img.setTag("selected");
                marker_1.setTag("selected");

                ImageView img1 = marker1_1;
                img1.setImageResource(R.drawable.mark_hint);
                img1.clearAnimation();
                img1.startAnimation(anim);
                img1.setTag("selected");
                marker_1.setTag("selected");

                rlGame.addView(marker_1);
                rlGame1.addView(marker1_1);


                //------------------

                if (point == arGamePosition.length) {
                    timer.cancel();
                    int num = Integer.parseInt(tmp);
                    if (num >= 70) {
                        if(level==1){
                            sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else if(level==2){
                            sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else{
                            sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, LastActivity.class);
                            startActivity(in);
                        }

                    } else {
                        if(level==1){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                        }else if(level==2){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                        }else{
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                        }

                        if (current_level < num) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            }

                        }
                        txtTime.setText("0:00:00");
                        timer.cancel();

                        if(level==1){
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                        }else if(level==2){
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                        }else{
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                        }

                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                intent.putExtra("Game", tmp);
                                intent.putExtra("score",score);
                                startActivityForResult(intent, 1);
                            }
                        }, 1000);
                    }
                }
                //-----------------
            }

        }else if(marker_2.getTag()==null){

            if(!img_readygo.isEnabled()) {
                point += 1;
                is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                if (is_sound) {
                    mp.start();
                }
                Log.i("point :", point + "");
                txtPoint.setText(point + "/4");

                int i = 1;
                String rateLeft = arGamePosition[i].split(" # ")[0];
                String rateTop = arGamePosition[i].split(" # ")[1];

                marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
                marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

                Log.e("Position " + i, marginLeft + " / " + marginTop);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

                marker_2 = new ImageView(getApplicationContext());
                marker_2.setImageResource(android.R.color.transparent);
                lp.setMargins(marginLeft, marginTop, 0, 0);
                marker_2.setLayoutParams(lp);

                marker1_2 = new ImageView(getApplicationContext());
                marker1_2.setImageResource(android.R.color.transparent);
                lp1.setMargins(marginLeft, marginTop, 0, 0);
                marker1_2.setLayoutParams(lp1);


                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                anim.reset();

                ImageView img = marker_2;
                img.setImageResource(R.drawable.mark_hint);
                img.clearAnimation();
                img.startAnimation(anim);
                marker_2.setTag("selected");

                ImageView img1 = marker1_2;
                img1.setImageResource(R.drawable.mark_hint);
                img1.clearAnimation();
                img1.startAnimation(anim);
                marker1_2.setTag("selected");

                rlGame.addView(marker_2);
                rlGame1.addView(marker1_2);

                //------------------

                if (point == arGamePosition.length) {
                    timer.cancel();
                    int num = Integer.parseInt(tmp);
                    if (num >= 70) {
                        if(level==1){
                            sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else if(level==2){
                            sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else{
                            sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, LastActivity.class);
                            startActivity(in);
                        }

                    } else {
                        if(level==1){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                        }else if(level==2){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                        }else{
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                        }

                        if (current_level < num) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            }

                        }
                        txtTime.setText("0:00:00");
                        timer.cancel();

                        if(level==1){
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                        }else if(level==2){
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                        }else{
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                        }

                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                intent.putExtra("Game", tmp);
                                intent.putExtra("score",score);
                                startActivityForResult(intent, 1);
                            }
                        }, 1000);
                    }
                }
                //-----------------
            }

        }else if(marker_3.getTag()==null){

            if(!img_readygo.isEnabled()) {
                point += 1;
                is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                if (is_sound) {
                    mp.start();
                }
                Log.i("point :", point + "");
                txtPoint.setText(point + "/4");

                int i = 2;
                String rateLeft = arGamePosition[i].split(" # ")[0];
                String rateTop = arGamePosition[i].split(" # ")[1];

                marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
                marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

                Log.e("Position " + i, marginLeft + " / " + marginTop);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

                marker_3 = new ImageView(getApplicationContext());
                marker_3.setImageResource(android.R.color.transparent);
                lp.setMargins(marginLeft, marginTop, 0, 0);
                marker_3.setLayoutParams(lp);

                marker1_3 = new ImageView(getApplicationContext());
                marker1_3.setImageResource(android.R.color.transparent);
                lp1.setMargins(marginLeft, marginTop, 0, 0);
                marker1_3.setLayoutParams(lp1);


                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                anim.reset();

                ImageView img = marker_3;
                img.setImageResource(R.drawable.mark_hint);
                img.clearAnimation();
                img.startAnimation(anim);
                marker_3.setTag("selected");

                ImageView img1 = marker1_3;
                img1.setImageResource(R.drawable.mark_hint);
                img1.clearAnimation();
                img1.startAnimation(anim);
                marker1_3.setTag("selected");

                rlGame.addView(marker_3);
                rlGame1.addView(marker1_3);


                //------------------

                if (point == arGamePosition.length) {
                    timer.cancel();
                    int num = Integer.parseInt(tmp);
                    if (num >= 70) {
                        if(level==1){
                            sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else if(level==2){
                            sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else{
                            sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, LastActivity.class);
                            startActivity(in);
                        }

                    } else {
                        if(level==1){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                        }else if(level==2){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                        }else{
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                        }

                        if (current_level < num) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            }

                        }
                        txtTime.setText("0:00:00");
                        timer.cancel();

                        if(level==1){
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                        }else if(level==2){
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                        }else{
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                        }

                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                intent.putExtra("Game", tmp);
                                intent.putExtra("score",score);
                                startActivityForResult(intent, 1);
                            }
                        }, 1000);
                    }
                }
                //-----------------

            }

        }else if(marker_4.getTag()==null){

            if(!img_readygo.isEnabled()) {
                point += 1;
                is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
                if (is_sound) {
                    mp.start();
                }
                Log.i("point :", point + "");
                txtPoint.setText(point + "/4");

                int i = 3;
                String rateLeft = arGamePosition[i].split(" # ")[0];
                String rateTop = arGamePosition[i].split(" # ")[1];

                marginLeft = widthScreen / Integer.parseInt(rateLeft.split("/")[0]) * Integer.parseInt(rateLeft.split("/")[1]);
                marginTop = heightScreen / Integer.parseInt(rateTop.split("/")[0]) * Integer.parseInt(rateTop.split("/")[1]);

                Log.e("Position " + i, marginLeft + " / " + marginTop);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition[i].split(" # ")[2]));
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]), heightScreen / Integer.parseInt(arGamePosition1[i].split(" # ")[2]));

                marker_4 = new ImageView(getApplicationContext());
                marker_4.setImageResource(android.R.color.transparent);
                lp.setMargins(marginLeft, marginTop, 0, 0);
                marker_4.setLayoutParams(lp);

                marker1_4 = new ImageView(getApplicationContext());
                marker1_4.setImageResource(android.R.color.transparent);
                lp1.setMargins(marginLeft, marginTop, 0, 0);
                marker1_4.setLayoutParams(lp1);

                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                anim.reset();

                ImageView img = marker_4;
                img.setImageResource(R.drawable.mark_hint);
                img.clearAnimation();
                img.startAnimation(anim);
                marker_4.setTag("selected");

                ImageView img1 = marker1_4;
                img1.setImageResource(R.drawable.mark_hint);
                img1.clearAnimation();
                img1.startAnimation(anim);
                marker_4.setTag("selected");

                rlGame.addView(marker_4);
                rlGame1.addView(marker1_4);

                //------------------

                if (point == arGamePosition.length) {
                    timer.cancel();
                    int num = Integer.parseInt(tmp);
                    if (num >= 70) {
                        if(level==1){
                            sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else if(level==2){
                            sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, PassedLevelActivity.class);
                            startActivity(in);

                        }else{
                            sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                            txtTime.setText("0:00:00");
                            timer.cancel();
                            finish();
                            Intent in = new Intent(PlayActivity.this, LastActivity.class);
                            startActivity(in);
                        }

                    } else {
                        if(level==1){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_easy","current_level_easy");
                        }else if(level==2){
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_medium","current_level_medium");
                        }else{
                            current_level = sharedPreferencesFile.getIntSharedPreference("current_level_hard","current_level_hard");
                        }

                        if (current_level < num) {
                            if(level==1){
                                sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
                            }else if(level==2){
                                sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
                            }else{
                                sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
                            }

                        }
                        txtTime.setText("0:00:00");
                        timer.cancel();

                        if(level==1){
                            updateDataArrayEasy(num,score);
                            storeIntArrayEasy(arrRate_easy);
                        }else if(level==2){
                            updateDataArrayMedium(num,score);
                            storeIntArrayMedium(arrRate_medium);
                        }else{
                            updateDataArrayHard(num,score);
                            storeIntArrayHard(arrRate_hard);
                        }

                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                Intent intent = new Intent(PlayActivity.this, NextActivity.class);
                                intent.putExtra("Game", tmp);
                                intent.putExtra("score",score);
                                startActivityForResult(intent, 1);
                            }
                        }, 1000);
                    }
                }
                //-----------------

            }
        }
    }
}
