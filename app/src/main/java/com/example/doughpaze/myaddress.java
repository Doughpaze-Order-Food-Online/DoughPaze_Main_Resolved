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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.Adapters.addressAdapter;
import com.example.doughpaze.Adapters.myaddressAdapter;
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

public class myaddress extends Activity {
    private TextView addnew;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private myaddressAdapter addressAdapter;
    private RecyclerView rvItem;
    private ImageView back;
    private Button retry;
    private LinearLayout internet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mSubscriptions = new CompositeSubscription();
        addnew = (TextView) findViewById(R.id.add_new_address);
        rvItem = findViewById(R.id.address);
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myaddress.this, saveaddress.class);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.back_btn);
        internet=findViewById(R.id.no_internet_container);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        retry=findViewById(R.id.retry_btn);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(myaddress.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_loading);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                FetchAddress();
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
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(AddressResponse response) {

        progressDialog.dismiss();
        List<Address> list = new ArrayList<>();
        list = response.getAddress();

        addressAdapter = new myaddressAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(myaddress.this);
        rvItem.setAdapter(addressAdapter);
        rvItem.setLayoutManager(layoutManager);


    }

    private void handleError(Throwable error) {
        progressDialog.dismiss();


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {


            rvItem.setVisibility(View.GONE);
            internet.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();


        FetchAddress();

    }
}
