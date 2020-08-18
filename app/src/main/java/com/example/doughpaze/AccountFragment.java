package com.example.doughpaze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doughpaze.models.Response;
import com.example.doughpaze.network.networkUtils;
import com.example.doughpaze.utils.constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Objects;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {


    private Button login_button, signup_button, otp_button;
    private ProgressDialog progressDialog;

    private RelativeLayout facebook;

    private ProgressBar mProgressbar;

    private RelativeLayout google;
    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";


    private GoogleSignInClient mGoogleSignInClient;
    private static final int SIGN_IN = 9001;
    private CompositeSubscription mSubscriptions;
    private CallbackManager callbackManager;




    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        mSubscriptions = new CompositeSubscription();


        login_button = rootView.findViewById(R.id.login_btn_1);
        signup_button = rootView.findViewById(R.id.signUp_btn);
        otp_button = rootView.findViewById(R.id.loginWithOtp_btn);

        mProgressbar = rootView.findViewById(R.id.progress_bar);


        facebook = rootView.findViewById(R.id.facebook_container);
        google = rootView.findViewById(R.id.google_container);




        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountFragment.this.getActivity(), loginActivity.class);
                startActivity(intent);
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountFragment.this.getActivity(), signUpActivity.class);
                startActivity(intent);
            }
        });

        otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtpBottomSheetFragment bottomSheetFragment = new OtpBottomSheetFragment();
                assert getFragmentManager() != null;
                bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        google.setOnClickListener(view -> {

            progressDialog=new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                    google();
        }
        );

        setUpFacebookCallBack();

        facebook.setOnClickListener(view->{
            progressDialog=new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

            facebook();
        });


        //google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);







        return rootView;
    }

    private void google()
    {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
        progressDialog.dismiss();


    }

    private void facebook()
    {

       progressDialog.dismiss();
        Fragment fragment=this;
        LoginManager.getInstance().logInWithReadPermissions(fragment, Collections.singleton("email"));

        LoginManager.getInstance().setAuthType(AUTH_TYPE);
        // Register a callback to respond to the user
    }

    private void setUpFacebookCallBack(){

        callbackManager =CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        try {
                            FACEBOOK_LOGIN(loginResult.getAccessToken().getToken(),loginResult.getAccessToken().getUserId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Facebook Login Failed", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(FacebookException e) {
                        // Handle exception
                        Toast.makeText(getActivity(), "Facebook Login Failed", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if(resultCode!=0)
        {
            progressDialog=new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_loading);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }


        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.



            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }




    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Google_Login(idToken);

            // TODO(developer): send ID Token to server and validate

            //updateUI(account);
        } catch (ApiException | JSONException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            progressDialog.dismiss();
            //updateUI(null);
        }
    }

    private void Google_Login(String idToken) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("idToken",idToken);
        mSubscriptions.add(networkUtils.getRetrofit().GOOGLE_LOGIN(jsonObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private  void FACEBOOK_LOGIN(String token,String userid) throws JSONException {
        JSONObject jsonObject=new JSONObject();

        jsonObject.put("userID",userid);
        jsonObject.put("accessToken",token);

        mSubscriptions.add(networkUtils.getRetrofit().FACEBOOK_LOGIN(jsonObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(com.example.doughpaze.models.Response response) {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getContext());

            if(response.getMobile_no().equals("-1"))
            {
                Intent intent=new Intent(getActivity(),EnterMobileNumberActivity.class);
                intent.putExtra("type",response.getType());
                intent.putExtra("token",response.getToken());
                intent.putExtra("email",response.getemail());
                intent.putExtra("dob",response.getDob());
                intent.putExtra("name",response.getname());
                startActivity(intent);
                progressDialog.dismiss();
                Objects.requireNonNull(getActivity()).finish();

            }
            else
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("type",response.getType());

                editor.putString(constants.TOKEN,response.getToken());
                editor.putString(constants.EMAIL,response.getemail());
                editor.putString(constants.NAME,response.getname());
                editor.putString(constants.PHONE,response.getMobile_no());
                editor.putString(constants.DOB,response.getDob());
                editor.apply();

                Toast.makeText(getActivity(), "sign in success!", Toast.LENGTH_SHORT).show();
                goToHome();
            }





    }

    private void handleError(Throwable error) {

    progressDialog.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new Gson();

            try {

                assert ((HttpException) error).response().errorBody() != null;
                String errorBody = ((HttpException) error).response().errorBody().string();
                com.example.doughpaze.models.Response response = gson.fromJson(errorBody, Response.class);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("error",error.toString());
            Toast.makeText(getActivity(), "Network Error !", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        Objects.requireNonNull(getActivity()).finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }




}