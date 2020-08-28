package com.example.doughpaze;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;



public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootom_navigation_container);
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.dashboard_icon, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new HomeFragment()).commit();
        bottomMenu();
    }

    @Override
    public void onBackPressed() {
        int menuItemId = chipNavigationBar.getSelectedItemId();
        if (menuItemId != R.id.dashboard_icon) {
            chipNavigationBar.setItemSelected(R.id.dashboard_icon, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container, new HomeFragment()).addToBackStack("HomeFragment").commit();
//            Intent intent=new Intent(MainActivity.this, MainActivity.class);
//            startActivity(intent);
        }
        super.onBackPressed();
    }

    private void bottomMenu() {


        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.dashboard_icon:
                        fragment = new HomeFragment();
                        break;
                    case R.id.offers_icon:
                        fragment = new OffersFragment();
                        break;
                    case R.id.cart_icon:

                        fragment = new CartFragment();
                        break;
                    case R.id.account_icon:
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(MainActivity.this);
                        fragment = sharedPreferences.getString("token", null) == null ?
                                new AccountFragment() :
                                new ProfileFragment();
                        break;
                }
                assert fragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_container,fragment).commit();
            }
        });


    }
}