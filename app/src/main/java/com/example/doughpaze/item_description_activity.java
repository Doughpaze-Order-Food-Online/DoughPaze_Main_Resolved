package com.example.doughpaze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.doughpaze.FoodList.SubItem;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;
import java.util.Objects;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class item_description_activity extends Activity {

    Intent i;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private ImageView cartImage;
    private TextView name,price, description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_description);
        i=getIntent();
        mSubscriptions = new CompositeSubscription();

        name=findViewById(R.id.item_name);
        price=findViewById(R.id.item_price_txt);
        description=findViewById(R.id.item_description);
        cartImage=findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        Fetch_Details(i.getStringExtra("id"));
    }

    private void Fetch_Details(String id) {
        mSubscriptions.add(networkUtils.getRetrofit()
                .GET_ITEM_DETAILS(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(SubItem response) {


        progressDialog.dismiss();
        name.setText(response.getFood_name());
        price.setText(String.valueOf(response.getPrice()));
        String url="https://doughpaze.ddns.net"+response.getFood_image();

        Glide
        .with(this)
        .load(url)
        .placeholder(R.drawable.image_loading)
        .centerInside()
        .into(cartImage);


    }

    private void handleError(Throwable error) {
        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                com.example.doughpaze.models.Response response = gson.fromJson(errorBody, Response.class);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();

        }
    }
}
