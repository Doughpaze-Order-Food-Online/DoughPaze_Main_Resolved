package com.example.doughpaze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doughpaze.FoodList.CartAdapter;
import com.example.doughpaze.FoodList.ChangePrice;
import com.example.doughpaze.models.FoodCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements ChangePrice {

private RecyclerView recyclerView;
private CartAdapter cartAdapter;
List<FoodCart> Cartlist;
private TextView Itemtotal,tax,delivery,topay,empty, t1,t2,t3,t4,t5,t6,t7;
private ScrollView scrollView;
private Button proceed;
private RelativeLayout apply_coupon;

private ImageView remove;
    private SharedPreferences mSharedPreferences;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view=inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.cart_list);
        Itemtotal=(TextView)view.findViewById(R.id.total_item_price_txt);
        tax=(TextView)view.findViewById(R.id.Taxes_charges_txt);
        delivery=(TextView)view.findViewById(R.id.Delivery_price_txt);
        topay = (TextView) view.findViewById(R.id.toPay_price_txt);
        empty = (TextView) view.findViewById(R.id.empty);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView4);
        proceed=(Button)view.findViewById(R.id.proceed);
        apply_coupon=(RelativeLayout)view.findViewById(R.id.apply_coupon);
        t1=view.findViewById(R.id.dash_line1);
        t2=view.findViewById(R.id.Discount_txt);
        t3=view.findViewById(R.id.rs_txt4);
        t4=view.findViewById(R.id.discount_value);
        t5=view.findViewById(R.id.dash_line2);
        t6=view.findViewById(R.id.applied_txt);
        t7=view.findViewById(R.id.coupon_applied);
        remove=view.findViewById(R.id.remove_coupon);


        Cartlist = CART();
        cartAdapter = new CartAdapter(Cartlist, this);
        cartAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(layoutManager);
            TOTAL(Cartlist);



        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                if(mSharedPreferences.getString("token", null)==null)
                {
                    Fragment fragment=new AccountFragment();
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction()
                            .add(android.R.id.content, fragment)
                            .addToBackStack(HomeFragment.class.getSimpleName())
                            .commit();
                }
                else
                {
                    Intent intent=new Intent(getContext(),address_activity.class);
                    startActivity(intent);

                }

            }
        });

        apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                if(mSharedPreferences.getString("token", null)==null)
                {
                    Fragment fragment=new AccountFragment();
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction()
                            .add(android.R.id.content, fragment)
                            .addToBackStack(HomeFragment.class.getSimpleName())
                            .commit();

                    Toast.makeText(getContext(), "Login to Apply Coupons!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent=new Intent(getContext(),apply_coupon_activity.class);
                    startActivityForResult(intent,1);
                }



            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext());

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("discount",null);
                editor.putString("coupon_name",null);
                editor.apply();

                TOTAL(CART());
            }
        });

        return view;
    }

    private List<FoodCart>  CART()
    {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        Gson gson = new Gson();
        String cart=mSharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<FoodCart>>() {
        }.getType();
        ArrayList<FoodCart> foodCart;
        foodCart=gson.fromJson(cart,type);

        return  foodCart;
    }

    @Override
    public void TOTAL(List<FoodCart> list) {
        try{
            if (!list.isEmpty()) {


                int sum = 0;
                for (FoodCart x : list) {
                    sum += x.getPrice() * x.getQuantity();
                }
                Itemtotal.setText(String.valueOf(sum));

                double taxamount = 0.05 * sum;
                tax.setText(String.format(Locale.ENGLISH, "%.2f", taxamount));

                int deliveryfees = sum > 1000 ? 0 : 40;
                delivery.setText(String.valueOf(deliveryfees));

                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getContext());




                if(mSharedPreferences.getString("discount", null)==null)
                {
                    topay.setText(String.format(Locale.ENGLISH, "%.2f", sum + taxamount + deliveryfees));
                    t1.setVisibility(View.GONE);
                    t2.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                    t4.setVisibility(View.GONE);
                    t5.setVisibility(View.GONE);
                    t6.setVisibility(View.GONE);
                    t7.setVisibility(View.GONE);
                    remove.setVisibility(View.GONE);


                }
                else
                {
                    topay.setText(String.format(Locale.ENGLISH, "%.2f", sum + taxamount + deliveryfees - Double.parseDouble(Objects.requireNonNull(mSharedPreferences.getString("discount", null)))));

                    t4.setText(Objects.requireNonNull(mSharedPreferences.getString("discount", null)));
                    t7.setText(Objects.requireNonNull(mSharedPreferences.getString("coupon_name", null)));
                    t1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.VISIBLE);
                    t4.setVisibility(View.VISIBLE);
                    t5.setVisibility(View.VISIBLE);
                    t6.setVisibility(View.VISIBLE);
                    t7.setVisibility(View.VISIBLE);
                    remove.setVisibility(View.VISIBLE);
                }


                empty.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);


            }
            else
            {
                empty.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            }
        }catch (NullPointerException e)
        {
            e.printStackTrace();
            empty.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TOTAL(CART());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("discount",null);
        editor.putString("coupon_name",null);
        editor.apply();
    }
}

