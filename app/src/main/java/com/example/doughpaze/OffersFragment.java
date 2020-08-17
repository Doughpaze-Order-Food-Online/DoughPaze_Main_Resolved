package com.example.doughpaze;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class OffersFragment extends Fragment {

    ViewPager2 viewPager2;


    public OffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_offers, container, false);

        viewPager2 = rootView.findViewById(R.id.offers_viewPager);

        viewPager2.setAdapter(new OfferFragmentStateAdapter(this));

        TabLayout offers_tabs = rootView.findViewById(R.id.offers_tabs);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                offers_tabs, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Restaurant Offers");
                } else {
                    tab.setText("Payment offers/coupons");
                }
            }
        }
        );
        tabLayoutMediator.attach();

        return rootView;
    }
}