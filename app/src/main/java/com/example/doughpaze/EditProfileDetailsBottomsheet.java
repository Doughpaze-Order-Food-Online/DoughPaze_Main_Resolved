package com.example.doughpaze;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.User;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.validation.validateFields;


public class EditProfileDetailsBottomsheet extends BottomSheetDialogFragment {
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            dob.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1)
                    + "/" + String.valueOf(year));
        }
    };
    private Button update;
    private User user;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences sharedPreferences;
    private String token;
    private ProgressDialog progressDialog;
    private EditText name, email, phone, dob;

    private ImageButton DatePickerBtn;

    public EditProfileDetailsBottomsheet() {

    }

    private Calendar myCalendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profile_bottom_sheet, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        name = rootView.findViewById(R.id.name);
        phone = rootView.findViewById(R.id.mobile);
        email = rootView.findViewById(R.id.email);
        dob = rootView.findViewById(R.id.dob);

        DatePickerBtn = rootView.findViewById(R.id.date_Picker_Button);


        update = rootView.findViewById(R.id.update);

        mSubscriptions = new CompositeSubscription();

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        token = sharedPreferences.getString(constants.TOKEN, null);
        name.setText(sharedPreferences.getString(constants.NAME, null));
        email.setText(sharedPreferences.getString(constants.EMAIL, null));
        phone.setText(sharedPreferences.getString(constants.PHONE, null));
        dob.setText(sharedPreferences.getString(constants.DOB, null));

        phone.setEnabled(false);
        if (sharedPreferences.getString(constants.EMAIL, null) != null) {
            email.setEnabled(false);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UPDATE();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DatePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        assert getFragmentManager() != null;
        date.show(getFragmentManager(), "Date Picker");
    }


    private void UPDATE() {
        int error = 0;
        String Name = name.getText().toString();
        String Email = email.getText().toString();
        String Dob = dob.getText().toString();
        String mobile = phone.getText().toString();

        if (!validateFields(Name) || !validateFields(Email) || !validateFields(Dob))
            error++;

        if (error == 0) {
            user = new User();
            user.setMobile_no(mobile);
            user.setEmail(Email);
            user.setName(Name);
            user.setDob(Dob);
            updateDetails(user);

            progressDialog=new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
        else
        {
            Toast.makeText(getActivity(), "Enter All Details !", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateDetails(User user)
    {
        mSubscriptions.add(networkUtils.getRetrofit(token).PROFILE_DETAILS(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        //mProgressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(constants.EMAIL,response.getemail());
        editor.putString(constants.NAME,response.getname());
        editor.putString(constants.PHONE,response.getMobile_no());
        editor.putString(constants.DOB,response.getDob());
        editor.apply();
        progressDialog.dismiss();

        sendResult(response.getemail(),response.getname(),response.getMobile_no(),response.getDob());

    }

    private void handleError(Throwable error) {

        //mProgressBar.setVisibility(View.GONE);
        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }





    private void sendResult(String email, String name, String mobile, String dob) {
        if( getTargetFragment() == null ) {
            return;
        }


        Intent intent = ProfileFragment.newIntent(email,name,mobile,dob);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);


        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}

