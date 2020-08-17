package com.example.doughpaze;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OfferFragmentStateAdapter extends FragmentStateAdapter {

    public OfferFragmentStateAdapter(@NonNull OffersFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new OfferListFragment();
        } else {
            return new CouponsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
