package com.avow.bhanu.feedme.fragments.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.activity.Home;
import com.avow.bhanu.feedme.activity.Upload;
import com.avow.bhanu.feedme.adapters.FeedAdapter;
import com.avow.bhanu.feedme.model.Feed;
import com.avow.bhanu.feedme.model.Post;
import com.avow.bhanu.feedme.model.User;
import com.avow.bhanu.feedme.util.ContactsManager;
import com.avow.bhanu.feedme.util.MyBaseAdapter1;
import com.avow.bhanu.feedme.util.Permissions;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain getActivity() fragment must implement the
 * {@link feed.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link feed#newInstance} factory method to
 * create an instance of getActivity() fragment.
 */
public class feed extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;




    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnCamera, btnSearch, btnProfile, btnGallery, btnMessage;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private ImageView imageView;

    private String login;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = Home.class.getSimpleName();

    private RecyclerView postLv;


    ArrayList<Post> posts;


    private ContactsManager contactsManager;
    private Button btnHome;
    private HashMap<String, String> contacts;




    private OnFragmentInteractionListener mListener;
    private View rootView;
    private boolean fragmentViewCreatedFlag = false;
    private FeedAdapter adapter;

    public feed() {
        // Required empty public constructor
    }

    /**
     * Use getActivity() factory method to create a new instance of
     * getActivity() fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment feed.
     */
    // TODO: Rename and change types and number of parameters
    public static feed newInstance(String param1, String param2) {
        feed fragment = new feed();
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
            login = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        Permissions.requestPermissions(getActivity());

        contactsManager = new ContactsManager(getContext(), getActivity());

        if(Permissions.checkContactReadPermissions(getActivity())){
            contactsManager.refreshContacts();
            contactsManager.updateContactInDatabase(login);

        }



    }

    @Override
    public void onResume() {
        super.onResume();


        int delay = 0;
        if(getActivity().getIntent().getStringExtra("activity")!=null){
            Log.d("abc", getActivity().getIntent().getStringExtra("activity"));
            delay = 1000;
        }

        try {
            contacts = contactsManager.getAllContacts();
        }
        catch (Exception e){
            contacts = new HashMap<String, String>();
        }
        swipeRefreshLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        if(login!=null){
                                            myAsyncTask m= (myAsyncTask) new myAsyncTask().execute(login);
                                        }

                                    }
                                },  delay
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView =  inflater.inflate(R.layout.fragment_feed, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
/*        swipeRefreshLayout.post(new Runnable() {
                                           @Override
                                           public void run() {
                                               swipeRefreshLayout.setRefreshing(true);
                                               if(login!=null){
                                                   myAsyncTask m= (myAsyncTask) new myAsyncTask().execute(login);
                                               }

                                           }
                                       }
        );*/

        //btnCamera = (Button) findViewById(R.id.camera);
        imageView = (ImageView) rootView.findViewById(R.id.capturedImage1);

/*        btnCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }


            }
        });*/


        postLv = (RecyclerView) rootView.findViewById(R.id.post_listview);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        postLv.setLayoutManager(llm);


        adapter = new FeedAdapter(getActivity(), new ArrayList<Post>());
        postLv.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(login!=null){
                    myAsyncTask m= (myAsyncTask) new myAsyncTask().execute(login);
                }
            }
        });


        return rootView;
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
     * getActivity() interface must be implemented by activities that contain getActivity()
     * fragment to allow an interaction in getActivity() fragment to be communicated
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





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  Permissions.REQUEST_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    boolean contactReadPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(contactReadPermission){

                        contactsManager.refreshContacts();
                        contactsManager.updateContactInDatabase(login);
                    }
                    else {
                        Toast.makeText(getContext(), "No contact Read Permissions ", Toast.LENGTH_SHORT).show();
                    }


                    if(writePermission){
                        myAsyncTask m= (myAsyncTask) new myAsyncTask().execute(login);
                    }
                    else {
                        Toast.makeText(getContext(), "No storage access permission ", Toast.LENGTH_SHORT).show();
                    }


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions getActivity() app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
/*            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            imageView.setImageBitmap(bitmap);*/

            Intent intent = new Intent(getActivity(), Upload.class);
            intent.putExtra("MyImagePath", mCurrentPhotoPath);
            startActivity(intent);
        }


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Intent intent = new Intent(getActivity(), Upload.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("MyImagePath", picturePath);

            Log.d(TAG, picturePath);
            startActivity(intent);
        }
    }







    class myAsyncTask extends AsyncTask<String, Void, String>
    {



/*        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(Search.getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }*/

        @Override
        protected String doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String param = URLEncoder.encode(params[0],"UTF-8");
                URL url = new URL(Setting.server+"/feed/home?username=" + param );

                Log.d("url", url.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty ("token", Utils.getMobileNumber(getContext()) + " " + Utils.getDeviceID(getContext()));
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {

                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }


        @Override
        protected void onPostExecute(String response) {

            swipeRefreshLayout.setRefreshing(false);

            super.onPostExecute(response);

            if(response == null) {
                Toast.makeText(getActivity(), "Unable to connect to server,please try later", Toast.LENGTH_LONG).show();

            }
            else {

                Gson gson = new Gson();
                Feed feed =  gson.fromJson(response, Feed.class);
                posts = feed.getPosts();

                if(posts==null){
                    posts = new ArrayList<Post>();
                }

                for(Post post: posts){
                    for(User receiver: post.getReceivers()){
                        if(contacts.containsKey(receiver.getUsername())){
                            receiver.setName(contacts.get(receiver.getUsername()));
                        }
                    }
                }

                //Log.d("feed", posts.get(0).getImage_url());
                adapter = new FeedAdapter(getActivity(), posts);
                postLv.setAdapter(adapter);

            }
        }


    }
}
