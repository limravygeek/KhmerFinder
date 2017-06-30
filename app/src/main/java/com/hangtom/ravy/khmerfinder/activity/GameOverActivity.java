package com.hangtom.ravy.khmerfinder.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;


public class GameOverActivity extends Activity {
	private String tmp;
	private MediaPlayer lossSound;
	private boolean is_sound;
	private SharedPreferencesFile sharedPreferencesFile;
	private int level_playing;
	private int[] arrRate_easy;
	private int[] arrRate_medium;
	private int[] arrRate_hard;
	private int score = 0;
	private int level;
	private static final String TAG = PlayActivity.class.getSimpleName();
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private TextView TVmessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gameover);
		Notification();
		sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
		level = sharedPreferencesFile.getIntSharedPreference("level","level");
		is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
		lossSound = MediaPlayer.create(getApplicationContext(), R.raw.loss);
		if (is_sound) {
			lossSound.start();
		}
		tmp = getIntent().getStringExtra("Game");
		level_playing = Integer.parseInt(tmp);
		level_playing += 1;

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

	}

	public void retry(View view) {
		Intent in = new Intent(GameOverActivity.this, PlayActivity.class);
		in.putExtra("Game",tmp);
		startActivity(in);
		finish();
	}

	public void skip(View view) {
		int num = Integer.parseInt(tmp);
		if (num >= 70) {
			if(level==1){
				updateDataArrayEasy(num,score);
				storeIntArrayEasy(arrRate_easy);
				finish();
				Intent in = new Intent(GameOverActivity.this, PassedLevelActivity.class);
				startActivity(in);
			}else if(level==2){
				updateDataArrayMedium(num,score);
				storeIntArrayMedium(arrRate_medium);
				finish();
				Intent in = new Intent(GameOverActivity.this, PassedLevelActivity.class);
				startActivity(in);
			}else{
				updateDataArrayHard(num,score);
				storeIntArrayHard(arrRate_hard);
				finish();
				Intent in = new Intent(GameOverActivity.this, LastActivity.class);
				startActivity(in);
			}
		} else {

			level = sharedPreferencesFile.getIntSharedPreference("level","level");

			if(level==1){
				int current_level_easy = sharedPreferencesFile.getIntSharedPreference("current_level_easy", "current_level_easy");
				if (current_level_easy < num) {
					sharedPreferencesFile.putIntSharedPreference("current_level_easy", "current_level_easy", Integer.parseInt(tmp));
				}
			}else if(level==2){
				int current_level_medium = sharedPreferencesFile.getIntSharedPreference("current_level_medium", "current_level_medium");
				if (current_level_medium < num) {
					sharedPreferencesFile.putIntSharedPreference("current_level_medium", "current_level_medium", Integer.parseInt(tmp));
				}
			}else{
				int current_level_hard = sharedPreferencesFile.getIntSharedPreference("current_level_hard", "current_level_hard");
				if (current_level_hard < num) {
					sharedPreferencesFile.putIntSharedPreference("current_level_hard", "current_level_hard", Integer.parseInt(tmp));
				}
			}

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

			Intent intent = new Intent(GameOverActivity.this, PlayActivity.class);
			intent.putExtra("Game",level_playing+"");
			startActivity(intent);
			finish();
		}
	}



	public void back(View view) {
		Intent in = new Intent(GameOverActivity.this, LevelActivity.class);
		finish();
		startActivity(in);
	}

	@Override
	protected void onResume() {
		super.onResume();
		is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
		if(is_sound) {
			lossSound.start();
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
		lossSound.stop();
	}




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
		arrRate_easy = new int[71];   // more than array 1 (if 10 is 11)
		for (int i = 0; i < 71; i++){ // more than array 1 (if 10 is 11)
			arrRate_easy[i] = prefs.getInt("IntValue_"+ i, i);
			Log.i("arr :"+i,arrRate_easy[i]+"");
		}
		return arrRate_easy;
	}


	//----------------------medium--------------------

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
		arrRate_medium = new int[71];   // more than array 1 (if 10 is 11)
		for (int i = 0; i < 71; i++){ // more than array 1 (if 10 is 11)
			arrRate_medium[i] = prefs.getInt("IntValue_"+ i, i);
			Log.i("arr :"+i,arrRate_medium[i]+"");
		}
		return arrRate_medium;
	}


	//----------------------hard--------------------

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
		arrRate_hard = new int[71];   // more than array 1 (if 10 is 11)
		for (int i = 0; i < 71; i++){ // more than array 1 (if 10 is 11)
			arrRate_hard[i] = prefs.getInt("IntValue_"+ i, i);
			Log.i("arr :"+i,arrRate_hard[i]+"");
		}
		return arrRate_hard;
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
					final Dialog dialog = new Dialog(GameOverActivity.this);
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
