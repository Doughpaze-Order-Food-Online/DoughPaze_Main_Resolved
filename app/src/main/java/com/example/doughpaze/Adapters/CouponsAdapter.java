package com.example.doughpaze.Adapters;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.doughpaze.R;
import com.example.doughpaze.models.Coupon;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.CouponsItemHolder> {
    private List<Coupon> myOrderResponseList;
    private Context context;
    private ClipboardManager myClipboard;
    private ClipData myClip;



    public CouponsAdapter(List<Coupon> couponList, Context context) {
        this.myOrderResponseList = couponList;
        this.context = context;

    }


    @NonNull
    @Override
    public CouponsAdapter.CouponsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_item, parent, false);
        return new CouponsAdapter.CouponsItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponsAdapter.CouponsItemHolder couponsItemHolder, int i) {

        Coupon coupon=myOrderResponseList.get(i);
        couponsItemHolder.name.setText(coupon.getCoupon_name().toUpperCase());
        String heading="Get "+coupon.getDiscount()+"% on  "+coupon.getCategory() +" items";
        couponsItemHolder.description1.setText(heading);
        String heading2="Use code "+coupon.getCoupon_name() +" & get "+coupon.getDiscount()+"% discount up to Rs."+coupon.getMax_discount()+" on orders above Rs."+coupon.getMin_amount();
        couponsItemHolder.description2.setText(heading2);

        if(coupon.getTerms()!=null)
        {
            couponsItemHolder.more.setVisibility(View.VISIBLE);
        }
        else
        {
            couponsItemHolder.more.setVisibility(View.GONE);

        }

        couponsItemHolder.more.setOnClickListener(v -> {
            if(couponsItemHolder.terms.getVisibility()==View.VISIBLE)
            {
                couponsItemHolder.terms_container.setVisibility(View.GONE);
            }
            else
            {
                couponsItemHolder.terms_container.setVisibility(View.VISIBLE);
                couponsItemHolder.terms.setText(coupon.getTerms());
            }
        });

        couponsItemHolder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                String text;
                text = coupon.getCoupon_name();

                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Coupon Copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return myOrderResponseList.size();

    }

    class CouponsItemHolder extends RecyclerView.ViewHolder {
        private TextView name, description1, description2,more,terms;
        private Button apply;
        private LinearLayout terms_container;


        CouponsItemHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.coupon_name_txt);
            description1=itemView.findViewById(R.id.coupon_head_description_txt);
            description2=itemView.findViewById(R.id.description2_txt);
            more=itemView.findViewById(R.id.more_btn);
            apply=itemView.findViewById(R.id.apply_btn);
            apply.setText("Copy to Clipboard");
            terms_container=itemView.findViewById(R.id.terms_container);
            terms=itemView.findViewById(R.id.terms);
        }
    }

    private void setClipboard(String text)
    {
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.HONEYCOMB)
        {

        }
    }

}

