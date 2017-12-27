package com.avow.bhanu.feedme.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.Services.GCMRegistrationIntentService;
import com.avow.bhanu.feedme.fragments.home.MessageDialogBox;
import com.avow.bhanu.feedme.fragments.home.contacts;
import com.avow.bhanu.feedme.fragments.home.feed;
import com.avow.bhanu.feedme.fragments.home.profile;
import com.avow.bhanu.feedme.model.User;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements MessageDialogBox.MessageDialogListener,
        feed.OnFragmentInteractionListener,
        profile.OnFragmentInteractionListener,
        contacts.OnFragmentInteractionListener{



    private ImageView homeIv, profileIv, messageIv, contactsIv;


    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnCamera, btnSearch, btnProfile, btnGallery, btnMessage;



    private BroadcastReceiver mRegistrationBroadcastReceiver;


    private static final String TAG = Home.class.getSimpleName();
    private Button btnHome;
    private String login;
    private TextView headingTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        login = settings.getString("username", null);
        if(login == null){
            Intent intent = new Intent(Home.this,LoginUsingMobileNumber.class);
            startActivity(intent);
        }



        homeIv = (ImageView) findViewById(R.id.homeIv);
        profileIv = (ImageView) findViewById(R.id.profileIv);
        messageIv = (ImageView) findViewById(R.id.messageIv);
        contactsIv = (ImageView) findViewById(R.id.contactsIv);
        headingTv = (TextView) findViewById(R.id.heading);
        headingTv.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/g1.ttf"));
        changeFooter(R.id.homeButton);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_fragment_container, feed.newInstance(login, ""), "feed_fragment").
                commit();




        btnHome = (Button) findViewById(R.id.homeButton);

        btnHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                changeFooter(R.id.homeButton);
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.main_fragment_container, feed.newInstance(login, ""), "feed_fragment").
                        commit();

            }
        });


        btnMessage = (Button) findViewById(R.id.message_button);

        btnMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeFooter(R.id.message_button);
                DialogFragment newFragment = MessageDialogBox.newInstance(new ArrayList<User>(), false);
                newFragment.show(getSupportFragmentManager(), "message");

            }
        });


        btnSearch = (Button) findViewById(R.id.searchButton);

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                changeFooter(R.id.searchButton);
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.main_fragment_container, contacts.newInstance(login, ""), "contacts_fragment").
                        commit();
            }
        });


        btnProfile = (Button) findViewById(R.id.profile_button);

        btnProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                changeFooter(R.id.profile_button);
                getSupportFragmentManager().
                        beginTransaction().
                        replace(R.id.main_fragment_container, profile.newInstance(login, login), "profile_fragment").
                        commit();
            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    //Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    Log.d("token", token);
                    Utils.saveDevice(login, token, Home.this);
                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };



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





    }

    private void changeFooter(int button) {

        switch (button){

            case R.id.homeButton:

                homeIv.setImageResource(R.drawable.home_clicked);
                contactsIv.setImageResource(R.drawable.contacts);
                profileIv.setImageResource(R.drawable.ic_action_profile);
                messageIv.setImageResource(R.drawable.text);

                headingTv.setText("Home");
                break;

            case R.id.profile_button:

                homeIv.setImageResource(R.drawable.home);
                contactsIv.setImageResource(R.drawable.contacts);
                profileIv.setImageResource(R.drawable.profile_clicked);
                messageIv.setImageResource(R.drawable.text);
                headingTv.setText("Profile");
                break;

            case R.id.searchButton:

                homeIv.setImageResource(R.drawable.home);
                contactsIv.setImageResource(R.drawable.contacts_clicked);
                profileIv.setImageResource(R.drawable.ic_action_profile);
                messageIv.setImageResource(R.drawable.text);
                headingTv.setText("Contacts");
                break;

            case R.id.message_button:

                homeIv.setImageResource(R.drawable.home);
                contactsIv.setImageResource(R.drawable.contacts);
                profileIv.setImageResource(R.drawable.ic_action_profile);
                messageIv.setImageResource(R.drawable.message_clicked);
                break;
        }
    }




    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String message, ArrayList<User> receivers_array, Boolean show_info) {

        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        try {
            String imagePath = Utils.createImageFromText(message, getApplicationContext());

            //Toast.makeText(getApplicationContext(), imagePath, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Home.this, Upload.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("receivers", receivers_array);
            intent.putExtra("show_info", show_info);
            intent.putExtra("MyImagePath", imagePath);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "could not create image", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCancelDialogInterface(DialogInterface dialog) {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if(currentFragment!=null){

            if(currentFragment instanceof feed){
                changeFooter(R.id.homeButton);

            }
            else if(currentFragment instanceof profile){
                changeFooter(R.id.profile_button);

            }
            else if(currentFragment instanceof contacts){
                changeFooter(R.id.searchButton);

            }

        }


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }



}