package com.example.doughpaze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.Adapters.confirmOrderAdapter;
import com.example.doughpaze.models.MyOrderResponse;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class order_details extends Activity {

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private TextView orderId,total,paymentMode_txt,transaction_ID_txt,date,address,couponText,coupon;
    private RecyclerView rvItem;
    private Button proceed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_complete_order_details);
        Intent intent=getIntent();

        mSubscriptions = new CompositeSubscription();
        orderId=findViewById(R.id.id_num);
        date=findViewById(R.id.order_date);
        total=findViewById(R.id.total);
        paymentMode_txt=findViewById(R.id.paymentMode_txt);
        transaction_ID_txt=findViewById(R.id.transaction_ID_txt);
        rvItem=findViewById(R.id.recycler_item_container);
        proceed=findViewById(R.id.proceed);
        address=findViewById(R.id.Delivery_address);
        coupon=findViewById(R.id.coupon_name);
        couponText=findViewById(R.id.couponText);

        Fetch_Details( intent.getStringExtra("id"));

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(order_details.this,MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });


    }

    private void Fetch_Details(String id) {
        progressDialog=new ProgressDialog(order_details.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .GET_ORDER_DETAILS(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(MyOrderResponse response) {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }

        orderId.setText(response.getOrderId());
        date.setText(response.getDate().toString());
        total.setText(String.valueOf(response.getTotalAmount()));
        paymentMode_txt.setText(response.getPayment_mode());
        transaction_ID_txt.setVisibility(View.GONE);
        String addres=response.getAddress().getHouse_details()+" "+response.getAddress().getAddress();
        address.setText(addres);
        if(response.getPayment_mode().equals("Online"))
        {
            transaction_ID_txt.setText(response.getPaymentDetails().getTransactionId());
            transaction_ID_txt.setVisibility(View.VISIBLE);
        }

        if(response.getCoupon_applied())
        {
            couponText.setVisibility(View.VISIBLE);
            coupon.setVisibility(View.VISIBLE);
            coupon.setText(response.getCoupon_name());
        }
        else
        {
            couponText.setVisibility(View.GONE);
            coupon.setVisibility(View.GONE);
        }


        confirmOrderAdapter cofirmOrderAdapter=new confirmOrderAdapter(response.getFinalOrderList());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvItem.setAdapter(cofirmOrderAdapter);
        rvItem.setLayoutManager(layoutManager);

    }

    private void handleError(Throwable error) {

        //mProgressBar.setVisibility(View.GONE);
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
            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }
}
