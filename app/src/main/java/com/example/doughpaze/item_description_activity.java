package com.example.doughpaze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.doughpaze.FoodList.SubItem;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class item_description_activity extends Activity {

    Intent i;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private ImageView cartImage;
    private TextView name, price, description, cart_fill_update_txt;
    private Button addtocart, plus, minus, mplus, mminus;
    private TextView quantity, quantity1;
    private RelativeLayout parent;
    private SubItem subItem;
    private ImageView cartImg;
    private ImageView backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_description);
        i = getIntent();
        mSubscriptions = new CompositeSubscription();

        name = findViewById(R.id.item_name);
        price = findViewById(R.id.item_price_txt);
        description=findViewById(R.id.item_description);
        cartImage=findViewById(R.id.imageView);
        addtocart=findViewById(R.id.addtocart);
        mplus=findViewById(R.id.plus_btn_1);
        mminus=findViewById(R.id.minus_btn_1);
        parent=findViewById(R.id.parent);
        cart_fill_update_txt=findViewById(R.id.cart_fill_update_txt);
        quantity1=findViewById(R.id.quantity_text_view_1);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        Fetch_Details(i.getStringExtra("id"));

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBox(subItem);
            }
        });

        cartImg = findViewById(R.id.cart_Img);

        cartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(item_description_activity.this, CartActivity.class);
                startActivity(i);
            }
        });

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADD_TO_CART(subItem);
            }
        });

        mminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REMOVE_FROM_CART(subItem);
            }
        });


        try {
            updateCartQuantity();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void Fetch_Details(String id) {
        mSubscriptions.add(networkUtils.getRetrofit()
                .GET_ITEM_DETAILS(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(SubItem response) {

        subItem=response;
        progressDialog.dismiss();
        parent.setVisibility(View.VISIBLE);
        name.setText(response.getFood_name());
        price.setText(String.valueOf(response.getPrice()));
        description.setText(response.getDescription());
        String url="https://doughpaze.ddns.net"+response.getFood_image();

        Glide
        .with(this)
        .load(url)
        .placeholder(R.drawable.image_loading)
        .centerInside()
        .into(cartImage);

        if(response.getCategory().equals("Pizza"))
        {
            addtocart.setVisibility(View.VISIBLE);
            mplus.setVisibility(View.GONE);
            mminus.setVisibility(View.GONE);
            quantity1.setVisibility(View.GONE);
        }
        else
        {
            addtocart.setVisibility(View.GONE);
            mplus.setVisibility(View.VISIBLE);
            mminus.setVisibility(View.VISIBLE);
            quantity1.setVisibility(View.VISIBLE);
            updateCartQuantity();


        }


    }

    private void handleError(Throwable error) {
        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                com.example.doughpaze.models.Response response = gson.fromJson(errorBody, Response.class);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();

        }
    }



    private void alertBox(SubItem subItem)
    {
        LayoutInflater layoutInflater= LayoutInflater.from(item_description_activity.this);
        final View offerView=layoutInflater.inflate(R.layout.pizza_list_item_popup,null);
        RadioGroup size=(RadioGroup) offerView.findViewById(R.id.size);
        Button plus,minus;
        plus=offerView.findViewById(R.id.plus_btn_1);
        minus=offerView.findViewById(R.id.minus_btn_1);
        TextView pizzaquantity=offerView.findViewById(R.id.quantity_text_view_1);
        ImageView close=offerView.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressDialog!=null)
                {
                    progressDialog.dismiss();
                }
            }
        });

        try{
            Gson gson = new Gson();
            String cart=mSharedPreferences.getString("cart", null);
            Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
            List<FoodCart> newfoodCarts=new ArrayList<>();
            newfoodCarts=gson.fromJson(cart,type);

            int q=0;
            assert newfoodCarts != null;
            for(FoodCart x:newfoodCarts)
            {
                if(x.getFood_name().equals(subItem.getFood_name()))
                {   q+=x.getQuantity();
                }
            }

            pizzaquantity.setText(String.valueOf(q));
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(item_description_activity.this);
                if(size.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(item_description_activity.this, "Select the size !", Toast.LENGTH_SHORT).show();
                }
                else
                {   int id=size.getCheckedRadioButtonId();
                    RadioButton radioButton=offerView.findViewById(id);

                    FoodCart foodCart=new FoodCart();
                    foodCart.setFood_Category(subItem.getCategory());
                    foodCart.setFood_subcategory(subItem.getSubcategory());
                    foodCart.setFood_name(subItem.getFood_name());
                    foodCart.setQuantity(1);
                    foodCart.setId(String.valueOf(subItem.getId()));
                    foodCart.set_id(subItem.getId());

                    foodCart.setSize(radioButton.getText().toString());
                    foodCart.setPrice(radioButton.getText().toString().equals("Small")?subItem.getPrice():subItem.getLarge_price());
                    foodCart.setAlt_price(radioButton.getText().toString().equals("Small")?subItem.getLarge_price():subItem.getPrice());
                    pizzaquantity.setText(String.valueOf(foodCart.getQuantity()));

                    if(mSharedPreferences.getString("cart", null) == null)
                    {

                        List<FoodCart> foodCarts=new ArrayList<>();
                        foodCarts.add(foodCart);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        Gson gson = new Gson();
                        String cart = gson.toJson(foodCarts);
                        editor.putString("cart",cart);
                        editor.apply();
                        updateNumber(foodCarts);


                    }
                    else
                    {


                        Gson gson = new Gson();
                        String cart=mSharedPreferences.getString("cart", null);
                        Log.e("cart",cart);
                        Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
                        List<FoodCart> newfoodCarts=new ArrayList<>();
                        newfoodCarts=gson.fromJson(cart,type);

                        int flag=0;
                        assert newfoodCarts != null;
                        for(FoodCart x:newfoodCarts)
                        {
                            if(x.getFood_name().equals(subItem.getFood_name()) &&  radioButton.getText().toString().equals(x.getSize()))
                            {   flag=1;
                                x.increment();
                                pizzaquantity.setText(String.valueOf(x.getQuantity()));
                            }
                        }


                        if(flag==0)
                        {
                            newfoodCarts.add(foodCart);

                        }

                        int q=0;
                        for(FoodCart x:newfoodCarts)
                        {
                            if(x.getFood_name().equals(subItem.getFood_name()))
                            {   q+=x.getQuantity();
                            }
                        }
                        updateNumber(newfoodCarts);
                        pizzaquantity.setText(String.valueOf(q));

                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        String newcart = gson.toJson(newfoodCarts);
                        Log.e("error",newcart);
                        editor.putString("cart",newcart);
                        editor.apply();
                    }




                }

            }

        });


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(size.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(item_description_activity.this, "Select the size to remove !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Gson gson = new Gson();
                    String cart=mSharedPreferences.getString("cart", null);
                    Log.e("cart",cart);
                    Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
                    List<FoodCart> newfoodCarts=new ArrayList<>();
                    newfoodCarts=gson.fromJson(cart,type);

                    int id=size.getCheckedRadioButtonId();
                    RadioButton radioButton=offerView.findViewById(id);

                    assert newfoodCarts != null;
                    for(int i = newfoodCarts.size()-1; i>=0; --i)
                    {
                        if(newfoodCarts.get(i).getFood_name().equals(subItem.getFood_name()) && newfoodCarts.get(i).getSize().equals(radioButton.getText().toString()))
                        {
                            newfoodCarts.get(i).decrement();
                        }
                        if(newfoodCarts.get(i).getQuantity()==0)
                        {
                            newfoodCarts.remove(i);
                        }
                    }

                    int q=0;
                    for(FoodCart x:newfoodCarts)
                    {
                        if(x.getFood_name().equals(subItem.getFood_name()))
                        {   q+=x.getQuantity();
                        }
                    }

                    pizzaquantity.setText(String.valueOf(q));

                    updateNumber(newfoodCarts);

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    String newcart = gson.toJson(newfoodCarts);
                    Log.e("error",newcart);
                    editor.putString("cart",newcart);
                    editor.apply();
                }


            }


        });


        progressDialog=new ProgressDialog(item_description_activity.this);
        progressDialog.show();
        progressDialog.setContentView(offerView);



    }

    private void updateCartQuantity()
    {mSharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(item_description_activity.this);
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
            cart_fill_update_txt.setVisibility(View.GONE);
        }
        else
        {
            cart_fill_update_txt.setVisibility(View.VISIBLE);
            cart_fill_update_txt.setText(String.valueOf(qquantity));
        }

        try{
            int q=0;
            for(FoodCart x:newfoodCarts)
            {
                if(x.getFood_name().equals(subItem.getFood_name()))
                {   q+=x.getQuantity();

                }
            }
            quantity1.setText(String.valueOf(q));
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private void updateNumber(List<FoodCart> list)
    {
        int qquantity=0;
        assert list != null;
        for(FoodCart x:list)
        {
            qquantity+=x.getQuantity();
        }
        if(qquantity==0)
        {
            cart_fill_update_txt.setVisibility(View.GONE);
        }
        else
        {
            cart_fill_update_txt.setVisibility(View.VISIBLE);
            cart_fill_update_txt.setText(String.valueOf(qquantity));
        }
    }

private void ADD_TO_CART(SubItem subItem)
{
    FoodCart foodCart=new FoodCart();
    foodCart.setFood_Category(subItem.getCategory());
    foodCart.setFood_subcategory(subItem.getSubcategory());
    foodCart.setFood_name(subItem.getFood_name());
    foodCart.setPrice(subItem.getPrice());
    foodCart.setQuantity(1);
    foodCart.set_id(subItem.getId());

    quantity1.setText(String.valueOf(foodCart.getQuantity()));

    if(mSharedPreferences.getString("cart", null) == null)
    {

        List<FoodCart> foodCarts=new ArrayList<>();
        foodCarts.add(foodCart);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String cart = gson.toJson(foodCarts);
        editor.putString("cart",cart);
        editor.apply();
        updateNumber(foodCarts);


    }
    else
    {
        Gson gson = new Gson();
        String cart=mSharedPreferences.getString("cart", null);
        Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
        List<FoodCart> newfoodCarts=new ArrayList<>();
        newfoodCarts=gson.fromJson(cart,type);

        assert newfoodCarts != null;
        int flag=0;
        for(FoodCart x:newfoodCarts)
        {
            if(x.getFood_name().equals(subItem.getFood_name()))
            {flag=1;
                x.increment();
                quantity1.setText(String.valueOf(x.getQuantity()));
            }
        }

        if(flag==0)
        {
            newfoodCarts.add(foodCart);
        }


      updateNumber(newfoodCarts);


        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String newcart = gson.toJson(newfoodCarts);
        Log.e("error",newcart);
        editor.putString("cart",newcart);
        editor.apply();

    }
}

private void REMOVE_FROM_CART(SubItem subItem)
{
    Gson gson = new Gson();
    String cart=mSharedPreferences.getString("cart", null);
    Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
    List<FoodCart> newfoodCarts=new ArrayList<>();
    newfoodCarts=gson.fromJson(cart,type);

    assert newfoodCarts != null;

    for(int i=newfoodCarts.size()-1;i>=0;--i)
    {
        if(newfoodCarts.get(i).getFood_name().equals(subItem.getFood_name()))
        {
            newfoodCarts.get(i).decrement();
            quantity1.setText(String.valueOf(newfoodCarts.get(i).getQuantity()));
        }
        if(newfoodCarts.get(i).getQuantity()==0)
        {   newfoodCarts.remove(i);

        }
    }

    updateNumber(newfoodCarts);



    SharedPreferences.Editor editor = mSharedPreferences.edit();
    String newcart = gson.toJson(newfoodCarts);
    Log.e("error",newcart);
    editor.putString("cart",newcart);
    editor.apply();

}
}
