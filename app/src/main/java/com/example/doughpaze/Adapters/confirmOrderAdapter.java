package com.example.doughpaze.Adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.FoodList.SubItem;
import com.example.doughpaze.R;
import com.example.doughpaze.models.Address;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.order_confirm_activity;
import com.google.gson.Gson;

import java.util.List;

public class confirmOrderAdapter extends RecyclerView.Adapter<confirmOrderAdapter.OrderItemHolder>  {

    private List<FoodCart> list;

    public confirmOrderAdapter(List<FoodCart> list) {
        this.list=list;
    }


    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_cart_list_item, parent, false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder orderItemHolder, int i) {
        FoodCart Item  = list.get(i);
        orderItemHolder.foodname.setText(Item.getFood_name());
        orderItemHolder.price.setText(String.valueOf(Item.getQuantity()));

    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class OrderItemHolder extends RecyclerView.ViewHolder {
        TextView foodname, price, quantity;

        OrderItemHolder(View itemView) {
            super(itemView);

            foodname=(TextView)itemView.findViewById(R.id.item_name_txt);
            price=(TextView)itemView.findViewById(R.id.quantity_txt);



        }
    }


}
