package com.example.doughpaze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.banners;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;



public class logo_splash extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.logo_splash);
        mSubscriptions = new CompositeSubscription();


        Runnable runnable = () -> {

            Intent i = new Intent(logo_splash.this, MainActivity.class);
            startActivity(i);
            finish();
        };

        Handler handler = new Handler();
        int TIME_OUT = 2000;
        handler.postDelayed(runnable, TIME_OUT);
        FETCH_IMAGES();
    }

    private void FETCH_IMAGES() {

        mSubscriptions.addAll(networkUtils.getRetrofit()
                 .BANNERS()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError),
                networkUtils.getRetrofit()
                        .Coupons()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse2,this::handleError));
    }

    private void handleResponse(List<banners> response) {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        Gson gson = new Gson();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String banner = gson.toJson(response);
        editor.putString("banner",banner);
        editor.apply();


    }


    private void handleResponse2(List<Coupon> response) {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        Gson gson = new Gson();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String banner = gson.toJson(response);
        editor.putString("coupon",banner);
        editor.apply();


    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
    }
}
