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

import com.example.doughpaze.Adapters.CouponAdapter;
import com.example.doughpaze.Adapters.OfferListAdapter;
import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class OfferListFragment extends Fragment {

    private RecyclerView rvitem;


    private List<Coupon> coupons;

    public OfferListFragment(List<Coupon> couponList)
    {
        this.coupons=couponList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_offer_list, container, false);
        rvitem=view.findViewById(R.id.All_offers_container);
        OfferListAdapter offerListAdapter=new OfferListAdapter(coupons,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvitem.setAdapter(offerListAdapter);
        rvitem.setLayoutManager(layoutManager);
        return  view;

    }





}