package com.hangtom.ravy.khmerfinder.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by soklundy on 4/20/2016.
 */
public class SharedPreferencesFile {
    public static final boolean IS_MUSIC = true;
    public static final boolean IS_SOUND = true;
    public static final boolean IS_VIBRATE= true;
    public static final int current_level = 0;
    public static final int current_level_easy = 0;
    public static final int current_level_medium = 0;
    public static final int current_level_hard= 0;
    public static final int level = 1;
    public static final int hint_no = 0;
    public static final boolean first_installed = true;

    public static final boolean medayi_installed = false;
    public static final boolean khmerapphelper_installed = false;
    public static final boolean khmerfrog_installed = false;
    public static final boolean hangtom_installed = false;
    public static final boolean jobschedule_delay = true;



    private Context mContext;
    private static SharedPreferencesFile mInstance = null;
    private SharedPreferences mSettings = null;
    private SharedPreferences.Editor mEditor = null;


    public SharedPreferencesFile(Context context, String sharedPrefName) {
        mContext = context;
        mSettings = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
    }

    public static SharedPreferencesFile newInstance(Context context, String sharedPrefName ) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesFile(context, sharedPrefName);
        }
        return mInstance;
    }

    /**
     * @param perferKey
     * @param perferValue
     */
    public void putBooleanSharedPreference(String perferKey, boolean perferValue){
        mEditor.putBoolean(perferKey, perferValue);
        mEditor.commit();
    }

    /**
     * @param perferKey
     * @param perferValue
     */
    public String putStringSharedPreference(String perferKey, String perferValue){
        mEditor.putString(perferKey, perferValue);
        mEditor.commit();
        return perferKey;
    }

    /**
     * @param perferFileName
     * @param perferKey
     * @param perferValue
     */
    public void putIntSharedPreference(String perferFileName, String perferKey, int perferValue){
        mEditor.putInt(perferKey, perferValue);
        mEditor.commit();
    }

    /**
     * @param perferKey
     * @return
     */
    public boolean getBooleanSharedPreference(String perferKey){
        return mSettings.getBoolean(perferKey, true);
    }

    /**
     * @param perferKey
     * @return
     */
    public String getStringSharedPreference(String perferKey){
        return mSettings.getString(perferKey, null);
    }

    /**
     * @param perferFileName
     * @param perferKey
     * @return
     */
    public int getIntSharedPreference(String perferFileName, String perferKey){
        return mSettings.getInt(perferKey, 0);
    }
}
