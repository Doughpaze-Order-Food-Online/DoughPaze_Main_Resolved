package com.example.doughpaze.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.doughpaze.R;
import com.example.doughpaze.models.Coupon;
import java.util.List;



public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.CouponsItemHolder> {
    private List<Coupon> myOrderResponseList;
    private Context context;



    public CouponsAdapter(List<Coupon> couponList, Context context) {
        this.myOrderResponseList = couponList;
        this.context = context;

    }


    @NonNull
    @Override
    public CouponsAdapter.CouponsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_offer_list_item, parent, false);
        return new CouponsAdapter.CouponsItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponsAdapter.CouponsItemHolder couponsItemHolder, int i) {

        Coupon coupon=myOrderResponseList.get(i);
        String url="https://doughpaze.ddns.net"+coupon.getCoupon_location();

        Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.image_loading)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .into(couponsItemHolder.imageView);

        String description="Get "+coupon.getDiscount()+"% off on "+coupon.getCategory()+" Items";
        couponsItemHolder.offer_head_description_txt.setText(description);
        couponsItemHolder.category_txt.setText(coupon.getCategory());

    }

    @Override
    public int getItemCount() {
        return myOrderResponseList.size();

    }

    class CouponsItemHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView offer_head_description_txt,category_txt;


        CouponsItemHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            offer_head_description_txt=itemView.findViewById(R.id.offer_head_description_txt);
            category_txt=itemView.findViewById(R.id.category_txt);
        }
    }

}

