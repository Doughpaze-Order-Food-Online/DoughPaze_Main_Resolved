package com.example.doughpaze;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.doughpaze.Adapters.CouponsAdapter;
import com.example.doughpaze.Adapters.OfferListAdapter;
import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class CouponsFragment extends Fragment {

    private RecyclerView rvitem;
    private CompositeSubscription mSubscriptions;
    private ProgressDialog progressDialog;
    private List<Coupon> coupons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_coupons, container, false);

        rvitem=view.findViewById(R.id.All_coupons_container);
        mSubscriptions = new CompositeSubscription();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        FETCH_COUPONS();
        return view;
    }

    private void FETCH_COUPONS() {

        mSubscriptions.add(networkUtils.getRetrofit()
                .GET_COUPONS()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(List<Coupon> response) {
        List<Coupon> newresponse=new ArrayList<>();
        progressDialog.dismiss();

        Date d1=new Date();

        for(Coupon x:response)
        { long diff= (d1.getTime()-x.getExpiry().getTime())/1000;
            if(diff<0)
            {
                newresponse.add(x);
            }
        }

        coupons=newresponse;
        CouponsAdapter couponsAdapter=new CouponsAdapter(newresponse,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvitem.setAdapter(couponsAdapter);
        rvitem.setLayoutManager(layoutManager);


    }

    private void handleError(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Network Error!", Toast.LENGTH_SHORT).show();
            Log.e("error",error.toString());

        }
    }
}