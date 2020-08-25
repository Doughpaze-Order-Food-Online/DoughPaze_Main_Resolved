package com.example.doughpaze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.example.doughpaze.utils.validation.VALIDATE_PASSWORD;
import static com.example.doughpaze.utils.validation.validateEmail;
import static com.example.doughpaze.utils.validation.validateFields;
import static com.example.doughpaze.utils.validation.validatePassword;
import static com.example.doughpaze.utils.validation.validatePhone;

public class signUpActivity extends AppCompatActivity {
    private ImageView back_btn;
    public static final String TAG = signUpActivity.class.getSimpleName();

    private TextInputEditText mEtName;
    private TextInputEditText mEtEmail;
    private TextInputEditText mEtMobile;
    private TextInputEditText mEtDob;
    private TextInputEditText mEtPassword;
    private TextInputEditText mEtPassword2;
    private TextView mTvLogin;
    private Button mBtRegister;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private TextInputLayout mTiPassword2;
    private TextInputLayout mTiMobile;
    private TextInputLayout mTiDob;
    private ProgressBar mProgressbar;
    private ProgressDialog progressDialog;
    private EditText DobEditText;
    private Calendar myCalendar;
    private TextInputLayout textInputLayout;
    User user;

    private TextView terms;


    private CompositeSubscription mSubscriptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textInputLayout = findViewById(R.id.user_Details2);
        terms = findViewById(R.id.term_txt);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(signUpActivity.this, TermsAndPoliciesActivity.class);
                startActivity(i);
            }
        });

        mSubscriptions = new CompositeSubscription();
        initViews();

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        mEtDob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(signUpActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mEtDob.setText(sdf.format(myCalendar.getTime()));
    }

    private void initViews() {


        mEtName = findViewById(R.id.user_firstName);
        mEtEmail = findViewById(R.id.user_email);
        mEtPassword = findViewById(R.id.login_password_editText);
        mEtPassword2 = findViewById(R.id.retype_login_password_editText);
        mEtMobile = findViewById(R.id.user_MobileNumber);
        mEtDob = findViewById(R.id.user_Dob);
        mBtRegister = findViewById(R.id.signup_txt);
        mTvLogin = findViewById(R.id.login_txt);
        mTiName = findViewById(R.id.user_Details1);
        mTiEmail = findViewById(R.id.user_login);
        mTiPassword = findViewById(R.id.login_password);
        mTiPassword2 = findViewById(R.id.retype_login_password);
        mTiMobile = findViewById(R.id.user_Details);
        mTiDob = findViewById(R.id.user_Details2);
        mProgressbar = findViewById(R.id.progress);

        mBtRegister.setOnClickListener(view -> register());
        mTvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(signUpActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void register() {

        setError();

        String name = Objects.requireNonNull(mEtName.getText()).toString();
        String email = Objects.requireNonNull(mEtEmail.getText()).toString();
        String password = Objects.requireNonNull(mEtPassword.getText()).toString();
        String password2 = Objects.requireNonNull(mEtPassword2.getText()).toString();
        String mobile_no = Objects.requireNonNull(mEtMobile.getText()).toString();
        String dob = Objects.requireNonNull(mEtDob.getText()).toString();

        int err = 0;

        if (!validateFields(name)) {

            err++;
            mTiName.setError("Name should not be empty !");
        }

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Email should be valid !");
        }

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


        if (!validateFields(mobile_no)) {

            err++;
            mTiMobile.setError("Phone Number should not be empty !");
        }

        if(validateFields(mobile_no) && !validatePhone(mobile_no))
        {
            err++;
            mTiMobile.setError("Enter Valid Phone Number!");
        }


        if (!validateFields(dob)) {

            err++;
            mTiDob.setError("D.O.B should not be empty !");
        }

        if(!password.equals(password2) && validateFields(password) && validateFields(password2)  )
        {
            err++;
            mTiPassword2.setError("Passwords do not match !");
        }


        if (err == 0) {

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

            SEND_OTP(user);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void setError() {

        mTiName.setError(null);
        mTiEmail.setError(null);
        mTiPassword.setError(null);
        mTiMobile.setError(null);
        mTiDob.setError(null);
    }

    private void SEND_OTP(User u) {

        mSubscriptions.add(networkUtils.getRetrofit().REGISTER_OTP(u)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {


        Toast.makeText(this, "One Time Password Sent Your Mobile Number!", Toast.LENGTH_SHORT).show();

        GoToOtp(response.getOtp(),user);
    }

    private void handleError(Throwable error) {

        progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
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

    private void GoToOtp(String otp,User user){

        Intent intent = new Intent(signUpActivity.this, otp_Activity.class);
        intent.putExtra("type","signup");
        intent.putExtra("otp",otp);
        intent.putExtra("name", user.getName());
        intent.putExtra("phone", user.getMobile_no());
        intent.putExtra("dob", user.getDob());
        intent.putExtra("password", Objects.requireNonNull(mEtPassword.getText()).toString());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);
        progressDialog.dismiss();

    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}