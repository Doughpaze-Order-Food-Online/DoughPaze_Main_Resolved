package com.example.doughpaze.Adapters;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doughpaze.R;
import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.couponsAvailable;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponItemHolder>  {

    private List<Coupon> couponList;
    private SharedPreferences mSharedPreferences;
    private finishActivity finishActivity;
    private Context context;
    private ProgressDialog progressDialog;
    private CompositeSubscription mSubscriptions;

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
                    total+=(x.getPrice()*x.getQuantity());
                }



                if(coupon.getCategory().equals("all"))
                {
                    if(total<coupon.getMin_amount() )
                    {
                        alertBox("Coupon not Applied! Bill Amount should be above Rs."+coupon.getMin_amount());

                    }
                    else
                    {
                        double saving=(total*coupon.getDiscount())/100;
                        saving=saving>coupon.getMax_discount()?coupon.getMax_discount():saving;
                        String coupon_name=coupon.getCoupon_name();
                        CouponsUsed(saving,coupon_name,coupon.getLimit());
                    }

                }
                else {
                    int flag=0;
                    for(FoodCart x:foodCart)
                    {
                        if(x.getFood_category().equals(coupon.getCategory()))
                        {flag=1;
                            if((x.getQuantity()*x.getPrice())<coupon.getMin_amount())
                            {
                                alertBox("Coupon not Applied! "+coupon.getCategory()+" Items should Price above Rs."+coupon.getMin_amount());
                                break;
                            }
                            else
                            {
                                double saving=(total*coupon.getDiscount())/100;
                                saving=saving>coupon.getMax_discount()?coupon.getMax_discount():saving;
                                String coupon_name=coupon.getCoupon_name();
                                CouponsUsed(saving,coupon_name,coupon.getLimit());
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
            mSubscriptions = new CompositeSubscription();

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

    private void CouponsUsed(Double saving,String coupon_name,int limit)
    {

        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .CHECK_COUPON_AVAILIBILITY(mSharedPreferences.getString(constants.PHONE, null),coupon_name,saving,limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError2));
    }

    private void handleResponse2(Response response) {

        progressDialog.dismiss();
        if(response.getAvailable())
        {
            OfferBox(response.getSaving(),response.getCoupon_name());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("discount", String.valueOf(response.getSaving()));
            editor.putString("coupon_name", String.valueOf(response.getCoupon_name()));
            editor.apply();
        }
        else {
            alertBox(response.getMessage());
        }


    }

    private void handleError2(Throwable error) {

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

}
