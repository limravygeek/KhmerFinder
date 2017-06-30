package com.hangtom.ravy.khmerfinder.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hangtom.ravy.khmerfinder.R;
import com.hangtom.ravy.khmerfinder.model.Game;
import com.hangtom.ravy.khmerfinder.util.SharedPreferencesFile;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ravy on 1/20/2017.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.MyViewHolder> {

    private List<Game> gameList;
    private int[] arrRate;
    private Context context;
    private int level;
    public  SharedPreferences prefs;
    private SharedPreferencesFile sharedPreferencesFile;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image,img_lock,img_rate;


        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            img_lock = (ImageView) view.findViewById(R.id.img_lock);
            img_rate = (ImageView) view.findViewById(R.id.img_rate);
        }
    }

    public GameAdapter(List<Game> gameList) {
        this.gameList = gameList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_list_row, parent, false);

        context = itemView.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Game game = gameList.get(position);
        int img = game.getImage();

       Picasso.with(context)
               .load(img)
               .fit()
               .centerCrop()
               .into(holder.image);

//        Glide.with(holder.image.getContext())
//                .load(img)
//                .thumbnail( 0.1f )
//                .override(300, 258)
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.placeholder)
//                .into(holder.image);
        try {
            sharedPreferencesFile = SharedPreferencesFile.newInstance(context, "current_level");
            level = sharedPreferencesFile.getIntSharedPreference("level","level");

            if (level==1){
                prefs = context.getSharedPreferences("RATE_EASY", context.MODE_PRIVATE);
            }else if(level==2){
                prefs = context.getSharedPreferences("RATE_MEDIUM", context.MODE_PRIVATE);
            }else{
                 prefs = context.getSharedPreferences("RATE_HARD", context.MODE_PRIVATE);
            }

            int count = prefs.getInt("Count", 0);
        arrRate = new int[70];
        for (int i = 0; i < position+1; i++){
            arrRate[i] = prefs.getInt("IntValue_"+ i, i);

            if(arrRate[i]==400){
                Resources res = context.getResources();
                Drawable draw = res.getDrawable(R.drawable.rate4);
                holder.img_rate.setImageDrawable(draw);
            }else if(arrRate[i]==300){
                Resources res = context.getResources();
                Drawable draw = res.getDrawable(R.drawable.rate3);
                holder.img_rate.setImageDrawable(draw);
            }else if(arrRate[i]==200){
                Resources res = context.getResources();
                Drawable draw = res.getDrawable(R.drawable.rate2);
                holder.img_rate.setImageDrawable(draw);
            }else if(arrRate[i]==100){
                Resources res = context.getResources();
                Drawable draw = res.getDrawable(R.drawable.rate1);
                holder.img_rate.setImageDrawable(draw);
            }else{
                Resources res = context.getResources();
                Drawable draw = res.getDrawable(R.drawable.rate0);
                holder.img_rate.setImageDrawable(draw);
            }
        }

        }catch (NullPointerException e){
            e.printStackTrace();
        }


        //----------------------------------
        holder.img_lock.setVisibility(View.GONE);
        holder.img_rate.setVisibility(View.VISIBLE);
        if(game.isLock()){
            holder.img_rate.setVisibility(View.GONE);
            holder.img_lock.setVisibility(View.VISIBLE);
        }else{
            holder.img_lock.setVisibility(View.GONE);
            holder.img_rate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void clearData() {
        this.gameList.clear();
        this.notifyDataSetChanged();
    }

    public void removeAt(int position) {
        gameList.remove(position);
        notifyItemRemoved(position);
    }

}

