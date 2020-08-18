package com.example.doughpaze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.FoodList.Cart_Quantity;
import com.example.doughpaze.FoodList.Item;
import com.example.doughpaze.FoodList.ItemAdapter;
import com.example.doughpaze.FoodList.SubItem;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.Food_Details;
import com.example.doughpaze.models.Food_Response;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.subcategory_list;
import com.example.doughpaze.network.networkUtils;
import com.google.android.material.textfield.TextInputEditText;
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

public class Subcategory_main extends Activity implements Cart_Quantity {

    private CompositeSubscription mSubscriptions;
    private Intent intent;
    private TextView heading, update;
    private RecyclerView rvItem;
    private ItemAdapter itemAdapter;
    private ProgressDialog progressDialog;
    private ImageView back_img_btn;
    private TextInputEditText search;
    private List<subcategory_list> list;
    private List<Item> itemList;
    private List<SubItem> subItemList;
    private SharedPreferences mSharedPreferences;
    private ImageView cart_btn;
    private Toast t;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subcategories_main);


        mSubscriptions = new CompositeSubscription();
        intent = getIntent();

        heading = (TextView) findViewById(R.id.category_txt);
        heading.setText(intent.getStringExtra("category"));
        rvItem = findViewById(R.id.subcategory_container);
        back_img_btn = findViewById(R.id.back_btn);
        update = findViewById(R.id.cart_fill_update_txt);

        search = (TextInputEditText) findViewById(R.id.search_edit_text);

        cart_btn = findViewById(R.id.cart_Img);

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Subcategory_main.this, CartActivity.class);
                startActivity(i);
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        FETCH_ITEMS(intent.getStringExtra("category"));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        back_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try{
            UpdateNumber(CART());
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        SEARCH();

    }

    private  void FETCH_ITEMS(String category){

        mSubscriptions.add(networkUtils.getRetrofit().GET_FOOD_LIST(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(Food_Response response) {
        list=new ArrayList<>();
            list=response.getResult();

        itemAdapter = new ItemAdapter(buildItemList(list,""),intent.getStringExtra("category"),this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Subcategory_main.this);
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);
        progressDialog.dismiss();


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
            Log.e("error",error.toString());
            t=Toast.makeText(Subcategory_main.this, "Network Error !", Toast.LENGTH_SHORT);
            t.show();
            FETCH_ITEMS(intent.getStringExtra("category"));
        }
    }

    private List<Item> buildItemList(List<subcategory_list> l,String s) {
        itemList = new ArrayList<>();

        for(subcategory_list x:l)
        {
            if(buildSubItemList(x.getDetails(),s).size()!=0)
            {   Item item = new Item(x.getSubcategory(), buildSubItemList(x.getDetails(),s));
                itemList.add(item);
            }
        }

        return itemList;
    }

    private List<SubItem> buildSubItemList(List<Food_Details> list,String s) {
        subItemList = new ArrayList<>();
        for (Food_Details y:list) {
            SubItem subItem;
            if(y.getFood_name().toLowerCase().contains(s))
            {   if(y.getCategory().equals("Pizza"))
            {
                subItem = new  SubItem(y.getID(),"http://40.88.123.141:3000"+y.getFood_image(),y.getFood_name(),y.getPrice(),y.getLarge_price(),y.getCategory(),y.getSubcategory());
            }
            else
            {
                subItem = new SubItem(y.getID(),"http://40.88.123.141:3000"+y.getFood_image(),y.getFood_name(),y.getPrice(),y.getCategory(),y.getSubcategory());
            }
                subItemList.add(subItem);
            }
        }
        return subItemList;
    }

    private void  SEARCH(){

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    filter(s.toString());
            }


        });
    }

    private void filter(String s) {
        itemAdapter = new ItemAdapter(buildItemList(list,s),intent.getStringExtra("category"),this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Subcategory_main.this);
        rvItem.setAdapter(itemAdapter);
        rvItem.setLayoutManager(layoutManager);
    }


    @Override
    public void UpdateNumber(List<FoodCart> list) {
        int quantity=0;
            for(FoodCart x:list)
            {
                quantity+=x.getQuantity();
            }
          if(quantity==0)
          {
              update.setVisibility(View.GONE);
          }
          else
          {
              update.setVisibility(View.VISIBLE);
              update.setText(String.valueOf(quantity));
          }

    }

    private List<FoodCart>  CART()
    {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String cart=mSharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<FoodCart>>() {
        }.getType();
        ArrayList<FoodCart> foodCart;
        foodCart=gson.fromJson(cart,type);

        return  foodCart;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSubscriptions!=null)
        {
            mSubscriptions.unsubscribe();
        }
    }
}
