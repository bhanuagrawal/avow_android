package com.avow.bhanu.feedme.fragments.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.activity.Home;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserDetailsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserDetails";

    // TODO: Rename and change types of parameters
    private String mobileNumber;
    private Boolean newUser;
    private String mParam2;

    private OnUserDetailsFragmentInteractionListener mListener;
    private View rootView;
    private Button getStarted;
    private String name;


    String create_url_URL = Setting.server + "/users/api/";
    String update_url_URL = Setting.server + "/users/update";

    ProgressDialog progressDialog;
    private String url;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailsFragment newInstance(String param1, Boolean param2) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobileNumber = getArguments().getString(ARG_PARAM1);
            newUser = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_user_details, container, false);



        getStarted = (Button) rootView.findViewById(R.id.btn_get_started);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name =  ((EditText)rootView.findViewById(R.id.input_user_name)).getText().toString();

                        Pattern validNamePattern = Pattern.compile("^[a-zA-Z0-9 ]*$");

                        if(name.length() == 0){
                            Toast.makeText(getActivity(), "Name field left blank", Toast.LENGTH_LONG).show();
                            return;
                        }


                        if(!validNamePattern.matcher(name.toString()).matches()){
                            Toast.makeText(getActivity(), "Not a valid name choose another one", Toast.LENGTH_LONG).show();
                            return;
                        }


                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("name", name);
                        params.put("username", mobileNumber);
                        params.put("password", "12345678");
                        params.put("status", "active");

                        if(newUser){
                            url = create_url_URL;
                        }
                        else{
                            url = update_url_URL;
                        }


                        //Toast.makeText(getActivity(), name + " " + mobileNumber , Toast.LENGTH_LONG).show();

                        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {


                                    @Override
                                    public void onResponse(JSONObject response) {

                                        progressDialog.dismiss();
                                        if(response.has("name")){

                                            saveMobileNumber(mobileNumber);
                                            try {
                                                saveName(response.getString("name"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getActivity(), Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                        else{
                                            Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                progressDialog.dismiss();
                                NetworkResponse networkResponse = error.networkResponse;

                                VolleyLog.e("Error: ", error.getMessage());
                            }



                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("token", mobileNumber + " " + Utils.getDeviceID(getContext()));
                                return params;
                            }
                        };

                        RequestQueue rQueue = Volley.newRequestQueue(getActivity());
                        rQueue.add(req);
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Loading....");
                        progressDialog.show();


            }
        });

        Utils.saveDevice(mobileNumber, FirebaseInstanceId.getInstance().getToken(), getActivity());
        // Inflate the layout for this fragment
        return rootView;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserDetailsFragmentInteractionListener) {
            mListener = (OnUserDetailsFragmentInteractionListener) context;
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
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUserDetailsFragmentInteractionListener {
    }

    private void saveMobileNumber(String mob) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username",  mob);
        editor.commit();
        Log.d(TAG, mobileNumber + " saved");
    }

    private void saveName(String name) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name",  name);
        editor.commit();
        Log.d(TAG, name + " saved");
    }
}
