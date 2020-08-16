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
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.MyOrderResponse;
import com.example.doughpaze.order_confirm_activity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import rx.subscriptions.CompositeSubscription;

public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.transactionItemHolder>  {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private CompositeSubscription mSubscriptions;
    private List<MyOrderResponse> myOrderResponseList;
    private SharedPreferences mSharedPreferences;






    public transactionAdapter(List<MyOrderResponse> list) {
        this.myOrderResponseList=list;

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
                editor.apply();

                Intent intent=new Intent(transactionItemHolder.rvItem.getContext(),order_confirm_activity.class);
                transactionItemHolder.rvItem.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return myOrderResponseList.size();

    }

    class transactionItemHolder extends RecyclerView.ViewHolder {
        private Button reorder;
        private TextView total;
        private RecyclerView rvItem;



        transactionItemHolder (View itemView) {
            super(itemView);
            reorder = itemView.findViewById(R.id.reorder_btn);
            total = itemView.findViewById(R.id.total);
            rvItem=itemView.findViewById(R.id.items);
        }
    }








}
