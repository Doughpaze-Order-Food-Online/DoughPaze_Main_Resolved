package com.example.doughpaze;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.doughpaze.FoodList.Cart_Quantity;
import com.example.doughpaze.models.FoodCart;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ImagePagerAdapter imagePagerAdapter, imagePagerAdapter2;
    private ArrayList<String> banners, coupons, sample;
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

        Objects.requireNonNull(getActivity()).registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));




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

        sample = new ArrayList<String>();
        sample.add("default");
        imagePagerAdapter = new ImagePagerAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), sample);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager2.setAdapter(imagePagerAdapter);

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
        quantity=v.findViewById(R.id.cart_fill_update_txt);

        //categories
        cake.setOnClickListener(view-> FOOD_LIST_VIEW("Cake") );
        pizza.setOnClickListener(view-> FOOD_LIST_VIEW("Pizza") );
        donut.setOnClickListener(view-> FOOD_LIST_VIEW("Donut") );
        nachos.setOnClickListener(view-> FOOD_LIST_VIEW("Nachos") );
        mocktail.setOnClickListener(view-> FOOD_LIST_VIEW("Mocktail") );
        brownies.setOnClickListener(view-> FOOD_LIST_VIEW("Brownie") );
        garlic_bread.setOnClickListener(view-> FOOD_LIST_VIEW("Garlic Breads") );
        pasta.setOnClickListener(view-> FOOD_LIST_VIEW("Pasta") );

        cart_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CartActivity.class);
                startActivity(i);
            }
        });

        updateCartQuantity();

    }

    private void FOOD_LIST_VIEW(String category)
    {
        Intent intent=new Intent(getActivity(),Subcategory_main.class);
        intent.putExtra("category",category);
        startActivity(intent);
    }


    @Override
    public void onPause() {
        super.onPause();
        if(this.mConnReceiver!=null)
        {
            Objects.requireNonNull(getActivity()).unregisterReceiver(this.mConnReceiver);
            this.mConnReceiver=null;
        }

    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null) {
                try {
                    fetchImage fetchImage = new fetchImage();
                    fetchImage.execute();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "oops! something went wrong, check internet connectivity :)", Toast.LENGTH_LONG).show();
            }
        }
    };

    public class fetchImage extends AsyncTask<String, Integer, Void> {

        //api for banners
        String Banner_URL = "https://doughpaze.ddns.net/api/banner";

        //api for coupons
        String coupons_URL = "https://doughpaze.ddns.net/api/coupons";

        //creating new volley request instance

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());


        //jsonobject request for banners
        final JsonObjectRequest objectRequest=new JsonObjectRequest(
                Request.Method.GET,
                Banner_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            banners=new ArrayList<>();
                            JSONArray array = response.getJSONArray("result");
                            for(int i = 0 ; i < array.length() ; i++){
                                banners.add("https://doughpaze.ddns.net"+array.getJSONObject(i).getString("banner_location"));
                            }

                            try {
                                // code runs in a thread
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //banner container
                                        imagePagerAdapter=new ImagePagerAdapter(getActivity().getApplicationContext(),banners);
                                        viewPager.setAdapter(imagePagerAdapter);
                                        viewPager.setCurrentItem(0);
                                        imagePagerAdapter.setTimer(viewPager,5,banners.size(),0);

                                    }
                                });
                            } catch (final Exception ex) {
                                Log.i("---","Exception in thread");
                            }


                        }
                        catch (JSONException e)
                        {
                            Log.e("Rest Response",e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest error",error.toString());

                    }
                }


        );




        //jsonobject request for offers
        final JsonObjectRequest objectRequest2 = new JsonObjectRequest(
                Request.Method.GET,
                coupons_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            coupons = new ArrayList<>();
                            JSONArray array = response.getJSONArray("result");

                            for (int i = 0; i < array.length(); i++) {
                                coupons.add("https://doughpaze.ddns.net" + array.getJSONObject(i).getString("coupon_location"));
                            }



                        } catch (JSONException e) {
                            Log.e("Rest Response", e.toString());
                        }

                        try {
                            // code runs in a thread
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //offers container
                                    imagePagerAdapter2 = new ImagePagerAdapter(getActivity().getApplicationContext(), coupons);
                                    viewPager2.setAdapter(imagePagerAdapter2);
                                    viewPager2.setCurrentItem(0);
                                    imagePagerAdapter2.setTimer(viewPager2, 5, coupons.size(), 0);

                                }
                            });
                        } catch (final Exception ex) {

                            Log.i("---","Exception in thread");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest error", error.toString());

                    }
                }


        );



        @Override
        protected Void doInBackground(String... strings) {
            requestQueue.add(objectRequest);
            requestQueue.add(objectRequest2);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
        }
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
        updateCartQuantity();

    }
}