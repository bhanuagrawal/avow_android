package com.avow.bhanu.feedme.fragments.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

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
 * {@link contacts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link contacts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class contacts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SearchView searchBar;
    String url;
    //ProgressDialog pd;
    JSONObject searchResult;
    ListView userList;


    ArrayList<String> name_array = new ArrayList<String>();
    ArrayList<String> username_array = new ArrayList<String>();
    ArrayList<String> image_array = new ArrayList<String>();
    ArrayList<Boolean> isSelected = new ArrayList<>();
    ArrayList<Boolean> following_array = new ArrayList<Boolean>();
    ArrayList<String> contactsAdded = new ArrayList<>();;
    ArrayList<String> contactsRemoved = new ArrayList<>();;
    ArrayList<User> user_array = new ArrayList<>();


    HashMap<String, String> oldContacts = new HashMap<>();
    HashMap<String, String> newContacts = new HashMap<>();
    private boolean deviceContactsUpdated;
    private ListView followersList;
    private ProgressDialog progressDialog;
    private String login;
    private MyBaseAdapter2 adapter;
    private HashMap<String, String> contacts;

    private OnFragmentInteractionListener mListener;
    private View rootView;

    public contacts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment contacts.
     */
    // TODO: Rename and change types and number of parameters
    public static contacts newInstance(String param1, String param2) {
        contacts fragment = new contacts();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_contacts, container, false);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        login = settings.getString("username", null);

        try {
            ContactsManager contactsManager =  new ContactsManager(getContext(), getActivity());
            contactsManager.refreshContacts();
            contactsManager.updateContactInDatabase(login);
            contacts = contactsManager.getAllContacts();
        }
        catch (Exception e){
            contacts = new HashMap<String, String>();
        }

        View empty = rootView.findViewById(R.id.no_contact);
        followersList = (ListView)rootView.findViewById(R.id.followers_listView);
        followersList.setEmptyView(empty);

        final String url =  Setting.server + "/users/following?username=" + login;

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
                                        name_array.add(jsonObject.getString("user").toString());
                                        username_array.add(jsonObject.getString("username").toString());

                                        user_array.add(new User(jsonObject.getString("name").toString(), jsonObject.getString("username").toString(), "http://www.neversaycutzbarber.com/images/user/user_default.png", jsonObject.getString("status").toString()));
                                    }


                                    Collections.sort(user_array, new Utils.UserNameComparator());

                                    image_array.add("http://www.neversaycutzbarber.com/images/user/user_default.png");
                                    isSelected.add(false);
                                    //Toast.makeText(getContext(), name_array.get(i) + " " + username_array.get(i) , Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }



                            adapter = new MyBaseAdapter2(getActivity(), getActivity(), user_array, name_array, username_array, new ArrayList());

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

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(getRequest);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        return  rootView;
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
                    + " must implement OnFragmentInteractionListener");
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
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
