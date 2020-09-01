package com.example.doughpaze;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.doughpaze.models.Coupon;

import java.util.List;

public class OfferFragmentStateAdapter extends FragmentStateAdapter {

    List<Coupon> coupon;
    public OfferFragmentStateAdapter(@NonNull OffersFragment fragmentActivity, List<Coupon> coupon) {
        super(fragmentActivity);
        this.coupon=coupon;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new OfferListFragment(coupon);
        } else {
            return new CouponsFragment(coupon);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
