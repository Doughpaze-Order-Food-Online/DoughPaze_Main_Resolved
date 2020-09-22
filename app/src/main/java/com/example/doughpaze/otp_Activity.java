package com.example.doughpaze;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.User;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class otp_Activity extends AppCompatActivity {


    private String otp;
    private String name;
    private String email;
    private String mobile_no;
    private String dob;
    private String password;
    private String token;
    private String type;
    private User user;
    Intent intent;
    private EditText E1;
    private EditText E2;
    private EditText E3;
    private EditText E4;
    private EditText E5;
    private EditText E6;
    private Button button;
    private TextView textView, resend;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressBar mProgressbar;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_otp);

        mSubscriptions = new CompositeSubscription();

        intent = getIntent();
        type=intent.getStringExtra("type");
        otp = intent.getStringExtra("otp");
        name = intent.getStringExtra("name");
        email= intent.getStringExtra("email");
        mobile_no = intent.getStringExtra("phone");
        dob= intent.getStringExtra("dob");
        password = intent.getStringExtra("password");
        token= intent.getStringExtra("token");

        E1 = (EditText) findViewById(R.id.num1);
        E2 = (EditText) findViewById(R.id.num2);
        E3 = (EditText) findViewById(R.id.num3);
        E4 = (EditText) findViewById(R.id.num4);
        E5 = (EditText) findViewById(R.id.num5);
        E6 = (EditText) findViewById(R.id.num6);
        textView = (TextView) findViewById(R.id.valid_invalid_otp);
        resend=(TextView)findViewById(R.id.resend_otp_txt);
        button = (Button) findViewById(R.id.Submit_btn);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);
        E1.requestFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        E1.addTextChangedListener(new GenericTextWatcher(E1));
        E2.addTextChangedListener(new GenericTextWatcher(E2));
        E3.addTextChangedListener(new GenericTextWatcher(E3));
        E4.addTextChangedListener(new GenericTextWatcher(E4));
        E5.addTextChangedListener(new GenericTextWatcher(E5));
        E6.addTextChangedListener(new GenericTextWatcher(E6));

        if(type.equals("signup")){ button.setOnClickListener(view -> register());}

        if(type.equals("login_otp")){ button.setOnClickListener(view -> OTP_LOGIN());}

        if(type.equals("google") || type.equals("facebook")){button.setOnClickListener(view -> GOOGLE_LOGIN());}

        resend.setOnClickListener(view->RESEND_OTP());



    }


    public void register() {
        String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString() + E5.getText().toString() + E6.getText().toString();

        if (enteredOtp.equals(otp)) {
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setMobile_no(mobile_no);
            user.setDob(dob);

            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            RegisterUser(user);
        } else {
            showSnackBarMessage("Enter Valid Otp");
            textView.setVisibility(View.VISIBLE);

        }
    }

    private void OTP_LOGIN()
    {
        String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString() + E5.getText().toString() + E6.getText().toString();
        if (enteredOtp.equals(otp)) {
            user = new User();
            user.setEmail(email);
            user.setMobile_no(mobile_no);

            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

           login_with_otp();
        } else {
            showSnackBarMessage("Enter Valid Otp");
            textView.setVisibility(View.VISIBLE);

        }
    }

    private View getRootView() {
        final ViewGroup contentViewGroup = (ViewGroup) findViewById(android.R.id.content);
        View rootView = null;

        if(contentViewGroup != null)
            rootView = contentViewGroup.getChildAt(0);

        if(rootView == null)
            rootView = getWindow().getDecorView().getRootView();

        return rootView;
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(getRootView(),message,Snackbar.LENGTH_LONG).show();

    }

    private void RegisterUser(User user) {

        mSubscriptions.add(networkUtils.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {


        Toast.makeText(otp_Activity.this, "SignUp success!", Toast.LENGTH_SHORT).show();
        showSnackBarMessage(response.getMessage());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type","local");
        editor.putString(constants.TOKEN,response.getToken());
        editor.putString(constants.EMAIL,response.getemail());
        editor.putString(constants.NAME,response.getname());
        editor.putString(constants.PHONE,response.getMobile_no());
        editor.putString(constants.DOB,response.getDob());
        editor.apply();
        goToHome();

    }

    private void handleError(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error", error.toString());
            showSnackBarMessage(error.toString());
        }
    }



    private void login_with_otp(){
        mSubscriptions.add(networkUtils.getRetrofit().NUMBER_LOGIN(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError2));
    }

    private void handleResponse2(Response response) {


        Toast.makeText(otp_Activity.this, "SignIn success!", Toast.LENGTH_SHORT).show();
        showSnackBarMessage(response.getMessage());
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type","Number_login");
        editor.putString(constants.TOKEN,response.getToken());
        editor.putString(constants.EMAIL,response.getemail());
        editor.putString(constants.NAME,response.getname());
        editor.putString(constants.PHONE,response.getMobile_no());
        editor.putString(constants.DOB,response.getDob());
        editor.apply();
        goToHome();

    }

    private void handleError2(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error", error.toString());
            showSnackBarMessage(error.toString());
        }
    }

    private void GOOGLE_LOGIN(){
        String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString() + E5.getText().toString() + E6.getText().toString();
        if (enteredOtp.equals(otp)) {

            User u=new User();
            u.setMobile_no(mobile_no);
            u.setEmail(email);
            mSubscriptions.add(networkUtils.getRetrofit().SAVE_NUMBER(u)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse3,this::handleError3));

            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        } else {
            showSnackBarMessage("Enter Valid Otp");
            textView.setVisibility(View.VISIBLE);

        }
    }

    private void handleResponse3(Response response) {


        Toast.makeText(otp_Activity.this, "SignIn success!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type",type);
        editor.putString(constants.TOKEN,token);
        editor.putString(constants.EMAIL,email);
        editor.putString(constants.NAME,name);
        editor.putString(constants.PHONE,mobile_no);
        editor.putString(constants.DOB,dob);

        editor.apply();
        goToHome();

    }

    private void handleError3(Throwable error) {


        progressDialog.dismiss();
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error", error.toString());
            showSnackBarMessage(error.toString());
        }
    }



    private void goToHome() {
        Intent intent = new Intent(otp_Activity.this, MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    private void RESEND_OTP(){


        User u=new User();
        u.setMobile_no(mobile_no);
        u.setEmail(email);
        if(type.equals("google") || type.equals("facebook"))
        {
            mSubscriptions.add(networkUtils.getRetrofit().GOOGLE_OTP(u)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse4,this::handleError4));
        }

        if(type.equals("signup"))
        {
            mSubscriptions.add(networkUtils.getRetrofit().REGISTER_OTP(u)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse4,this::handleError4));
        }

        if(type.equals("login_otp"))
        {
            mSubscriptions.add(networkUtils.getRetrofit().NUMBER_OTP(u)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse4,this::handleError4));
        }

        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

    }

    private void handleResponse4(Response response) {

        //mProgressbar.setVisibility(View.GONE);
        Toast.makeText(otp_Activity.this, "New One Time Password Sent!", Toast.LENGTH_SHORT).show();
        otp=response.getOtp();
        progressDialog.dismiss();
    }

    private void handleError4(Throwable error) {

        //mProgressbar.setVisibility(View.GONE);
        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error", error.toString());
            showSnackBarMessage(error.toString());
        }
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.num1:
                    if (text.length() == 1)
                        E2.requestFocus();
                    break;
                case R.id.num2:
                    if (text.length() == 1)
                        E3.requestFocus();
                    else if (text.length() == 0)
                        E1.requestFocus();
                    break;
                case R.id.num3:
                    if (text.length() == 1)
                        E4.requestFocus();
                    else if (text.length() == 0)
                        E2.requestFocus();
                    break;
                case R.id.num4:
                    if (text.length() == 1)
                        E5.requestFocus();
                    else if (text.length() == 0)
                        E3.requestFocus();
                    break;
                case R.id.num5:
                    if (text.length() == 1)
                        E6.requestFocus();
                    else if (text.length() == 0)
                        E4.requestFocus();
                    break;
                case R.id.num6:
                    if (text.length() == 0)
                        E5.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
