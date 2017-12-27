package com.avow.bhanu.feedme.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.Services.GCMRegistrationIntentService;
import com.avow.bhanu.feedme.fragments.login.EnterVerificationCodeFragment;
import com.avow.bhanu.feedme.fragments.login.MobileNumberRegistrationFragment;
import com.avow.bhanu.feedme.fragments.login.UserDetailsFragment;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;

import org.json.JSONObject;

import java.util.HashMap;


public class LoginUsingMobileNumber extends AppCompatActivity
        implements MobileNumberRegistrationFragment.OnRegistrationFragmentInteractionListener,
        EnterVerificationCodeFragment.OnEnterVerificationFragmentInteractionListener, UserDetailsFragment.OnUserDetailsFragmentInteractionListener {


    private static final String TAG = "loginFix";
    Boolean verificationInprocessFlag = false;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String URL = Setting.server + "/users/login/";
    private ProgressDialog progressDialog;
    private boolean newUser;
    private String login;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String device_save_URL = Setting.server + "/users/device/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        login = settings.getString("username", null);
        String name = settings.getString("name", null);
        if(login != null && name != null){
            Intent intent = new Intent(LoginUsingMobileNumber.this, Home.class);
            startActivity(intent);
            finish();
        }
        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                //  Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }




        if(savedInstanceState == null){

            setContentView(R.layout.activity_login_using_mobile_number);



            mAuth = FirebaseAuth.getInstance();

            user = mAuth.getCurrentUser();

            if(user!=null){
                Intent intent = new Intent(LoginUsingMobileNumber.this, Home.class);
                startActivity(intent);
                finish();
            }

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.login_fragment_container, MobileNumberRegistrationFragment.newInstance(false, "", ""), "mobile_number_registration_fragment").
                    commit();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("LoginActivity", "onPause");
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    public void setVerificationInProcessFlag(Boolean flag) {

        verificationInprocessFlag = flag;

    }

    @Override
    public void login(PhoneAuthCredential credential, String mob) {
        loginUtil(credential, mob);
    }

    @Override
    public void setCodeInTextView(String smsCode) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.login_fragment_container);
        if(fragment instanceof EnterVerificationCodeFragment){
            ((TextView)fragment.getView().findViewById(R.id.input_code)).setText(smsCode);
        }

    }


    public void loginUtil(PhoneAuthCredential credential, final String mob) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            verificationInprocessFlag = false;

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            user = task.getResult().getUser();

                            checkIfUserExist(mob);

                        } else {

                            verificationInprocessFlag = false;

                            Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_LONG).show();

                            getSupportFragmentManager().
                                    beginTransaction().
                                    replace(R.id.login_fragment_container, MobileNumberRegistrationFragment.newInstance(false, "", ""), "mobile_number_registration_fragment").
                                    commit();

                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }

    @Override
    public void loginFix(final PhoneAuthCredential credential, final String mob) {
        loginUtil(credential, mob);
    }

    public void onNetworkError(){

        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.login_fragment_container, MobileNumberRegistrationFragment.newInstance(false, "", ""), "mobile_number_registration_fragment").
                commit();

    }


    public void onNetworkResponse(JSONObject response, String mob){

        progressDialog.dismiss();

        if(response.has("username")){//user exists

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.login_fragment_container, UserDetailsFragment.newInstance(mob, false), "user_details_fragment").
                    commit();

        }
        else{

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.login_fragment_container, UserDetailsFragment.newInstance(mob, true), "user_details_fragment").
                    commit();


        }

    }

    private void checkIfUserExist(final String mob) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", mob);
        params.put("password", "12345678");
/*                try {
                    Log.d("password", AESCrypt.decrypt("BJRffwDIKYI1CP/9+9u9ag=="));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        onNetworkResponse(response, mob);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                progressDialog.dismiss();
                onNetworkError();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(LoginUsingMobileNumber.this);
        rQueue.add(req);

        progressDialog = new ProgressDialog(LoginUsingMobileNumber.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("verificationInprocess", verificationInprocessFlag);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {


        super.onRestoreInstanceState(savedInstanceState);
    }
}
