package com.example.doughpaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doughpaze.utils.constants;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {

    private Button edit;
    private TextView name, dob, mobile_no, email, logout ,address,transaction;
    private String type;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private static final int TARGET_FRAGMENT_REQUEST_CODE = 1;



    public ProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        edit = rootView.findViewById(R.id.edit_profile_btn);
        name=rootView.findViewById(R.id.name);
        mobile_no=rootView.findViewById(R.id.mobile_no);
        email=rootView.findViewById(R.id.email);
        dob=rootView.findViewById(R.id.date_of_birth);
        logout=rootView.findViewById(R.id.logout);
        address=rootView.findViewById(R.id.address);
        transaction=rootView.findViewById(R.id.transaction);

        //google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        name.setText(sharedPreferences.getString(constants.NAME,null));
        email.setText(sharedPreferences.getString(constants.EMAIL,null));
        mobile_no.setText(sharedPreferences.getString(constants.PHONE,null));
        dob.setText(sharedPreferences.getString(constants.DOB,null));
        type=sharedPreferences.getString("type",null);
        EditProfileDetailsBottomsheet editProfileDetailsBottomsheet = new EditProfileDetailsBottomsheet();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileDetailsBottomsheet.setTargetFragment(ProfileFragment.this, TARGET_FRAGMENT_REQUEST_CODE);
                assert getFragmentManager() != null;

                if(editProfileDetailsBottomsheet.isAdded())
                    editProfileDetailsBottomsheet.dismiss();
                else
                    editProfileDetailsBottomsheet.show(getFragmentManager(), editProfileDetailsBottomsheet.getTag());

            }
        });

        logout.setOnClickListener(view->LOGOUT());

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),myaddress.class);
                startActivity(intent);
            }
        });

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),my_transaction_activity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }

    private void LOGOUT(){

        progressDialog=new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_loading);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());


        if(Objects.equals(sharedPreferences.getString("type", null), "google"))
        {
            revokeAccess();
        }

        if(Objects.equals(sharedPreferences.getString("type", null), "facebook"))
        {
            LoginManager.getInstance().logOut();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("type",null);
        editor.putString(constants.TOKEN,null);
        editor.putString(constants.EMAIL,null);
        editor.putString(constants.NAME,null);
        editor.putString(constants.PHONE,null);
        editor.putString(constants.DOB,null);
        editor.apply();
        goToHome();

    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    private void goToHome() {
        Toast.makeText(getActivity(), "LogOut Success! ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        progressDialog.dismiss();
        Objects.requireNonNull(getActivity()).finish();
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode != Activity.RESULT_OK ) {
            return;
        }
        if( requestCode == TARGET_FRAGMENT_REQUEST_CODE ) {
            name.setText( data.getStringExtra(constants.NAME));
            email.setText( data.getStringExtra(constants.EMAIL));
            mobile_no.setText( data.getStringExtra(constants.PHONE));
            dob.setText( data.getStringExtra(constants.DOB));

        }
    }






    public static Intent newIntent(String email, String name, String mobile, String dob) {


        Intent intent = new Intent();
        intent.putExtra(constants.NAME,name);
        intent.putExtra(constants.EMAIL,email);
        intent.putExtra(constants.PHONE,mobile);
        intent.putExtra(constants.DOB,dob);
        return intent;
    }


}