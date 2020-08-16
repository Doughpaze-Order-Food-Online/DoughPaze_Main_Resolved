package com.example.doughpaze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.Adapters.addressAdapter;
import com.example.doughpaze.Adapters.transactionAdapter;
import com.example.doughpaze.models.Address;
import com.example.doughpaze.models.AddressResponse;
import com.example.doughpaze.models.FinalOrder;
import com.example.doughpaze.models.MyOrderResponse;
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

public class my_transaction_activity extends Activity {

    private TextView addnew;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private RecyclerView rvItem;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_transactions);

        mSubscriptions = new CompositeSubscription();
        rvItem = findViewById(R.id.orders_container);
        back=findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        FetchTransaction();

    }

    private void FetchTransaction() {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .GET_TRANSACTIONS(mSharedPreferences.getString(constants.PHONE, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(List<MyOrderResponse> finalOrder) {

        progressDialog.dismiss();
        transactionAdapter transactionAdapter=new transactionAdapter(finalOrder);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvItem.setAdapter(transactionAdapter);
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
}
