package com.example.doughpaze.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.doughpaze.R;
import com.example.doughpaze.Subcategory_main;
import com.example.doughpaze.models.Coupon;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.constants.BASE;

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.OfferListItemHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private CompositeSubscription mSubscriptions;
    private List<Coupon> myOrderResponseList;
    private SharedPreferences mSharedPreferences;
    private Context context;
    private ProgressDialog progressDialog;


    public OfferListAdapter(List<Coupon> couponList, Context context) {
        this.myOrderResponseList = couponList;
        this.context = context;

    }


    @NonNull
    @Override
    public OfferListAdapter.OfferListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_offer_list_item, parent, false);
        return new OfferListAdapter.OfferListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferListAdapter.OfferListItemHolder offerListItemHolder, int i) {
       Coupon coupon = myOrderResponseList.get(i);
        String url = BASE + coupon.getCoupon_location();

        Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.image_loading)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .into(offerListItemHolder.imageView);

        if(coupon.getTerms()!=null)
        {
            offerListItemHolder.more.setVisibility(View.VISIBLE);

        }
        else
        {
            offerListItemHolder.more.setVisibility(View.GONE);
        }

        offerListItemHolder.more.setOnClickListener(v -> {
            if(offerListItemHolder.terms.getVisibility()==View.VISIBLE)
            {
                offerListItemHolder.terms_container.setVisibility(View.GONE);
            }
            else
            {
                offerListItemHolder.terms_container.setVisibility(View.VISIBLE);
                offerListItemHolder.terms.setText(coupon.getTerms());
            }
        });

        String description="Get "+coupon.getDiscount()+"% off on "+coupon.getCategory()+" Items";
        offerListItemHolder.offer_head_description_txt.setText(description);
        offerListItemHolder.category_txt.setText(coupon.getCategory());
        if(!coupon.getCategory().equals("all"))
        {
            offerListItemHolder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(offerListItemHolder.itemView.getContext(), Subcategory_main.class);
                    intent.putExtra("category",coupon.getCategory());
                    offerListItemHolder.itemView.getContext().startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return myOrderResponseList.size();

    }

    class OfferListItemHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView offer_head_description_txt,category_txt,more,terms;
    private LinearLayout parent,terms_container;

        OfferListItemHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            offer_head_description_txt=itemView.findViewById(R.id.offer_head_description_txt);
            category_txt=itemView.findViewById(R.id.category_txt);
            parent=itemView.findViewById(R.id.parent);
            terms_container=itemView.findViewById(R.id.terms_container);
            terms=itemView.findViewById(R.id.terms);
            more=itemView.findViewById(R.id.more_btn);

        }
    }

}
