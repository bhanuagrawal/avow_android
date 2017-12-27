package com.avow.bhanu.feedme.fragments.upload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.model.User;
import com.avow.bhanu.feedme.util.ContactsManager;
import com.avow.bhanu.feedme.util.MyBaseAdapter2;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceiverSelectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReceiverSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiverSelectFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String username;
    private String filePath;
    ProgressDialog progressDialog;
    ListView followersList;
    ArrayList<String> name_array = new ArrayList<String>();
    ArrayList<String> username_array = new ArrayList<String>();
    ArrayList<String> image_array = new ArrayList<String>();
    ArrayList<Boolean> isSelected = new ArrayList<>();
    ArrayList<User> user_array = new ArrayList<>();

    ArrayList<User> receivers_array = new ArrayList<User>();
    MyBaseAdapter2 adapter;
    Button sendButton, cancelButton;
    private OnFragmentInteractionListener mListener;
    private HashMap<String, String> contacts;

    public ReceiverSelectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiverSelectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiverSelectFragment newInstance(String param1, String param2) {
        ReceiverSelectFragment fragment = new ReceiverSelectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            filePath = getArguments().getString(ARG_PARAM2);
        }

        try {
            ContactsManager contactsManager =  new ContactsManager(getContext(), getActivity());
            contactsManager.refreshContacts();
            contactsManager.updateContactInDatabase(username);
            contacts = contactsManager.getAllContacts();
        }
        catch (Exception e){
            contacts = new HashMap<String, String>();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_receiver_select, container, false);
        followersList = (ListView)rootView.findViewById(R.id.followers_listView);


        final String url =  Setting.server + "/users/following?username=" + username;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        try {


                            JSONArray followers_array = response.getJSONArray("following");


                            name_array.clear();
                            username_array.clear();
                            image_array.clear();
                            user_array.clear();

                            for (int i = 0, count = followers_array.length(); i < count; i++) {
                                try {
                                    JSONObject jsonObject = followers_array.getJSONObject(i);

                                    if(contacts.containsKey(jsonObject.getString("username").toString())){
                                        name_array.add(contacts.get(jsonObject.getString("username").toString()));
                                        username_array.add(jsonObject.getString("username").toString());

                                        user_array.add(new User(contacts.get(jsonObject.getString("username").toString()), jsonObject.getString("username").toString(), "http://www.neversaycutzbarber.com/images/user/user_default.png", jsonObject.getString("status").toString()));
                                    }
                                    else{
                                        name_array.add(jsonObject.getString("name").toString());
                                        username_array.add(jsonObject.getString("username").toString());

                                        user_array.add(new User(jsonObject.getString("name").toString(), jsonObject.getString("username").toString(), "http://www.neversaycutzbarber.com/images/user/user_default.png", jsonObject.getString("status").toString()));
                                    }


                                    Collections.sort(user_array, new Utils.UserNameComparator());
                                    isSelected.add(false);
                                    //Toast.makeText(getContext(), name_array.get(i) + " " + username_array.get(i) , Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }



                            adapter = new MyBaseAdapter2(getContext(), getActivity(), user_array, name_array, username_array, receivers_array);

                            followersList.setAdapter(adapter);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token",Utils.getMobileNumber(getContext()) + " " + Utils.getDeviceID(getContext()));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getRequest);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading....");
        progressDialog.show();




        Log.d("array_size", String.valueOf(name_array.size()));


        followersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                User userTouched = user_array.get(position);


                Boolean selected = true;
                for(User receiver: receivers_array){
                    if(receiver.getUsername().compareTo(userTouched.getUsername())==0){
                        selected = false;
                        break;
                    }
                }

                if(!selected){
                    receivers_array.remove(userTouched);
                }
                else if(!inactiveReceiverPresent(userTouched)){
                    receivers_array.add(userTouched);
                }
                else {
                    Toast.makeText(getContext(), "Cannot Send Group Message to Inactive User", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }
        });

        CheckBox selectAllCheckBox = (CheckBox) rootView.findViewById(R.id.checkBox);

        selectAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {


                if (isChecked) {

                    receivers_array.clear();
                    for(User user: user_array){
                        if(user.getStatus().compareTo("active") == 0){
                            receivers_array.add(user);
                        }
                    }
                    adapter.selectAll();


                } else {
                    receivers_array.clear();
                    adapter.undoselectAll();
                }
            }
        });


        sendButton = (Button) rootView.findViewById(R.id.FinalUpload);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(receivers_array.isEmpty()){
                    if(receivers_array.contains(username)){
                        receivers_array.remove(username);
                    }
                    Toast.makeText(getContext(), "No user selected, Select atleast One", Toast.LENGTH_SHORT).show();
                }
                else if(receivers_array.size()>1){


                    new AlertDialog.Builder(getContext())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.uploadImage(filePath, getContext(), username, receivers_array, true);

                                    mListener.onSend();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.uploadImage(filePath, getContext(), username, receivers_array, false);

                                    mListener.onSend();

                                }
                            })
                            .setMessage("Do you want receivers to see list of receivers of this message?")
                            .setCancelable(false)
                            .show();


                }
                else {
                    Utils.uploadImage(filePath, getContext(), username, receivers_array, false);
                    mListener.onSend();
                }

            }
        });


        cancelButton = (Button) rootView.findViewById(R.id.cancelUpload1);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel();
            }
        });

        return rootView;
    }

    private boolean inactiveReceiverPresent(User user) {

        if(user.getStatus().compareTo("active") != 0 && !receivers_array.isEmpty()){
            return true;
        }


        for(User receiver: receivers_array){
            if(receiver.getStatus().compareTo("active") != 0){
                return true;

            }
        }


        return false;


    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEnterVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other com.example.bhanu.feedme.fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onSend();
        void onCancel();

    }

}
