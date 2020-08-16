package com.example.doughpaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.validation.validateEmail;
import static com.example.doughpaze.utils.validation.validateFields;


public class loginActivity extends AppCompatActivity {

    private ImageView back_btn;



    private TextInputEditText mEtEmail;
    private TextInputEditText mEtPassword;
    private Button mBtLogin;
    private TextView mTvRegister;
    private Button mTvForgotPassword;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;


    Button forgotPassword_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSubscriptions = new CompositeSubscription();
        initViews();
        initSharedPreferences();

        back_btn = findViewById(R.id.back_btn);
        forgotPassword_btn = findViewById(R.id.forget_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        forgotPassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordOtpBottomsheet bottomsheet = new ForgotPasswordOtpBottomsheet();
                bottomsheet.show(getSupportFragmentManager(), "TAG");
            }
        });
    }


    private void initViews() {

        mEtEmail = (TextInputEditText) findViewById(R.id.user_email);
        mEtPassword = (TextInputEditText) findViewById(R.id.login_password_editText);
        mBtLogin = (Button) findViewById(R.id.login_txt);
        mTiEmail = (TextInputLayout) findViewById(R.id.user_login);
        mTiPassword = (TextInputLayout) findViewById(R.id.login_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mTvRegister = (TextView) findViewById(R.id.signup_txt);
        mTvForgotPassword = (Button) findViewById(R.id.forget_btn);

        mBtLogin.setOnClickListener(view -> login());
        mTvRegister.setOnClickListener(view -> goToRegister());
        //mTvForgotPassword.setOnClickListener(view -> showDialog());
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    private void login() {

        setError();

        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        int err = 0;

        if (!validateFields(email)) {
            err++;
            mTiEmail.setError("Email/Number should not be empty !");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            loginProcess(email,password);

            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void setError() {

        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void loginProcess(String email, String password) {

        mSubscriptions.add(networkUtils.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {



        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(loginActivity.this, "Log In success!", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("type","local");
        editor.putString(constants.TOKEN,response.getToken());
        editor.putString(constants.EMAIL,response.getemail());
        editor.putString(constants.NAME,response.getname());
        editor.putString(constants.PHONE,response.getMobile_no());
        editor.putString(constants.DOB,response.getDob());
        editor.apply();

        mEtEmail.setText(null);
        mEtPassword.setText(null);

        Intent intent = new Intent(loginActivity.this,MainActivity.class );
        startActivity(intent);
        progressDialog.dismiss();
        finish();

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

            showSnackBarMessage("Network Error !");
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
        Snackbar.make(getRootView(),message,Snackbar.LENGTH_SHORT).show();

    }

    private void goToRegister(){
        Intent intent = new Intent(loginActivity.this, signUpActivity.class);
        startActivity(intent);
        finish();
    }

//    private void showDialog(){
//
//        ResetPasswordDialog fragment = new ResetPasswordDialog();
//
//        fragment.show(getFragmentManager(), ResetPasswordDialog.TAG);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}