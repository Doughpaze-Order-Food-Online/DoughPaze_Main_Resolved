package com.example.doughpaze;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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

import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.validation.validateFields;

public class location_activity extends Activity {
    private Button location, proceed;
    private TextInputEditText user_landmark, user_house;
    private ResultReceiver resultReceiver;
    private TextInputLayout user_house_layout, user_landmark_layout;
    private double latitude, longitude;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private CheckBox save_for_future;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mylocation;
    private static final String TAG = location_activity.class.getSimpleName();

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);


        mSubscriptions = new CompositeSubscription();


        location = (Button) findViewById(R.id.location);
        user_house = (TextInputEditText) findViewById(R.id.user_house);
        user_landmark = (TextInputEditText) findViewById(R.id.user_land);
        proceed = (Button) findViewById(R.id.proceed);
        user_house_layout = (TextInputLayout) findViewById(R.id.house_flat_input);
        user_landmark_layout = (TextInputLayout) findViewById(R.id.user_landmark);
        save_for_future = (CheckBox) findViewById(R.id.save_for_future);
        radioGroup = (RadioGroup) findViewById(R.id.type);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                progressDialog=new ProgressDialog(location_activity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_loading);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
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


    private void GO_TO_CONFIRM_PAGE() {
        Intent intent=new Intent(location_activity.this,order_confirm_activity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                          "Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mylocation= location;

                    latitude=location.getLatitude();
                   longitude=location.getLongitude();


                    setAddress(location);
                } else {
                    Toast.makeText(location_activity.this,
                            "Permisson denied",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAddress(Location location) {
        Geocoder geocoder = new Geocoder(location_activity.this,
                Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems
            resultMessage = location_activity.this
                    .getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage =location_activity.this
                        .getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        } else {
            Address address = addresses.get(0);
            StringBuilder out = new StringBuilder();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                out.append(address.getAddressLine(i));
            }

            resultMessage = out.toString();
        }
        user_landmark.setText(resultMessage);
        progressDialog.dismiss();
    }
}
