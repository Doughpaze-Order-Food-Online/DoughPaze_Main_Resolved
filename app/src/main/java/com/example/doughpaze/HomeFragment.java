package com.example.doughpaze;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.doughpaze.Adapters.BannerAdapter;
import com.example.doughpaze.Adapters.CategoryAdapter;
import com.example.doughpaze.Adapters.CouponImageAdpter;
import com.example.doughpaze.models.Category;
import com.example.doughpaze.models.Coupon;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.Images;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.banners;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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
    private RecyclerView rvitem;

    private CompositeSubscription mSubscriptions;

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
        mSubscriptions = new CompositeSubscription();
        viewPager = (ViewPager) v.findViewById(R.id.banners_container);
        viewPager2 = (ViewPager) v.findViewById(R.id.offers_container);

        //side navigation bar
        drawer = v.findViewById(R.id.drawer_layout);
        button = v.findViewById(R.id.menu_button);
        cart_Img=v.findViewById(R.id.cart_Img);

        rvitem=v.findViewById(R.id.categories_container);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        back();

        if(bannersList!=null)
        {
            bannerAdapter=new BannerAdapter(Objects.requireNonNull(getContext()),bannersList);
            bannerAdapter.setTimer(viewPager,5,5,1);
            couponImageAdpter=new CouponImageAdpter(getContext(),CouponsList);
            couponImageAdpter.setTimer(viewPager2,5,5,0);
            viewPager.setAdapter(bannerAdapter);
            viewPager2.setAdapter(couponImageAdpter);

        }
        else
        {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getContext());

            Gson gson = new Gson();
            String cart=sharedPreferences.getString("banner", null);
            Type type=new TypeToken<List<banners>>(){}.getType();
            bannersList=gson.fromJson(cart,type);

            String c=sharedPreferences.getString("coupon", null);
            Type type2=new TypeToken<List<Coupon>>(){}.getType();
            CouponsList=gson.fromJson(c,type2);

            bannerAdapter = new BannerAdapter(Objects.requireNonNull(getContext()), bannersList);
            bannerAdapter.setTimer(viewPager,5,5,1);
            couponImageAdpter = new CouponImageAdpter(getContext(), CouponsList);
            couponImageAdpter.setTimer(viewPager2,5,5,0);
            viewPager.setAdapter(bannerAdapter);
            viewPager2.setAdapter(couponImageAdpter);


        }


        navigationView = (NavigationView) v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        quantity = v.findViewById(R.id.cart_fill_update_txt);


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

        List<Category> categoryList=new ArrayList<>();
        categoryList.add(new Category("Pizzas",R.drawable.asiafood1));
        categoryList.add(new Category("Cakes",R.drawable.cakes));
        categoryList.add(new Category("Donuts",R.drawable.donuts));
        categoryList.add(new Category("Pastas",R.drawable.pasta));
        categoryList.add(new Category("Garlic Breads",R.drawable.garlicbread));
        categoryList.add(new Category("Mocktails",R.drawable.mocktails));
        categoryList.add(new Category("Nachos",R.drawable.nachos));
        categoryList.add(new Category("Brownies",R.drawable.brownies));


        CategoryAdapter categoryAdapter=new CategoryAdapter(categoryList);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        rvitem.setLayoutManager(gridLayoutManager);
        rvitem.setAdapter(categoryAdapter);



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



    private void back()
    {
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else
                {
                    getActivity().finish();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }
}