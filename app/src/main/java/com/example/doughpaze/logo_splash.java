package com.example.doughpaze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doughpaze.models.Images;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;

import java.io.Serializable;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class logo_splash extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_splash);
        mSubscriptions = new CompositeSubscription();


        FETCH_IMAGES();
    }

    private void FETCH_IMAGES() {

        mSubscriptions.add(networkUtils.getRetrofit().IMAGES()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Images response) {

        Intent intent=new Intent(logo_splash.this,MainActivity.class);
        intent.putExtra("banner",(Serializable)response.getBannersList());
        intent.putExtra("coupons",(Serializable)response.getCouponsList());
        startActivity(intent);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        Gson gson = new Gson();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String banner = gson.toJson(response.getBannersList());
        String coupon=gson.toJson(response.getCouponsList());
        editor.putString("banner",banner);
        editor.putString("coupon",coupon);
        editor.apply();
        startActivity(intent);
        finish();

    }

    private void handleError(Throwable error) {

        Intent intent=new Intent(logo_splash.this,MainActivity.class);
        startActivity(intent);
        finish();

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
            Log.e("error",error.toString());
            Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();

        }
    }
}
