package com.example.doughpaze;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.doughpaze.Adapters.BannerAdapter;
import com.example.doughpaze.Adapters.CouponImageAdpter;
import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.banners;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager, viewPager2;
    private BannerAdapter bannerAdapter;
    private CouponImageAdpter couponImageAdpter;
    private List<banners> bannersList;
    private List<Coupon> CouponsList;
    private DrawerLayout drawer;
    private Button button;
    private CardView cake, pizza,donut,pasta,garlic_bread,mocktail,nachos,brownies;
    private NavigationView navigationView;
    private TextView quantity;
    private SharedPreferences mSharedPreferences;
    private ImageView cart_Img;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent= Objects.requireNonNull(getActivity()).getIntent();

        bannersList= (List<banners>) intent.getSerializableExtra("banner");
        CouponsList= (List<Coupon>) intent.getSerializableExtra("coupons");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main, container, false);

        initView(view);




        return view;

    }

    private void initView(View v){
        viewPager = (ViewPager) v.findViewById(R.id.banners_container);
        viewPager2 = (ViewPager) v.findViewById(R.id.offers_container);

        //side navigation bar
        drawer = v.findViewById(R.id.drawer_layout);
        button = v.findViewById(R.id.menu_button);
        cart_Img=v.findViewById(R.id.cart_Img);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });


        bannerAdapter=new BannerAdapter(Objects.requireNonNull(getContext()),bannersList);
        couponImageAdpter=new CouponImageAdpter(getContext(),CouponsList);
        viewPager.setAdapter(bannerAdapter);
        viewPager2.setAdapter(couponImageAdpter);

        cake=(CardView)v.findViewById(R.id.cakes_btn) ;
        pizza=(CardView)v.findViewById(R.id.pizza_btn) ;
        donut=(CardView)v.findViewById(R.id.donuts_btn) ;
        nachos=(CardView)v.findViewById(R.id.nachos_btn) ;;
        mocktail=(CardView)v.findViewById(R.id.mocktail_btn) ;
        brownies=(CardView)v.findViewById(R.id.brownie_btn) ;
        garlic_bread=(CardView)v.findViewById(R.id.garlic_btn) ;
        pasta=(CardView)v.findViewById(R.id.pasta_btn) ;
        navigationView=(NavigationView) v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        quantity = v.findViewById(R.id.cart_fill_update_txt);

        //categories
        cake.setOnClickListener(view-> FOOD_LIST_VIEW("Cake") );
        pizza.setOnClickListener(view-> FOOD_LIST_VIEW("Pizza") );
        donut.setOnClickListener(view-> FOOD_LIST_VIEW("Donut") );
        nachos.setOnClickListener(view-> FOOD_LIST_VIEW("Nachos") );
        mocktail.setOnClickListener(view-> FOOD_LIST_VIEW("Mocktail") );
        brownies.setOnClickListener(view-> FOOD_LIST_VIEW("Brownie") );
        garlic_bread.setOnClickListener(view -> FOOD_LIST_VIEW("Garlic Breads"));
        pasta.setOnClickListener(view -> FOOD_LIST_VIEW("Pasta"));

        cart_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CartActivity.class);
                startActivity(i);
            }
        });
        try {
            updateCartQuantity();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    private void FOOD_LIST_VIEW(String category)
    {
        Intent intent=new Intent(getActivity(),Subcategory_main.class);
        intent.putExtra("category",category);
        startActivity(intent);
    }







    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()){
            case R.id.feedback:
                intent=new Intent(getContext(),feedBack_Activity.class);
                break;
            case R.id.terms_policies:
                intent=new Intent(getContext(),TermsAndPoliciesActivity.class);
                break;
            case R.id.help_support:
                intent=new Intent(getContext(),HelpAndSupportActivity.class);
                break;
        }

        startActivity(intent);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateCartQuantity()
    {mSharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String cart=mSharedPreferences.getString("cart", null);

        Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
        List<FoodCart> newfoodCarts=new ArrayList<>();
        newfoodCarts=gson.fromJson(cart,type);

        int qquantity=0;
        assert newfoodCarts != null;
        for(FoodCart x:newfoodCarts)
        {
            qquantity+=x.getQuantity();
        }
        if(qquantity==0)
        {
            quantity.setVisibility(View.GONE);
        }
        else
        {
            quantity.setVisibility(View.VISIBLE);
            quantity.setText(String.valueOf(qquantity));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateCartQuantity();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}