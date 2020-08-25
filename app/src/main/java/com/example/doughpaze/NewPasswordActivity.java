package com.example.doughpaze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.User;
import com.example.doughpaze.network.networkUtils;
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

import static com.example.doughpaze.utils.validation.VALIDATE_PASSWORD;
import static com.example.doughpaze.utils.validation.validateFields;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText E1;
    private EditText E2;
    private EditText E3;
    private EditText E4;
    private EditText E5;
    private EditText E6;
    private TextInputLayout mTiPassword;
    private TextInputLayout mTiPassword2;
    private TextInputEditText mEtPassword;
    private TextInputEditText mEtPassword2;
    private String otp;
    private String mobile_no;
    private TextView mBtRegister,resend, invalid;
    private CompositeSubscription mSubscriptions;
    Intent intent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        intent=getIntent();
        otp=intent.getStringExtra("otp");
        mobile_no=intent.getStringExtra("phone");

        E1 = (EditText) findViewById(R.id.num1);
        E2 = (EditText) findViewById(R.id.num2);
        E3 = (EditText) findViewById(R.id.num3);
        E4 = (EditText) findViewById(R.id.num4);
        E5 = (EditText) findViewById(R.id.num5);
        E6 = (EditText) findViewById(R.id.num6);

        mEtPassword = findViewById(R.id.login_password_editText);
        mEtPassword2 = findViewById(R.id.login_repeat_password_editText);
        mTiPassword = findViewById(R.id.login_password);
        mTiPassword2 = findViewById(R.id.login_repeat_password);
        mBtRegister = findViewById(R.id.login_txt);
        resend=(TextView)findViewById(R.id.resend_otp_txt);
        invalid=(TextView)findViewById(R.id.valid_invalid_otp);

        mBtRegister.setOnClickListener(view-> check_validation());

        mSubscriptions = new CompositeSubscription();

        E1.requestFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        E1.addTextChangedListener(new GenericTextWatcher(E1));
        E2.addTextChangedListener(new GenericTextWatcher(E2));
        E3.addTextChangedListener(new GenericTextWatcher(E3));
        E4.addTextChangedListener(new GenericTextWatcher(E4));
        E5.addTextChangedListener(new GenericTextWatcher(E5));
        E6.addTextChangedListener(new GenericTextWatcher(E6));
        resend.setOnClickListener(view->RESEND_OTP());

    }

    private void check_validation(){
        setError();
        int err = 0;
        String password = Objects.requireNonNull(mEtPassword.getText()).toString();
        String password2 = Objects.requireNonNull(mEtPassword2.getText()).toString();

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }
        else if(!VALIDATE_PASSWORD(password))
        {
            err++;
            mTiPassword.setError("Password should have atleast 6 characters with 1 uppercase, 1 special character and 1 number");
        }

        if (!validateFields(password2)) {

            err++;
            mTiPassword2.setError("Confirm Password should not be empty !");
        }

        if(!password.equals(password2) && validateFields(password) && validateFields(password2)  )
        {
            err++;
            mTiPassword2.setError("Passwords do not match !");
        }

        if (err == 0) {
            String enteredOtp = E1.getText().toString() + E2.getText().toString() + E3.getText().toString() + E4.getText().toString() + E5.getText().toString() + E6.getText().toString();

            if(enteredOtp.equals(otp))
            {
                setpassword(password);

                progressDialog=new ProgressDialog(this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_loading);
                Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            }
            else
            {
                    invalid.setVisibility(View.VISIBLE);
            }

        } else {

            Snackbar.make(findViewById(android.R.id.content),"Enter Valid Details !",Snackbar.LENGTH_SHORT).show();

        }



    }

    private void setError() {

        mTiPassword.setError(null);
        mTiPassword2.setError(null);

    }

    private void setpassword(String password)
    {
        User user=new User();
        user.setPassword(password);
        user.setMobile_no(mobile_no);
        mSubscriptions.add(networkUtils.getRetrofit().SET_PASSWORD(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(Response response) {

        //mProgressbar.setVisibility(View.GONE);
        Toast.makeText(this, "Reset Password Successful", Toast.LENGTH_SHORT).show();

        Intent intent=new Intent(NewPasswordActivity.this,loginActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    private void handleError(Throwable error) {

        //mProgressbar.setVisibility(View.GONE);
        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Snackbar.make(findViewById(android.R.id.content),response.getMessage(),Snackbar.LENGTH_SHORT).show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            Snackbar.make(findViewById(android.R.id.content),"Network Error !",Snackbar.LENGTH_SHORT).show();


        }
    }


    private void RESEND_OTP(){

        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        User u=new User();
        u.setMobile_no(mobile_no);
        u.setEmail("harish.jartarghar@gmail.com");
        mSubscriptions.add(networkUtils.getRetrofit().RESET_PASSWORD_OTP(u)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError2));




    }

    private void handleResponse2(Response response) {

        //mProgressbar.setVisibility(View.GONE);
        progressDialog.dismiss();
        Toast.makeText(this, "New One Time Password Sent!", Toast.LENGTH_SHORT).show();
        otp=response.getOtp();
    }

    private void handleError2(Throwable error) {

        //mProgressbar.setVisibility(View.GONE);
        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error", error.toString());
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();

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