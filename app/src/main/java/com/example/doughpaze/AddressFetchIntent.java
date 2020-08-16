package com.example.doughpaze;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.doughpaze.utils.constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddressFetchIntent extends IntentService {
private ResultReceiver resultReceiver;

    public AddressFetchIntent(){
        super("AddressFetchIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
            if(intent!=null)
            {
                String errorMessage="";
                resultReceiver=intent.getParcelableExtra(constants.RECEIVER);
                Location location=intent.getParcelableExtra(constants.LOCATION_DATA_EXTRA);
                if(location==null)
                {
                    return;
                }

                Geocoder geocoder=new Geocoder(this, Locale.getDefault());
                List<Address> addresses=null;
                try {
                    addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                } catch (IOException e) {
                    errorMessage=e.getMessage();
                }
                if(addresses==null || addresses.isEmpty())
                {
                    deliverResultToReceiver(constants.FAILURE_RESULT,errorMessage);
                }
                else
                {
                    Address address=addresses.get(0);
                    ArrayList<String> addressFragments=new ArrayList<>();
                    for(int i=0;i<=address.getMaxAddressLineIndex();i++)
                    {
                        addressFragments.add(address.getAddressLine(i));
                    }
                    deliverResultToReceiver(
                            constants.SUCCESS_RESULT,
                            TextUtils.join(
                                    Objects.requireNonNull(System.getProperty("line.separator")),
                                    addressFragments
                            )
                    );
                }

            }
    }

    private void deliverResultToReceiver(int resultCode,String addressMessgae)
    {
        Bundle bundle=new Bundle();
        bundle.putString(constants.RESULT_DATA_KEY,addressMessgae);
        resultReceiver.send(resultCode,bundle);
    }
}
