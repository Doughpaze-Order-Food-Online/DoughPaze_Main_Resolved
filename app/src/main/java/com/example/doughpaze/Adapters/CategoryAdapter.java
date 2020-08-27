package com.example.doughpaze.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.doughpaze.R;
import com.example.doughpaze.models.Category;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryItemHolder>  {

    private List<Category> list;

    public CategoryAdapter(List<Category> list) {
            this.list=list;
    }


    @NonNull
    @Override
    public CategoryAdapter.CategoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_picks_list_item, parent, false);
        return new CategoryAdapter.CategoryItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryItemHolder couponItemHolder, int i) {
        Category category=list.get(i);
        couponItemHolder.name.setText(category.getName());

                Glide
                .with(couponItemHolder.itemView.getContext())
                .load(category.getDrawable())
                .thumbnail(Glide.with(couponItemHolder.itemView.getContext()).load(R.drawable.loading2))
                .centerInside()
                .into(couponItemHolder.image);






    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class CategoryItemHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private ImageView image;


        CategoryItemHolder (View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            image=itemView.findViewById(R.id.image);



        }
    }











}
