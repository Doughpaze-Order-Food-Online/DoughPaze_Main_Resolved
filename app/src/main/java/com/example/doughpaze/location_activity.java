package com.example.doughpaze;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.validation.validateFields;

public class location_activity extends Activity {
    private Button location,proceed;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextInputEditText user_landmark,user_house;
    private ResultReceiver resultReceiver;
    private TextInputLayout user_house_layout,user_landmark_layout ;
    private double latitude, longitude;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private CheckBox save_for_future;
    private RadioGroup radioGroup;
    private RadioButton radioButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);



            resultReceiver=new AddressResultReceiver(new Handler());
            mSubscriptions = new CompositeSubscription();


        location = (Button) findViewById(R.id.location);
        user_house=(TextInputEditText) findViewById(R.id.user_house);
        user_landmark=(TextInputEditText)findViewById(R.id.user_land);
        proceed=(Button)findViewById(R.id.proceed);
        user_house_layout=(TextInputLayout) findViewById(R.id.house_flat_input);
        user_landmark_layout=(TextInputLayout)findViewById(R.id.user_landmark);
        save_for_future=(CheckBox)findViewById(R.id.save_for_future);
        radioGroup=(RadioGroup)findViewById(R.id.type);

        //PLACE_API_INIT();
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            location_activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );


                } else {

                    progressDialog=new ProgressDialog(location_activity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_loading);
                    Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                    getCurrentLocation();
                }
            }
        });

        proceed.setOnClickListener(view->DETAILS());




    }



    private void DETAILS() {
        user_landmark_layout.setError(null);
        user_house_layout.setError(null);

        int err=0;

        String house_details = user_house.getText().toString();
        String landmark = user_landmark.getText().toString();

        if (!validateFields(house_details)) {

            err++;
            user_house_layout.setError("House/Flat Details is required!");
        }

        if (!validateFields(landmark)) {

            err++;
            user_landmark_layout.setError("Landmark is required!");
        }

        if(radioGroup.getCheckedRadioButtonId()==-1)
        {
            Toast.makeText(this, "Select the Address Type", Toast.LENGTH_SHORT).show();
            err++;
        }

        if(err==0)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            com.example.doughpaze.models.Address newaddress=new com.example.doughpaze.models.Address();
            newaddress.setHouse_details(house_details);
            newaddress.setAddress(landmark);
            newaddress.setLatitude(latitude);
            newaddress.setLongitude(longitude);
            int id=radioGroup.getCheckedRadioButtonId();
            radioButton=(RadioButton)findViewById(id);
            newaddress.setType(radioButton.getText().toString());



            mSharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            Gson gson = new Gson();
            String address = gson.toJson(newaddress);
            Log.d("address",address);
            editor.putString("address",address);
            editor.apply();

            if(save_for_future.isChecked())
            {
                newaddress.setMobile_no(mSharedPreferences.getString(constants.PHONE, null));
                String token = mSharedPreferences.getString(constants.TOKEN, null);
                mSubscriptions.add(networkUtils.getRetrofit(token).SAVE_ADDRESS(newaddress)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));
            }
            else
            {
                GO_TO_CONFIRM_PAGE();
                progressDialog.dismiss();
            }
        }

    }



    private void handleResponse(Response response) {
        progressDialog.dismiss();

        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

        GO_TO_CONFIRM_PAGE();
    }

    private void handleError(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network Error!", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(location_activity.this).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(location_activity.this)
                                .removeLocationUpdates(this);

                        if(locationResult!=null && locationResult.getLocations().size()>0)
                        {
                            int latestLocationIndex=locationResult.getLocations().size()-1;
                            latitude=
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude=
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            Location location=new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLocation(location);
                        }
                        else
                        {
                            progressDialog.dismiss();
                        }

                    }
                }, Looper.getMainLooper());


    }

    private  void fetchAddressFromLocation(Location location)
    {
        Intent intent=new Intent(this,AddressFetchIntent.class);
        intent.putExtra(constants.RECEIVER,resultReceiver);
        intent.putExtra(constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
        finish();

    }

    private class AddressResultReceiver extends ResultReceiver
    {
        public  AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if(resultCode== constants.SUCCESS_RESULT)
            {
                user_landmark.setText(resultData.getString(constants.RESULT_DATA_KEY));

                if(progressDialog!=null)
                {
                    progressDialog.dismiss();
                }

            }else
            {
                Toast.makeText(location_activity.this, resultData.getString(constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

//    private void PLACE_API_INIT()
//    {
//        user_house.setAdapter(new PlaceAutoSuggestAdapter(this,android.R.layout.simple_list_item_1));
//
//        user_house.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("Address : ",user_house.getText().toString());
//                LatLng latLng=getLatLngFromAddress(user_house.getText().toString());
//                if(latLng!=null) {
//                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
//                    Address address=getAddressFromLatLng(latLng);
//                    if(address!=null) {
//                        Log.d("Address : ", "" + address.toString());
//                        Log.d("Address Line : ",""+address.getAddressLine(0));
//                        Log.d("Phone : ",""+address.getPhone());
//                        Log.d("Pin Code : ",""+address.getPostalCode());
//                        Log.d("Feature : ",""+address.getFeatureName());
//                        Log.d("More : ",""+address.getLocality());
//                    }
//                    else {
//                        Log.d("Adddress","Address Not Found");
//                    }
//                }
//                else {
//                    Log.d("Lat Lng","Lat Lng Not Found");
//                }
//
//            }
//        });
//    }


//    private LatLng getLatLngFromAddress(String address){
//
//        Geocoder geocoder=new Geocoder(this);
//        List<Address> addressList;
//
//        try {
//            addressList = geocoder.getFromLocationName(address, 1);
//            if(addressList!=null){
//                Address singleaddress=addressList.get(0);
//                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
//                return latLng;
//            }
//            else{
//                return null;
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//
//    }
//
//    private Address getAddressFromLatLng(LatLng latLng){
//        Geocoder geocoder=new Geocoder(this);
//        List<Address> addresses;
//        try {
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
//            if(addresses!=null){
//                Address address=addresses.get(0);
//                return address;
//            }
//            else{
//                return null;
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//
//    }

    private void GO_TO_CONFIRM_PAGE() {
        Intent intent=new Intent(location_activity.this,order_confirm_activity.class);
        startActivity(intent);
        finish();

    }




}
