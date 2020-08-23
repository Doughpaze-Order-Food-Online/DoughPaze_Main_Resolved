package com.example.doughpaze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.Adapters.addressAdapter;
import com.example.doughpaze.Adapters.finishActivity;
import com.example.doughpaze.FoodList.ItemAdapter;
import com.example.doughpaze.models.Address;
import com.example.doughpaze.models.AddressResponse;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class address_activity extends Activity implements finishActivity{
    private TextView addnew;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private addressAdapter addressAdapter;
    private RecyclerView rvItem;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mSubscriptions = new CompositeSubscription();
        addnew=(TextView)findViewById(R.id.add_new_address);
        rvItem = findViewById(R.id.address);
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(address_activity.this,location_activity.class);
                startActivity(intent);
            }
        });

        back=findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        FetchAddress();
    }


    private void FetchAddress() {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);


        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .GET_ADDRESS(mSharedPreferences.getString(constants.PHONE, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(AddressResponse response) {

        progressDialog.dismiss();
        List<Address> list=new ArrayList<>();
        list=response.getAddress();

        addressAdapter = new addressAdapter(list,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(address_activity.this);
        rvItem.setAdapter(addressAdapter);
        rvItem.setLayoutManager(layoutManager);



    }

    private void handleError(Throwable error) {
        progressDialog.dismiss();


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();
            Log.e("error",error.toString());

        }
    }



    @Override
    protected void onResume() {
        super.onResume();


        FetchAddress();

    }

    @Override
    public void ActivityFinish() {
        finish();
    }
}
