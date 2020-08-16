package com.example.doughpaze.FoodList;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doughpaze.R;
import com.example.doughpaze.models.FoodCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

    private List<SubItem> subItemList;
    private String category;
    private SharedPreferences mSharedPreferences;
    private Cart_Quantity cart_quantity;


    SubItemAdapter(List<SubItem> subItemList,String category, Cart_Quantity cart_quantity) {
        this.subItemList = subItemList;
        this.category=category;
        this.cart_quantity=cart_quantity;
    }


    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(category.equals("Pizza") ?R.layout.pizza_list_item:R.layout.subcategories_list_item, parent, false);
        initSharedPreferences(parent.getContext());
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int i) {
        SubItem subItem = subItemList.get(i);
        subItemViewHolder.foodname.setText(subItem.getFood_name());
        subItemViewHolder.price.setText(String.valueOf(subItem.getPrice()));
        subItemViewHolder.quantity.setText(("0"));
        ArrayAdapter<CharSequence> adapter = null;

       if(category.equals("Pizza"))
       {
           //spinner

          adapter=ArrayAdapter.createFromResource(subItemViewHolder.itemView.getContext(),R.array.PizzaSize,android.R.layout.simple_spinner_item);
          subItemViewHolder.size.setAdapter(adapter);
          subItemViewHolder.size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  subItemViewHolder.price.setText(String.valueOf(parent.getItemAtPosition(position).toString().equals("Small") ?subItem.getPrice():subItem.getLarge_price()));

              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });


       }

        subItemViewHolder.add.setVisibility(View.VISIBLE);
        subItemViewHolder.plus_minus.setVisibility(View.GONE);


        Glide
                .with(subItemViewHolder.itemView.getContext())
                .load(subItem.getFood_image())
                .thumbnail(Glide.with(subItemViewHolder.itemView.getContext()).load(R.drawable.loading2))
                 .centerInside()
                 .into(subItemViewHolder.foodimage);
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(subItemViewHolder.itemView.getContext());

        Gson gson = new Gson();
        String cart=mSharedPreferences.getString("cart", null);
        Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
        List<FoodCart> newfoodCarts=new ArrayList<>();
        newfoodCarts=gson.fromJson(cart,type);
        assert newfoodCarts != null;

        try{
            int q=0;
            for(FoodCart x:newfoodCarts)
            {
                if(x.getFood_name().equals(subItem.getFood_name()))
                {   q+=x.getQuantity();
                    subItemViewHolder.add.setVisibility(View.GONE);
                    subItemViewHolder.plus_minus.setVisibility(View.VISIBLE);

                }
            }
            subItemViewHolder.quantity.setText(String.valueOf(q));
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }





        subItemViewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodCart foodCart=new FoodCart();
                foodCart.setFood_Category(subItem.getCategory());
                foodCart.setFood_subcategory(subItem.getSubcategory());
                foodCart.setFood_name(subItem.getFood_name());
                foodCart.setPrice(subItem.getPrice());
                foodCart.setQuantity(1);
                if(category.equals("Pizza")){
                    foodCart.setId(String.valueOf(subItem.getId()));
                    foodCart.setSize(subItemViewHolder.size.getSelectedItem().toString());
                    foodCart.setPrice(subItemViewHolder.size.getSelectedItem().toString().equals("Small")?subItem.getPrice():subItem.getLarge_price());
                    foodCart.setAlt_price(!subItemViewHolder.size.getSelectedItem().toString().equals("Small")?subItem.getPrice():subItem.getLarge_price());
                }
                subItemViewHolder.quantity.setText(String.valueOf(foodCart.getQuantity()));

                if(mSharedPreferences.getString("cart", null) == null)
                {

                    List<FoodCart> foodCarts=new ArrayList<>();
                    foodCarts.add(foodCart);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    Gson gson = new Gson();
                    String cart = gson.toJson(foodCarts);
                    editor.putString("cart",cart);
                    editor.apply();
                    cart_quantity.UpdateNumber(foodCarts);


                }
                else
                {
                    Gson gson = new Gson();
                    String cart=mSharedPreferences.getString("cart", null);
                    Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
                    List<FoodCart> newfoodCarts=new ArrayList<>();
                    newfoodCarts=gson.fromJson(cart,type);
                    newfoodCarts.add(foodCart);

                    cart_quantity.UpdateNumber(newfoodCarts);

                    SharedPreferences.Editor editor = mSharedPreferences.edit();

                    String newcart = gson.toJson(newfoodCarts);
                    Log.e("error",newcart);
                    editor.putString("cart",newcart);
                    editor.apply();


                }
                subItemViewHolder.add.setVisibility(View.GONE);
                subItemViewHolder.plus_minus.setVisibility(View.VISIBLE);
            }
        });

        subItemViewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String cart=mSharedPreferences.getString("cart", null);
                Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
                List<FoodCart> newfoodCarts=new ArrayList<>();
                newfoodCarts=gson.fromJson(cart,type);

                assert newfoodCarts != null;
                if(!category.equals("Pizza"))
                {
                    for(FoodCart x:newfoodCarts)
                    {
                        if(x.getFood_name().equals(subItem.getFood_name()))
                        {
                            x.increment();
                            subItemViewHolder.quantity.setText(String.valueOf(x.getQuantity()));
                        }
                    }
                    cart_quantity.UpdateNumber(newfoodCarts);
                }
                else
                {int flag=0;
                    for(FoodCart x:newfoodCarts)
                    {
                        if(x.getFood_name().equals(subItem.getFood_name()) &&  subItemViewHolder.size.getSelectedItem().toString().equals(x.getSize()))
                        {   flag=1;
                            x.increment();
                            subItemViewHolder.quantity.setText(String.valueOf(x.getQuantity()));
                        }
                    }


                    if(flag==0)
                    {
                        FoodCart foodCart=new FoodCart();
                        foodCart.setId(String.valueOf(subItem.getId()));
                        foodCart.setFood_Category(subItem.getCategory());
                        foodCart.setFood_subcategory(subItem.getSubcategory());
                        foodCart.setFood_name(subItem.getFood_name());
                        foodCart.setQuantity(1);
                        foodCart.setSize(subItemViewHolder.size.getSelectedItem().toString());
                        foodCart.setPrice(subItemViewHolder.size.getSelectedItem().toString().equals("Small")?subItem.getPrice():subItem.getLarge_price());
                        foodCart.setAlt_price(!subItemViewHolder.size.getSelectedItem().toString().equals("Small")?subItem.getPrice():subItem.getLarge_price());
                        newfoodCarts.add(foodCart);

                    }

                    int q=0;
                    for(FoodCart x:newfoodCarts)
                    {
                        if(x.getFood_name().equals(subItem.getFood_name()))
                        {   q+=x.getQuantity();
                        }
                    }
                    cart_quantity.UpdateNumber(newfoodCarts);
                    subItemViewHolder.quantity.setText(String.valueOf(q));
                    

                }

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                String newcart = gson.toJson(newfoodCarts);
                Log.e("error",newcart);
                editor.putString("cart",newcart);
                editor.apply();





            }
        });

        subItemViewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                String cart=mSharedPreferences.getString("cart", null);
                Type type=new TypeToken<ArrayList<FoodCart>>(){}.getType();
                List<FoodCart> newfoodCarts=new ArrayList<>();
                newfoodCarts=gson.fromJson(cart,type);

                assert newfoodCarts != null;
                if(!category.equals("Pizza"))
                {
                    for(int i=newfoodCarts.size()-1;i>=0;--i)
                    {
                        if(newfoodCarts.get(i).getFood_name().equals(subItem.getFood_name()))
                        {
                            newfoodCarts.get(i).decrement();
                            subItemViewHolder.quantity.setText(String.valueOf(newfoodCarts.get(i).getQuantity()));
                        }
                        if(newfoodCarts.get(i).getQuantity()==0)
                        {   newfoodCarts.remove(i);
                            subItemViewHolder.add.setVisibility(View.VISIBLE);
                            subItemViewHolder.plus_minus.setVisibility(View.GONE);
                        }
                    }

                    cart_quantity.UpdateNumber(newfoodCarts);
                }
                else
                {
                    for(int i=newfoodCarts.size()-1;i>=0;--i)
                    {
                        if(newfoodCarts.get(i).getFood_name().equals(subItem.getFood_name()) && newfoodCarts.get(i).getSize().equals(subItemViewHolder.size.getSelectedItem().toString()))
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
                    if(q==0)
                    {
                        subItemViewHolder.add.setVisibility(View.VISIBLE);
                        subItemViewHolder.plus_minus.setVisibility(View.GONE);
                    }
                    subItemViewHolder.quantity.setText(String.valueOf(q));

                    cart_quantity.UpdateNumber(newfoodCarts);
                }

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                String newcart = gson.toJson(newfoodCarts);
                Log.e("error",newcart);
                editor.putString("cart",newcart);
                editor.apply();

            }
        });



    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    class SubItemViewHolder extends RecyclerView.ViewHolder {
        ImageView foodimage;
        TextView foodname, price, quantity;
        Button plus,minus,add;
        RelativeLayout plus_minus;
        Spinner size;

        SubItemViewHolder(View itemView) {
            super(itemView);
            foodimage=(ImageView)itemView.findViewById(R.id.food_img);
            foodname=(TextView)itemView.findViewById(R.id.foodName_txt_1);
            price=(TextView)itemView.findViewById(R.id.foodPrice_txt_1);
            plus=(Button)itemView.findViewById(R.id.plus_btn_1);
            minus=(Button)itemView.findViewById(R.id.minus_btn_1);
            quantity=(TextView)itemView.findViewById(R.id.quantity_text_view_1);
            add=(Button)itemView.findViewById(R.id.add);
            plus_minus=(RelativeLayout)itemView.findViewById(R.id.plus_minus_Or_AddContainer);
            size=(Spinner)itemView.findViewById(R.id.static_size_spinner);

        }
    }

    private void initSharedPreferences(Context context) {


    }


}