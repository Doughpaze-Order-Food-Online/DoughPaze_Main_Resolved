package com.example.doughpaze.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doughpaze.FoodList.ChangePrice;
import com.example.doughpaze.R;
import com.example.doughpaze.address_activity;
import com.example.doughpaze.location_activity;
import com.example.doughpaze.models.Address;
import com.example.doughpaze.models.AddressResponse;
import com.example.doughpaze.models.FoodCart;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.order_confirm_activity;
import com.example.doughpaze.utils.constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class addressAdapter extends RecyclerView.Adapter<addressAdapter.AddressItemHolder>  {

    private CompositeSubscription mSubscriptions;
    private List<Address> list;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    Context context;
    private finishActivity finishActivity;


    public addressAdapter(List<Address> list,finishActivity finishActivity1) {
        this.list=list;
        this.finishActivity=finishActivity1;
    }


    @NonNull
    @Override
    public AddressItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_address_item, parent, false);
        return new AddressItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressItemHolder addressItemHolder, int i) {
        Address newaddress=list.get(i);
        addressItemHolder.address.setText(newaddress.getAddress());
        addressItemHolder.type.setText(newaddress.getType());
        if(newaddress.getType().trim().toLowerCase().equals("home"))
        {
            addressItemHolder.image.setImageResource(R.drawable.ic_home_black_24dp);
        }
        else if(newaddress.getType().trim().toLowerCase().equals("work"))
        {
            addressItemHolder.image.setImageResource(R.drawable.ic_work);
        }
        else
        {
            addressItemHolder.image.setImageResource(R.drawable.ic_other_location);
        }

        addressItemHolder.house_details.setText(newaddress.getHouse_details());


        addressItemHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(addressItemHolder.itemView.getContext());

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                Gson gson = new Gson();
                String address = gson.toJson(newaddress);
                Log.d("address",address);
                editor.putString("address",address);
                editor.apply();

                Intent intent=new Intent(addressItemHolder.itemView.getContext(), order_confirm_activity.class);
                addressItemHolder.itemView.getContext().startActivity(intent);
                finishActivity.ActivityFinish();
                //addressItemHolder.itemView.getContext().finish();

            }
        });

        addressItemHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(addressItemHolder.itemView.getContext())
                        .setTitle("Are You Sure??")
                        .setMessage("Do you want to delete the address?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                progressDialog=new ProgressDialog(addressItemHolder.itemView.getContext());
                                progressDialog.show();
                                progressDialog.setContentView(R.layout.progress_loading);
                                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

                                Delete_Address(newaddress.get_id(),i,addressItemHolder.itemView.getContext());

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void Delete_Address(String id, int i,Context context) {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        mSubscriptions.add(networkUtils.getRetrofit(mSharedPreferences.getString(constants.TOKEN, null))
                .DELETE_ADDRESS(id,mSharedPreferences.getString(constants.PHONE, null),i)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    @Override
    public int getItemCount() {
        return list.size();

    }

    class AddressItemHolder extends RecyclerView.ViewHolder {
        private TextView type,address,house_details;
        private LinearLayout parent;
        private ImageView delete;
        private ImageView image;

        AddressItemHolder (View itemView) {
            super(itemView);

            type=itemView.findViewById(R.id.address_type);
            house_details=itemView.findViewById(R.id.house_details);
            address=itemView.findViewById(R.id.address);
            parent=itemView.findViewById(R.id.address_container);
            delete=itemView.findViewById(R.id.delete_button);
            mSubscriptions = new CompositeSubscription();
            image=itemView.findViewById(R.id.address_type_img);
        }
    }


    private void handleResponse(Response response) {

        progressDialog.dismiss();
        if(response.getMessage().equals("success"))
        {
            update(response.getPosition());
        }


    }

    private void handleError(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
            Log.e("error",error.toString());

        }
    }

    private void update(int i)
    {   list.remove(i);
        notifyDataSetChanged();
    }


}

