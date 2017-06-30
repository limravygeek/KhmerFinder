package com.hangtom.ravy.khmerfinder.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.app.Config;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NextActivity extends Activity {
	private int tmp;
	private int score = 0;
	private ImageView img_rate,medayi_ad;
	private MediaPlayer winSound;
	private boolean is_sound;
	private SharedPreferencesFile sharedPreferencesFile;
	private String TAG = PlayActivity.class.getSimpleName();
	InterstitialAd mInterstitialAd;
	Handler handler = new Handler();
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private TextView TVmessage;
	ShareDialog shareDialog;
	CallbackManager callbackManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_next);
		Notification();
		medayi_ad = (ImageView) findViewById(R.id.medayi_ad);
		medayi_ad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isNetworkAvailable(getApplicationContext())) {
					handler.removeCallbacksAndMessages(null);
					Intent i = new Intent(android.content.Intent.ACTION_VIEW);
					i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.askhmer.lockscreen"));
					startActivity(i);
				}
			}
		});
		sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
		is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");

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


		winSound = MediaPlayer.create(getApplicationContext(), R.raw.win);
		if(is_sound) {
			winSound.start();
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			tmp = Integer.parseInt(extras.getString("Game"));
			score = extras.getInt("score");
		}

		img_rate = (ImageView) findViewById(R.id.img_rate);
		if(score==400){
			Resources res = getResources();
			Drawable draw = res.getDrawable(R.drawable.start_four);
			img_rate.setImageDrawable(draw);
		}else if(score==300){
			Resources res = getResources();
			Drawable draw = res.getDrawable(R.drawable.start_three);
			img_rate.setImageDrawable(draw);
		}else if(score==200){
			Resources res = getResources();
			Drawable draw = res.getDrawable(R.drawable.start_two);
			img_rate.setImageDrawable(draw);
		}else if(score==100){
			Resources res = getResources();
			Drawable draw = res.getDrawable(R.drawable.start_one);
			img_rate.setImageDrawable(draw);
		}

		ScaleAnimation scaler = new ScaleAnimation((float) 0.7, (float) 1.0, (float) 0.7, (float) 1.0);
		scaler.setDuration(2000);
		img_rate.startAnimation(scaler);

	}
	
	// Click this button to fo to next game
	public void next(View view) {
		if(tmp%6==0 && isNetworkAvailable(this)){
			loadAds();
			Intent intent = new Intent(NextActivity.this, PlayActivity.class);
			intent.putExtra("Game", tmp+1+"");
			finish();
			startActivity(intent);
		}else{
			winSound.stop();
			medayi_ad.setVisibility(View.VISIBLE);
			handler.postDelayed(new Runnable(){
					public void run() {
						Intent intent = new Intent(NextActivity.this, PlayActivity.class);
						intent.putExtra("Game", tmp+1+"");
						finish();
						startActivity(intent);
					}
				}, 1500);
		}
	}


	public void share(View view){
		//sharedVia("com.facebook.katana");

		LoginManager.getInstance().logInWithPublishPermissions(
				NextActivity.this,
				Arrays.asList("publish_actions"));

		ShareLinkContent content = new ShareLinkContent.Builder()
				.setQuote("ទាញយកហ្គេមដ៍សប្បាយលេងនៅទីនេះ")
				.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder"))
				.build();
		shareDialog.show(content);
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
		is_sound = sharedPreferencesFile.getBooleanSharedPreference("IS_SOUND");
		if(is_sound) {
			winSound.start();
		}
		medayi_ad.setVisibility(View.GONE);

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
		winSound.stop();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}


	//------------admob method------------
	private void loadAds(){
		mInterstitialAd = new InterstitialAd(this);

		// set the ad unit ID
		mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

		AdRequest adRequest = new AdRequest.Builder()
				//.addTestDevice("A21E7C367B4FC18C0FE4FAE6BA7DD111")
				.build();

		// Load ads into Interstitial Ads
		mInterstitialAd.loadAd(adRequest);

		mInterstitialAd.setAdListener(new AdListener() {
			public void onAdLoaded() {
				showInterstitial();
			}

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				Intent intent = new Intent(NextActivity.this, PlayActivity.class);
				intent.putExtra("Game", tmp+1+"");
				finish();
				startActivity(intent);
			}
		});
	}

	private void showInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}

	//--------end admob method-----------

	public static boolean isNetworkAvailable(Context context) {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);

			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;
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
					final Dialog dialog = new Dialog(NextActivity.this);
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
