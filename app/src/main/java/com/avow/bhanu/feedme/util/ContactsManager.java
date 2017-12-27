package com.avow.bhanu.feedme.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by bhanu on 14/8/17.
 */

public class ContactsManager {

    ArrayList<String> contactsAdded = new ArrayList<>();;
    ArrayList<String> contactsRemoved = new ArrayList<>();;
    Set<String> updatedContactList = new TreeSet<>();;

    HashMap<String, String> oldContacts = new HashMap<>();
    HashMap<String, String> newContacts = new HashMap<>();

    public boolean deviceContactsUpdated;
    public Context context;
    public Activity activity;

    public ContactsManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void refreshContacts() {

        try {
            getOldContacts("contacts");
            printContacts(oldContacts);

        } catch (IOException e) {
            oldContacts = new HashMap<>();
            System.out.println("could not get contacts from file");
        } catch (ClassNotFoundException e) {
            oldContacts = new HashMap<>();
            //e.printStackTrace();
            System.out.println("could not get hashmap from file");
        } catch (Exception e) {
            oldContacts = new HashMap<>();
            e.printStackTrace();
        }


        getAllContacts();
        printContacts(newContacts);
        checkForContactsUpdate();

    }

    public void checkForContactsUpdate() {

        contactsAdded.clear();
        contactsRemoved.clear();
        deviceContactsUpdated = false;


        System.out.println("new contacts" + String.valueOf(newContacts.keySet().size()));

        for(String number: newContacts.keySet()){

            if(!oldContacts.containsKey(number)){
                contactsAdded.add(number);
                deviceContactsUpdated = true;
            }

        }

        for(String number: oldContacts.keySet()){

            if(!newContacts.containsKey(number)){
                contactsRemoved.add(number);
                deviceContactsUpdated = true;
            }

        }

        if(deviceContactsUpdated){

            System.out.println(String.valueOf(contactsAdded.size()) + "  contacts added");
            System.out.println(String.valueOf(contactsRemoved.size()) + "  contacts deleted");

        }


    }


    public void getOldContacts(String file) throws IOException, ClassNotFoundException {

        FileInputStream fileInputStream  = new FileInputStream(context.getDir("data", context.MODE_PRIVATE) + "/" + file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        oldContacts = (HashMap<String, String>) objectInputStream.readObject();
        objectInputStream.close();


        System.out.println("fetched old contacts");
    }

    public void saveContactsFileOnDevice(Map<String, String> contacts, String fileName) throws IOException {

        File file = new File( context.getDir("data", context.MODE_PRIVATE), fileName);
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
        outputStream.writeObject(contacts);
        outputStream.flush();
        outputStream.close();
    }





    public HashMap<String, String> getAllContacts(){

        newContacts.clear();
        updatedContactList.clear();

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String name = phones.getString(phones.getColumnIndex(Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY :
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            //System.out.println(name + " " + phoneNumber);

            newContacts.put(Utils.parsePhoneNumber(phoneNumber), name);
            updatedContactList.add(Utils.parsePhoneNumber(phoneNumber));

        }
        phones.close();

        return newContacts;
    }


    public void printContacts(HashMap<String, String> contacts){

        for(String number: contacts.keySet()){
            System.out.println(number + " " + contacts.get(number));
        }

    }


    public void updateContactInDatabase(String username) {

        if(deviceContactsUpdated){

            Boolean contactAddedInDatabase = false;
            Boolean contactDeletedInDatabase = false;



            if(getContactsAdded().size() > 0){

                System.out.println("contacts added");
                try {
                    followContactsAdded(getContactsAdded(), username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else{
                contactAddedInDatabase = true;
            }

            if(getContactsRemoved().size() > 0){
                try {
                    unfollowContactedRemoved(getContactsRemoved(), username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else{
                contactDeletedInDatabase = true;
            }


            if(contactAddedInDatabase && contactDeletedInDatabase){
                try {
                    saveContactsFileOnDevice(getNewContacts(), "contacts");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void unfollowContactedRemoved(ArrayList<String> contactsRemoved, String username) throws JSONException {
        
        final String unfollowMany_url =  Setting.server + "/users/unfollowmany";
        JSONObject dataToPost = new JSONObject();
        dataToPost.put("username", username);
        dataToPost.put("toUnFollow", new JSONArray(contactsRemoved));

        JsonObjectRequest req = new JsonObjectRequest(unfollowMany_url, dataToPost,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getBoolean("success")){

                                Toast.makeText(context, "Few Contacts Removed", Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("Error: ", error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token",Utils.getMobileNumber(context) + " " + Utils.getDeviceID(context));
                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(req);

    }

    private void followContactsAdded(ArrayList<String> contactsAdded, String username) throws JSONException {

        final String followMany_url =  Setting.server + "/users/followmany";
        contactsAdded.remove(username);

        JSONObject dataToPost = new JSONObject();
        JSONArray toFollow = new JSONArray(contactsAdded);


        dataToPost.put("toFollow", toFollow);
        dataToPost.put("username", username);

        System.out.println(dataToPost.toString());

        JsonObjectRequest req = new JsonObjectRequest(followMany_url, dataToPost,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getBoolean("success")){



                                //Toast.makeText(context, response.getInt("count") + " New Contacts Added", Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {

                            Toast.makeText(context, "Some error occured in adding contacts", Toast.LENGTH_LONG).show();


                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("Error: ", error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token",Utils.getMobileNumber(context) + " " + Utils.getDeviceID(context));
                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(req);

    }

    public ArrayList<String> getContactsAdded() {
        return contactsAdded;
    }

    public void setContactsAdded(ArrayList<String> contactsAdded) {
        this.contactsAdded = contactsAdded;
    }

    public ArrayList<String> getContactsRemoved() {
        return contactsRemoved;
    }

    public void setContactsRemoved(ArrayList<String> contactsRemoved) {
        this.contactsRemoved = contactsRemoved;
    }

    public HashMap<String, String> getOldContacts() {
        return oldContacts;
    }

    public void setOldContacts(HashMap<String, String> oldContacts) {
        this.oldContacts = oldContacts;
    }

    public HashMap<String, String> getNewContacts() {
        return newContacts;
    }

    public void setNewContacts(HashMap<String, String> newContacts) {
        this.newContacts = newContacts;
    }

    public boolean isDeviceContactsUpdated() {
        return deviceContactsUpdated;
    }

    public void setDeviceContactsUpdated(boolean deviceContactsUpdated) {
        this.deviceContactsUpdated = deviceContactsUpdated;
    }
}
