package com.example.doughpaze.FoodList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Item> itemList;
    private String category;
    private Cart_Quantity cart_quantity;

    public ItemAdapter(List<Item> itemList,String category, Cart_Quantity cart_quantity) {
        this.itemList = itemList;
        this.category=category;
        this.cart_quantity=cart_quantity;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item ,parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        Item item = itemList.get(i);
            itemViewHolder.tvItemTitle.setText(item.getItemTitle());



        GridLayoutManager gridLayoutManager=new GridLayoutManager(itemViewHolder.rvSubItem.getContext(),2);

        // Create sub item view adapter
        SubItemAdapter subItemAdapter = new SubItemAdapter(item.getSubItemList(),category,cart_quantity);

        itemViewHolder.rvSubItem.setLayoutManager(gridLayoutManager);
        itemViewHolder.rvSubItem.setAdapter(subItemAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);
        itemViewHolder.rvSubItem.setNestedScrollingEnabled(true);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle;
        private RecyclerView rvSubItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.subCategory_txt);
            rvSubItem = itemView.findViewById(R.id.subcategories_item_container);
        }
    }
}
