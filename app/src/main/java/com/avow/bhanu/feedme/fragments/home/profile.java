package com.avow.bhanu.feedme.fragments.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.activity.LoginUsingMobileNumber;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private JSONArray followers, following;
    private String name;
    private boolean isFollowing;

    private TextView nameTv, usernameTv;
    private ImageView followIv, menuIv;
    private Button followButton, popupButton;
    ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private String username;
    private String loggedInUser;

    public profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
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
            loggedInUser = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        final String url = Setting.server + "/users/details?username=" + username;
        final String follow_url =  Setting.server + "/users/follow/";
        final String unfollow_url =  Setting.server + "/users/unfollow/";


        nameTv = (TextView)rootView. findViewById(R.id.name_textview_profile);
        usernameTv = (TextView) rootView.findViewById(R.id.username_textview_profile);
        followIv = (ImageView) rootView.findViewById(R.id.imageView4_profile);
        menuIv = (ImageView) rootView.findViewById(R.id.imageView4_menu);
        followButton = (Button)rootView. findViewById(R.id.followButton_profile);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        try {
                            followers = response.getJSONArray("followers");
                            following = response.getJSONArray("following");
                            name = response.getJSONObject("user_details").getString("name");
                            isFollowing = false;

                            if(followers.toString().contains(loggedInUser)){
                                isFollowing = true;
                            }



                            nameTv.setText(name);

                            usernameTv.setText("@" + username);

                            if(loggedInUser.equals(username)){

                                followButton.setVisibility(View.GONE);
                                followIv.setVisibility(View.GONE);

                            }
                            else if(isFollowing){

                                followIv.setImageResource(R.drawable.following);

                            }
                            else{
                                followIv.setImageResource(R.drawable.follow);
                            }







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


        followButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                if(!isFollowing){
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user", loggedInUser);
                    params.put("following", username);

                    JsonObjectRequest req = new JsonObjectRequest(follow_url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        if(response.getBoolean("success")){

                                            isFollowing = true;
                                            followIv.setImageResource(R.drawable.following);

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
                            params.put("token",Utils.getMobileNumber(getContext()) + " " + Utils.getDeviceID(getContext()));
                            return params;
                        }
                    };

                    RequestQueue rQueue = Volley.newRequestQueue(getActivity());
                    rQueue.add(req);
                }
                else{

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user", loggedInUser);
                    params.put("following", username);

                    JsonObjectRequest req = new JsonObjectRequest(unfollow_url, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        if(response.getBoolean("success")){

                                            isFollowing = false;
                                            followIv.setImageResource(R.drawable.follow);

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
                            params.put("token",Utils.getMobileNumber(getContext()) + " " + Utils.getDeviceID(getContext()));
                            return params;
                        }
                    };

                    RequestQueue rQueue = Volley.newRequestQueue(getActivity());
                    rQueue.add(req);

                }
            }
        });

        popupButton = (Button)rootView. findViewById(R.id.popup_profile);
        if(!username.equals(loggedInUser)){
            popupButton.setVisibility(View.GONE);
            menuIv.setVisibility(View.GONE);
        }


        popupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopup(v);

            }
        });
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


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), popupButton);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }


    public void logout(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

        FirebaseAuth.getInstance().signOut();

        settings.edit().remove("username").commit();
        Intent intent = new Intent(getActivity(),LoginUsingMobileNumber.class);
        startActivity(intent);
    }
}
