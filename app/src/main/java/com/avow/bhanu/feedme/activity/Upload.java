package com.avow.bhanu.feedme.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.fragments.upload.ReceiverSelectFragment;
import com.avow.bhanu.feedme.fragments.upload.UploadImagePreview;
import com.avow.bhanu.feedme.model.User;
import com.avow.bhanu.feedme.util.Utils;

import java.util.ArrayList;


public class Upload extends AppCompatActivity
        implements UploadImagePreview.OnFragmentInteractionListener, ReceiverSelectFragment.OnFragmentInteractionListener {


    String username;
    String filePath;
    UploadImagePreview imagePreviewFrament;
    ReceiverSelectFragment receiverSelectFragment;
    ArrayList<User> receivers;
    private Boolean show_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent i = getIntent();
        receivers = (ArrayList<User>) i.getSerializableExtra("receivers");
        show_info = i.getBooleanExtra("show_info", false);




        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString("username", null);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("MyImagePath");


        Boolean show_receivers = true;
        if(receivers.size() > 0){
            show_receivers = false;
            Log.d("preview", "reply");
        }
        else {
            Log.d("preview", String.valueOf(receivers.size()));
        }
        imagePreviewFrament = UploadImagePreview.newInstance(username, filePath, receivers.size()>0?false:true);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back

        transaction.add(R.id.upload_fragment_container, imagePreviewFrament);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }


    @Override
    public void onCofirmUpload() {

        if(!receivers.isEmpty()){


            User sender = null;
            for(User receiver: receivers){
                if(receiver.getUsername().compareTo(username) ==0){
                    sender = receiver;
                    break;
                }
            }

            if(sender!=null){
                receivers.remove(sender);
            }


            Utils.uploadImage(filePath, getApplicationContext(), username, receivers, show_info);

            onSend();

        }
        else{

            receiverSelectFragment = ReceiverSelectFragment.newInstance(username, filePath);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.upload_fragment_container, receiverSelectFragment);
            // transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        }


    }

    @Override
    public void onCancelUpload() {
        Intent intent = new Intent(Upload.this,Home.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSend() {
        Intent intent = new Intent(Upload.this,Home.class);
        intent.putExtra("activity", "upload");
        startActivity(intent);
        finish();
    }

    @Override
    public void onCancel() {
        Intent intent = new Intent(Upload.this,Home.class);
        startActivity(intent);
        finish();
    }


}