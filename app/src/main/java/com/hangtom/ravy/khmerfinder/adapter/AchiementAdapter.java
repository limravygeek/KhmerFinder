package com.hangtom.ravy.khmerfinder.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.model.Achievement;
import com.hangtom.ravy.khmerfinder.model.Game;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Ravy on 1/20/2017.
 */

public class AchiementAdapter extends RecyclerView.Adapter<AchiementAdapter.MyViewHolder> {

    private List<Achievement> achiementList;
    private Context context;
    public  SharedPreferences prefs;
    private SharedPreferencesFile sharedPreferencesFile;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView app_logo;
        public TextView app_name;
        public TextView hint_number;
        public Button btnInstall;


        public MyViewHolder(View view) {
            super(view);
            app_logo = (ImageView) view.findViewById(R.id.app_logo);
            app_name = (TextView) view.findViewById(R.id.txtApp_name);
            hint_number = (TextView) view.findViewById(R.id.txtHint_number);
            btnInstall = (Button) view.findViewById(R.id.btnInstall);

            Typeface font = Typeface.createFromAsset(context.getAssets(), "kh_kulen.TTF");
            app_name.setTypeface(font);
        }
    }

    public AchiementAdapter(List<Achievement> achievementList) {
        this.achiementList = achievementList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_list_row, parent, false);

        context = itemView.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Achievement achievement = achiementList.get(position);
        int img = achievement.getApp_logo();
        String app_name = achievement.getApp_name();
        int hint_number = achievement.getHint_number();
        holder.app_name.setText(app_name);
        holder.hint_number.setText(hint_number+" hints");
       Picasso.with(context)
               .load(img)
               .fit()
               .centerCrop()
               .into(holder.app_logo);

        if(achievement.isApp()){
            holder.app_name.setText("ទាញយក "+app_name);
            if(achievement.isInstalled()){
                holder.btnInstall.setBackgroundResource(R.drawable.btn_bg_installed);
            }else{
                holder.btnInstall.setBackgroundResource(R.drawable.btn_bg_install);
            }
        }else{
            holder.app_name.setText("ចែករំលែកទៅកាន់ "+app_name);
            holder.btnInstall.setBackgroundResource(R.drawable.btn_bg_share);
        }

        if(achievement.isInstalled()) {
            PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            // myLayout.getBackground().setColorFilter(greyFilter);
            holder.app_logo.setColorFilter(greyFilter);
            holder.btnInstall.getBackground().setColorFilter(greyFilter);
            holder.app_name.setTextColor(0xff777777);
            holder.hint_number.setTextColor(0xff777777);
        }

    }

    @Override
    public int getItemCount() {
        return achiementList.size();
    }

    public void clearData() {
        this.achiementList.clear();
        this.notifyDataSetChanged();
    }

    public void removeAt(int position) {
        achiementList.remove(position);
        notifyItemRemoved(position);
    }

}

