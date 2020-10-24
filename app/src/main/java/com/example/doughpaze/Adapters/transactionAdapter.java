package com.example.doughpaze.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doughpaze.R;;
import com.example.doughpaze.address_activity;
import com.example.doughpaze.models.Address;
import com.example.doughpaze.models.AddressResponse;
import com.example.doughpaze.models.FinalOrder;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.MyOrderResponse;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.order_confirm_activity;
import com.example.doughpaze.utils.constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.transactionItemHolder>  {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private CompositeSubscription mSubscriptions;
    private List<MyOrderResponse> myOrderResponseList;
    private SharedPreferences mSharedPreferences;
    private Context context;
    private ProgressDialog progressDialog;
    private refresh refresh;







    public transactionAdapter(List<MyOrderResponse> list,Context context,refresh refresh) {
        this.myOrderResponseList=list;
        this.context=context;
        this.refresh=refresh;

    }


    @NonNull
    @Override
    public transactionAdapter.transactionItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_item, parent, false);
        return new transactionAdapter.transactionItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull transactionAdapter.transactionItemHolder transactionItemHolder, int i) {
        MyOrderResponse myOrderResponse=myOrderResponseList.get(i);
        transactionItemHolder.total.setText(String.valueOf(myOrderResponse.getTotalAmount()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(transactionItemHolder.rvItem.getContext());
        confirmOrderAdapter confirmOrderAdapter=new confirmOrderAdapter(myOrderResponse.getFinalOrderList());
        transactionItemHolder.rvItem.setAdapter(confirmOrderAdapter);
        transactionItemHolder.rvItem.setLayoutManager(layoutManager);

        String address=myOrderResponse.getAddress().getHouse_details()+" " +myOrderResponse.getAddress().getAddress();
        transactionItemHolder.address.setText(address);
        transactionItemHolder.status.setText(myOrderResponse.getOrder_status());
        transactionItemHolder.payment_mode.setText(myOrderResponse.getPayment_mode());
        transactionItemHolder.orderId.setText(myOrderResponse.getOrderId());
        transactionItemHolder.date.setText(myOrderResponse.getDate().toString());

        if(myOrderResponse.getPayment_mode().equals("Online") && myOrderResponse.getPaymentDetails()!=null)
        {
            transactionItemHolder.tid.setVisibility(View.VISIBLE);

                transactionItemHolder.tid.setText(myOrderResponse.getPaymentDetails().getTransactionId());

            transactionItemHolder.tidtext.setVisibility(View.VISIBLE);
        }
        else
        {
            transactionItemHolder.tid.setVisibility(View.GONE);
            transactionItemHolder.tidtext.setVisibility(View.GONE);
        }

        if(myOrderResponse.getCoupon_applied())
        {
            transactionItemHolder.coupon.setText(myOrderResponse.getCoupon_name());
        }
        else
        {
            transactionItemHolder.coupon.setVisibility(View.GONE);
            transactionItemHolder.couponText.setVisibility(View.GONE);
        }



        Date d1=new Date();
        long diff= (d1.getTime()-myOrderResponse.getDate().getTime())/1000;

        if(myOrderResponse.getPayment_status() && diff<=1800)
        {
            transactionItemHolder.reorder.setVisibility(View.GONE);
            transactionItemHolder.cancel.setVisibility(View.VISIBLE);
            transactionItemHolder.check.setVisibility(View.GONE);
        }
        else if(myOrderResponse.getOrder_status().trim().toLowerCase().equals("Payment Initiated".toLowerCase().trim()) || myOrderResponse.getOrder_status().trim().toLowerCase().equals("PENDING".toLowerCase().trim()))
        {
            transactionItemHolder.reorder.setVisibility(View.GONE);
            transactionItemHolder.cancel.setVisibility(View.GONE);
            transactionItemHolder.check.setVisibility(View.VISIBLE);
        }
        else
        {
            transactionItemHolder.reorder.setVisibility(View.VISIBLE);
            transactionItemHolder.cancel.setVisibility(View.GONE);
            transactionItemHolder.check.setVisibility(View.GONE);
        }

        transactionItemHolder.check.setOnClickListener(v -> {
            mSharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);

            FinalOrder final_order=new FinalOrder();

            String token = mSharedPreferences.getString(constants.TOKEN, null);
            mSubscriptions.add(networkUtils.getRetrofit(token)
                    .PLACE_ONLINE_ORDER(final_order,constants.MID,myOrderResponse.getOrderId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse_COD,this::handleError));

        });


        transactionItemHolder.reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<FoodCart> foodCart=new ArrayList<>();
                foodCart=myOrderResponse.getFinalOrderList();
                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(transactionItemHolder.rvItem.getContext());

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                Gson gson = new Gson();
                String cart = gson.toJson(foodCart);
                editor.putString("cart",cart);
                String address = gson.toJson(myOrderResponse.getAddress());
                editor.putString("address",address);
                editor.putString("reorder","yes");
                editor.apply();

                Intent intent=new Intent(transactionItemHolder.rvItem.getContext(),order_confirm_activity.class);
                transactionItemHolder.rvItem.getContext().startActivity(intent);

            }
        });

        transactionItemHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(transactionItemHolder.itemView.getContext())
                        .setTitle("Cancel Order")
                        .setMessage("Are you sure you want to cancel order??")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                progressDialog=new ProgressDialog(context);
                                progressDialog.show();
                                progressDialog.setContentView(R.layout.progress_loading);
                                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                               if(myOrderResponse.getPayment_mode().equals("Online"))
                               {
                                   CANCEL_ONLINE_ORDER(myOrderResponse.get_id(),myOrderResponse.getOrderId(),
                                           myOrderResponse.getPaymentDetails().getTransactionId(),myOrderResponse.getTotalAmount());
                               }
                               else
                               {
                                   CANCEL_OFFLINE_ORDER(myOrderResponse.get_id(),myOrderResponse.getOrderId());
                               }

                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myOrderResponseList.size();

    }

    private void handleResponse_COD(Response response) {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
        refresh.FETCH_AGAIN();
        Toast.makeText(context, "Status Updated! Refreshing", Toast.LENGTH_SHORT).show();

    }

    class transactionItemHolder extends RecyclerView.ViewHolder {
        private Button reorder;
        private TextView total,orderId,date,status,payment_mode,tid,address,coupon,tidtext,couponText;
        private RecyclerView rvItem;
        private Button cancel,check;



        transactionItemHolder (View itemView) {
            super(itemView);
            reorder = itemView.findViewById(R.id.reorder_btn);
            total = itemView.findViewById(R.id.total);
            rvItem=itemView.findViewById(R.id.items);
            cancel=itemView.findViewById(R.id.cancel_btn) ;
            orderId=itemView.findViewById(R.id.id_num);
            date=itemView.findViewById(R.id.order_date);
            status=itemView.findViewById(R.id.status_value_txt);
            payment_mode=itemView.findViewById(R.id.paymentMode_txt);
            tid=itemView.findViewById(R.id.transaction_ID_txt);
            coupon=itemView.findViewById(R.id.coupon_name);
            address=itemView.findViewById(R.id.delivery);
            mSubscriptions = new CompositeSubscription();
            tidtext=itemView.findViewById(R.id.transaction_txt);
            couponText=itemView.findViewById(R.id.couponText);
            check=itemView.findViewById(R.id.check_status_btn);

        }
    }

    private void CANCEL_ONLINE_ORDER(String id,String orderId,String trasnsactionId,Double amount)
    {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String token = mSharedPreferences.getString(constants.TOKEN, null);
        mSubscriptions.add(networkUtils.getRetrofit(token)
                .CANCEL_ONLINE_ORDER(id,orderId,constants.MID,trasnsactionId,amount,GENERATE_ID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void CANCEL_OFFLINE_ORDER(String id,String orderId)
    {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String token = mSharedPreferences.getString(constants.TOKEN, null);
        mSubscriptions.add(networkUtils.getRetrofit(token)
                .CANCEL_OFFLINE_ORDER(id,orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(Response response) {
        progressDialog.dismiss();
        Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();

    }

    private void handleError(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
            Log.e("error",error.toString());

        }
    }




    private String GENERATE_ID()
    {
        String ts=String.valueOf(System.currentTimeMillis());
        String rand= UUID.randomUUID().toString();
        return ts;
    }



}
