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

    private List<Coupon> coupons;

    public CouponsFragment(List<Coupon> couponList)
    {
        this.coupons=couponList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_coupons, container, false);

        rvitem=view.findViewById(R.id.All_coupons_container);


        CouponsAdapter couponsAdapter=new CouponsAdapter(coupons,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvitem.setAdapter(couponsAdapter);
        rvitem.setLayoutManager(layoutManager);


        return view;
    }
}