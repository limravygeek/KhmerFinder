package com.hangtom.ravy.khmerfinder.activity;

import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.TintAwareDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.adapter.AchiementAdapter;
import com.hangtom.ravy.khmerfinder.adapter.GameAdapter;
import com.hangtom.ravy.khmerfinder.listener.ClickListener;
import com.hangtom.ravy.khmerfinder.listener.RecyclerItemClickListenerInFragment;
import com.hangtom.ravy.khmerfinder.model.Achievement;
import com.hangtom.ravy.khmerfinder.model.Game;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AchievementActivity extends AppCompatActivity {

    private List<Achievement> achievementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AchiementAdapter mAdapter;
    private Achievement achievement;
    private SharedPreferencesFile sharedPreferencesFile;
    TextView txtTotalHint;
    TextView txtTitle;
    private int hint_no;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    Handler handler;
    public ACProgressFlower dialog;
    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }

        sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "current_level");
        hint_no = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTotalHint = (TextView) findViewById(R.id.txtTotalHint);
        Typeface font = Typeface.createFromAsset(getAssets(), "kh_kulen.TTF");
        txtTitle.setTypeface(font);
        txtTotalHint.setTypeface(font);

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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_achievement);
        mAdapter = new AchiementAdapter(achievementList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView
                            .addOnItemTouchListener(new RecyclerItemClickListenerInFragment(getApplicationContext(), recyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            try{
                                 achievement = achievementList.get(position);
                            }catch (ArrayIndexOutOfBoundsException e){
                                e.printStackTrace();
                            }
                            String app_name = achievement.getApp_name();
                            if(app_name.equals("FACEBOOK")){

                                LoginManager.getInstance().logInWithPublishPermissions(
                                        AchievementActivity.this,
                                        Arrays.asList("publish_actions"));

                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setQuote("ទាញយកហ្គេមដ៍សប្បាយលេងនៅទីនេះ")
                                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder"))
                                        .build();
                                shareDialog.show(content);
                            }else{
                                if(achievement.isInstalled()){
                                    new SweetAlertDialog(AchievementActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setCustomImage(R.drawable.ic_help)
                                            .setTitleText("សូមអភ័យទោស")
                                            .setContentText("អ្នកបានធ្វើវារួចហើយ!!")
                                            .show();
                                }else {
                                    String link_playstore = achievement.getLink_url();
                                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(link_playstore));
                                    startActivity(i);
                                }
                            }
                        }
                        @Override
                        public void onLongClick(final View view, final int position) {

                        }
                }));

    }

    public void loadAchievementData(){

        boolean medayi_installed = isPackageExisted("com.askhmer.lockscreen");
        boolean khmerapphelper_installed = isPackageExisted("com.longdy.khmerapphelper");
        boolean khmerfrog_installed = isPackageExisted("com.longdy.medayi.khmerfrog");
        boolean hangtom_installed = isPackageExisted("com.hangtom");

        Achievement achiement1 = new Achievement(true,"Khmer Finder",5,R.drawable.icon_khmerfinder,
                "https://play.google.com/store/apps/details?id=com.hangtom.ravy.khmerfinder",true);
        achievementList.add(achiement1);


        Achievement achiement2 = new Achievement(medayi_installed,"Medayi Slide",5,R.drawable.icon_medayi_slide
                    ,"https://play.google.com/store/apps/details?id=com.askhmer.lockscreen",true);
        achievementList.add(achiement2);

        Achievement achiement3 = new Achievement(khmerapphelper_installed, "Khmer App Helper", 5, R.drawable.icon_khmer_app_helper
                    , "https://play.google.com/store/apps/details?id=com.longdy.khmerapphelper"
                    , true);
        achievementList.add(achiement3);

        Achievement achiement4 = new Achievement(khmerfrog_installed,"Khmer Frog",5,R.drawable.icon_khmer_frog
                    ,"https://play.google.com/store/apps/details?id=com.longdy.medayi.khmerfrog",true);
        achievementList.add(achiement4);


        Achievement achiement5 = new Achievement(hangtom_installed,"Hangtom",5,R.drawable.icon_hangtom
                    ,"https://play.google.com/store/apps/details?id=com.hangtom",true);
        achievementList.add(achiement5);

        Achievement achiement6 = new Achievement(false,"FACEBOOK",5,R.drawable.icon_sharefacebook,"",false);
        achievementList.add(achiement6);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading..")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                checkPackage();
                mAdapter.clearData();
                loadAchievementData();
                dialog.dismiss();
            }
        };
        handler.postDelayed(r, 100);

        hint_no = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
        if(hint_no>0){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mJobScheduler.cancelAll();
            }
        }
        if(hint_no <= 1){
            txtTotalHint.setText("សរុប ៖ "+hint_no+ " hint");
        }else{
            txtTotalHint.setText("សរុប ៖ "+hint_no+ " hints");
        }
    }

    public void checkPackage(){

        boolean medayi_installed_check = isPackageExisted("com.askhmer.lockscreen");
        boolean khmerapphelper_installed_check = isPackageExisted("com.longdy.khmerapphelper");
        boolean khmerfrog_installed_check = isPackageExisted("com.longdy.medayi.khmerfrog");
        boolean hangtom_installed_check = isPackageExisted("com.hangtom");

        boolean  medayi_installed =  sharedPreferencesFile.getBooleanSharedPreference("medayi_installed");
        boolean  khmerapphelper_installed =  sharedPreferencesFile.getBooleanSharedPreference("khmerapphelper_installed");
        boolean  khmerfrog_installed =  sharedPreferencesFile.getBooleanSharedPreference("khmerfrog_installed");
        boolean  hangtom_installed =  sharedPreferencesFile.getBooleanSharedPreference("hangtom_installed");

        if(medayi_installed_check && medayi_installed){
                int hint_number;
                hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                hint_number = hint_number + 5;
                sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",hint_number);
                sharedPreferencesFile.putBooleanSharedPreference("medayi_installed", false);
        }

        if(khmerapphelper_installed_check && khmerapphelper_installed) {
                int hint_number;
                hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                hint_number = hint_number + 5;
                sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",hint_number);
                sharedPreferencesFile.putBooleanSharedPreference("khmerapphelper_installed", false);
        }

        if(khmerfrog_installed_check && khmerfrog_installed){
                int hint_number;
                hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                hint_number = hint_number + 5;
                sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",hint_number);
                sharedPreferencesFile.putBooleanSharedPreference("khmerfrog_installed", false);
        }

        if(hangtom_installed_check && hangtom_installed){
                int hint_number;
                hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                hint_number = hint_number + 5;
                sharedPreferencesFile.putIntSharedPreference("hint_no","hint_no",hint_number);
                sharedPreferencesFile.putBooleanSharedPreference("hangtom_installed", false);
        }
    }

    //--------------check package exists
    public boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }


//    private void printHashkey() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.hangtom.ravy.khmerfinder",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//    }
}
