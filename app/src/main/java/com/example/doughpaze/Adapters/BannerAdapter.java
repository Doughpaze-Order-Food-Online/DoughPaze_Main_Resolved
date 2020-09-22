package com.example.doughpaze.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.doughpaze.R;
import com.example.doughpaze.models.banners;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class BannerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    private List<banners> bannersList;

    //gloabals
    final Handler handler = new Handler();
    public Timer swipeTimer ;

    public BannerAdapter(Context context, List<banners> bannersList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bannersList=bannersList;
    }


    @Override
    public int getCount() {
        if(bannersList != null){
            return bannersList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.banners_list_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        String url = "http://3.131.127.136:5000" + bannersList.get(position).getBanner_location();
                    Glide
                    .with(context)
                    .load(url)
                    .placeholder(R.drawable.image_loading)
                    .fitCenter()
                    .into(imageView);


        container.addView(itemView);
        return itemView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((LinearLayout) object);
    }

    public void setTimer(final ViewPager myPager, int time, final int numPages, final int curPage){

        final Runnable Update = new Runnable() {
            int NUM_PAGES =numPages;
            int currentPage = curPage ;
            public void run() {
                if (currentPage == NUM_PAGES ) {
                    currentPage = 0;
                }
                myPager.setCurrentItem(currentPage++, true);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, time*1000);

    }


}
