package com.example.doughpaze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doughpaze.models.Response;
import com.example.doughpaze.models.User;
import com.example.doughpaze.network.networkUtils;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.validation.validateFields;
import static com.example.doughpaze.utils.validation.validatePhone;

public class EnterMobileNumberActivity extends AppCompatActivity {
    private EditText Number;
    private TextView error;
    private Button button;
    Intent intent;
    private String token;
    private String name;
    private String email;
    private String mobile_no;
    private String dob;
    private String password;
    private String type;
    private CompositeSubscription mSubscriptions;
    private User user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_mobile_number);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Number=(EditText)findViewById(R.id.number);
        button=(Button)findViewById(R.id.loginWithOtp_btn);
        error=(TextView)findViewById(R.id.error);
        mSubscriptions = new CompositeSubscription();

        intent = getIntent();
        type=intent.getStringExtra("type");
        token = intent.getStringExtra("token");
        name = intent.getStringExtra("name");
        email= intent.getStringExtra("email");
        mobile_no = intent.getStringExtra("phone");
        dob= intent.getStringExtra("dob");
        password = intent.getStringExtra("password");

        button.setOnClickListener(view->otp_send());

    }

    private void otp_send()
    {
        String number = Objects.requireNonNull(Number.getText()).toString();


        int err = 0;

        if (!validateFields(number)) {

            err++;
            error.setText("Phone Number is required");
            error.setVisibility(View.VISIBLE);
        }

        if(!validatePhone(number) && validateFields(number))
        {
            err++;
            error.setError("Enter Valid Phone Number!");
            error.setVisibility(View.VISIBLE);
        }



        if (err == 0) {

            user = new User();
            user.setEmail(email);
            user.setMobile_no(number);

            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            SEND_OTP(user);

        } else {
            Toast.makeText(EnterMobileNumberActivity.this, "Enter Valid Details !", Toast.LENGTH_SHORT).show();

        }
    }

    private void SEND_OTP(User user)
    {
        mSubscriptions.add(networkUtils.getRetrofit().GOOGLE_OTP(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {
        User u=new User();

        u.setMobile_no(response.getMobile_no());
        u.setEmail(response.getemail());
        GoToOtp(response.getOtp(),u);
    }

    private void handleError(Throwable error) {
        Response response;

    progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                response = gson.fromJson(errorBody,Response.class);
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            Toast.makeText(EnterMobileNumberActivity.this, "Network Error !", Toast.LENGTH_SHORT).show();

        }


    }

    private void GoToOtp(String otp,User u){

        Intent intent = new Intent(EnterMobileNumberActivity.this, otp_Activity.class);
        intent.putExtra("type",type);
        intent.putExtra("otp",otp);
        intent.putExtra("phone",u.getMobile_no() );
        intent.putExtra("email", u.getEmail());
        intent.putExtra("token", token);
        intent.putExtra("name",name);
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}