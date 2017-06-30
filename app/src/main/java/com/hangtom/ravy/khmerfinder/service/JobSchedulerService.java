package com.hangtom.ravy.khmerfinder.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;

/**
 * Created by Ravy on 5/3/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private Handler mJobHandler = new Handler( new Handler.Callback() {
        private SharedPreferencesFile sharedPreferencesFile;
        @Override
        public boolean handleMessage( Message msg ) {
            sharedPreferencesFile = SharedPreferencesFile.newInstance(getApplicationContext(), "");
            boolean  jobschedule_delay = sharedPreferencesFile.getBooleanSharedPreference("jobschedule_delay");
            if(!jobschedule_delay) {
              //  Toast.makeText(JobSchedulerService.this, "true", Toast.LENGTH_SHORT).show();
                int hint_number = sharedPreferencesFile.getIntSharedPreference("hint_no","hint_no");
                if(hint_number==0) {
                   // Toast.makeText( getApplicationContext(), "Working", Toast.LENGTH_SHORT ).show();
                    sharedPreferencesFile.putIntSharedPreference("hint_no", "hint_no", 1);
                    sharedPreferencesFile.putBooleanSharedPreference("jobschedule_delay", true);
                }
            }else{
                //Toast.makeText(JobSchedulerService.this, "false", Toast.LENGTH_SHORT).show();
                sharedPreferencesFile.putBooleanSharedPreference("jobschedule_delay", false);
            }
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }

    });

    @Override
    public boolean onStartJob(JobParameters params ) {
        mJobHandler.sendMessage( Message.obtain( mJobHandler, 1, params ) );
        return true;
    }

    @Override
    public boolean onStopJob( JobParameters params ) {
        mJobHandler.removeMessages( 1 );
        return false;
    }

}
