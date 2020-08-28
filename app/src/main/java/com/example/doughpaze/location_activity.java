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
import com.example.doughpaze.utils.addressServiceIntent;
import com.example.doughpaze.utils.constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);



            mSubscriptions = new CompositeSubscription();


        location = (Button) findViewById(R.id.location);
        user_house=(TextInputEditText) findViewById(R.id.user_house);
        user_landmark=(TextInputEditText)findViewById(R.id.user_land);
        proceed=(Button)findViewById(R.id.proceed);
        user_house_layout=(TextInputLayout) findViewById(R.id.house_flat_input);
        user_landmark_layout=(TextInputLayout)findViewById(R.id.user_landmark);
        save_for_future=(CheckBox)findViewById(R.id.save_for_future);
        radioGroup=(RadioGroup)findViewById(R.id.type);

        addressResultReceiver = new LocationAddressResultReceiver(new Handler());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };



        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        proceed.setOnClickListener(view->DETAILS());




    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(location_activity.this, "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, addressServiceIntent.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
            else {
                Toast.makeText(this, "Location permission not granted, " + "restart the app if you want the " +
                        "feature", Toast.LENGTH_SHORT).show();
            }
        }
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

        Toast.makeText(this, "Address Saved!", Toast.LENGTH_SHORT).show();

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




    private void getCurrentLocation() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            progressDialog.dismiss();
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
                            LatLng latLng=new LatLng(latitude,longitude);
                            getAddressFromLatLng(latLng);
                        }
                        else
                        {
                            progressDialog.dismiss();
                        }

                    }
                }, Looper.getMainLooper());


    }





//
    private void getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                String fulladdress=address.getAddressLine(0);
                user_landmark.setText(fulladdress);

                if(progressDialog!=null)
                {
                    progressDialog.dismiss();
                }
            }
            else{

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void GO_TO_CONFIRM_PAGE() {
        Intent intent=new Intent(location_activity.this,order_confirm_activity.class);
        startActivity(intent);
        finish();

    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }
            if (resultCode == 1) {
                Toast.makeText(location_activity.this, "Address not found, ", Toast.LENGTH_SHORT).show();
            }
            String currentAdd = resultData.getString("address_result");
            showResults(currentAdd);
        }
    }
    private void showResults(String currentAdd) {
       user_landmark.setText(currentAdd);
    }
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


}
