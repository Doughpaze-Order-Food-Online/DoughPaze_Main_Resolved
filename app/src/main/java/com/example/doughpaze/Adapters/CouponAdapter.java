package com.example.doughpaze.Adapters;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doughpaze.R;
import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.FoodCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponItemHolder>  {

    private List<Coupon> couponList;
    private SharedPreferences mSharedPreferences;
    private finishActivity finishActivity;
    private Context context;
    private ProgressDialog progressDialog;

    public CouponAdapter(List<Coupon> list, finishActivity finishActivity,Context context) {
        this.couponList=list;
        this.finishActivity=finishActivity;
        this.context=context;
    }


    @NonNull
    @Override
    public CouponAdapter.CouponItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_item, parent, false);
        return new CouponAdapter.CouponItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponAdapter.CouponItemHolder couponItemHolder, int i) {
        Coupon coupon=couponList.get(i);

        couponItemHolder.name.setText(coupon.getCoupon_name().toUpperCase());
        String heading="Get "+coupon.getDiscount()+"% on  "+coupon.getCategory() +" items";
        couponItemHolder.description1.setText(heading);
        String heading2="Use code "+coupon.getCoupon_name() +" & get "+coupon.getDiscount()+"% discount up to Rs."+coupon.getMax_discount()+" on orders above Rs."+coupon.getMin_amount();
        couponItemHolder.description2.setText(heading2);


        couponItemHolder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(couponItemHolder.itemView.getContext());

                int total=0;
                Gson gson = new Gson();
                String cart = mSharedPreferences.getString("cart", null);
                Type type = new TypeToken<ArrayList<FoodCart>>() {
                }.getType();
                ArrayList<FoodCart> foodCart;
                foodCart = gson.fromJson(cart, type);
                assert foodCart != null;
                for(FoodCart x:foodCart)
                {
                    total+=x.getPrice()*x.getQuantity();
                }



                if(coupon.getCategory().equals("all"))
                {
                    if(total<coupon.getMin_amount())
                    {
                        alertBox("Coupon not Applied! Bill Amount should be above Rs."+coupon.getMin_amount());

                    }
                    else
                    {
                        double saving=(total*coupon.getDiscount())/100;
                        saving=saving>coupon.getMax_discount()?coupon.getMax_discount():saving;
                        int discount=coupon.getDiscount();
                        int max_discount=coupon.getMax_discount();
                        String coupon_name=coupon.getCoupon_name();
                        OfferBox(saving,coupon_name);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("discount", String.valueOf(saving));
                        editor.putString("coupon_name", String.valueOf(coupon_name));
                        editor.apply();


                    }

                }
                else {
                    int flag=0;
                    for(FoodCart x:foodCart)
                    {
                        if(x.getFood_category().equals(coupon.getCategory()))
                        {flag=1;
                            if(x.getQuantity()*x.getPrice()<coupon.getMin_amount())
                            {
                                alertBox("Coupon not Applied! "+coupon.getCategory()+" Items should Price above Rs."+coupon.getMin_amount());
                                break;
                            }
                            else
                            {
                                double saving=(total*coupon.getDiscount())/100;
                                saving=saving>coupon.getMax_discount()?coupon.getMax_discount():saving;
                                int discount=coupon.getDiscount();
                                int max_discount=coupon.getMax_discount();
                                String coupon_name=coupon.getCoupon_name();
                                OfferBox(saving,coupon_name);
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString("discount", String.valueOf(saving));
                                editor.putString("coupon_name", String.valueOf(coupon_name));
                                editor.apply();
                                break;
                            }
                        }

                    }

                    if(flag==0)
                    {
                        alertBox("Coupon not Applied! Cart doesn't contain "+coupon.getCategory()+" Items");
                    }
                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return couponList.size();

    }

    class CouponItemHolder extends RecyclerView.ViewHolder {
        private TextView name, description1, description2,more;
        private Button apply;



        CouponItemHolder (View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.coupon_name_txt);
            description1=itemView.findViewById(R.id.coupon_head_description_txt);
            description2=itemView.findViewById(R.id.description2_txt);
            more=itemView.findViewById(R.id.more_btn);
            apply=itemView.findViewById(R.id.apply_btn);

        }
    }

    private void  alertBox(String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("oK",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void OfferBox(double saving, String coupon)
    {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        final View offerView=layoutInflater.inflate(R.layout.coupon,null);

        final TextView savings=offerView.findViewById(R.id.saving);
        final TextView offername=offerView.findViewById(R.id.offer);
        String s="₹"+ saving;
        savings.setText(s);
        offername.setText(coupon);


        progressDialog=new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(offerView);

        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                finishActivity.ActivityFinish();
            }
        }.start();

    }

}
