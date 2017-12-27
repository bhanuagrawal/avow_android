package com.avow.bhanu.feedme.fragments.upload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.avow.bhanu.feedme.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadImagePreview.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadImagePreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadImagePreview extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String username;
    private String filePath;
    Boolean showReceivers;


    ImageView myImage;
    String mCurrentPhotoPath;
    Button uploadButton, cancelButton;

    private OnFragmentInteractionListener mListener;

    public UploadImagePreview() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadImagePreview.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadImagePreview newInstance(String param1, String param2, Boolean showReceivers) {
        UploadImagePreview fragment = new UploadImagePreview();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, showReceivers);

        Log.d("showReceivers", String.valueOf(showReceivers));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            filePath = getArguments().getString(ARG_PARAM2);
            showReceivers = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_upload_image_preview, container, false);
        File imgFile = new  File(filePath);


        if(imgFile.exists()){

            mCurrentPhotoPath = imgFile.getAbsolutePath();
            //Toast.makeText(Upload.this, filePath, Toast.LENGTH_LONG).show();
            myImage = (ImageView) rootView.findViewById(R.id.capturedImage1);
            setPic();

        }

        uploadButton = (Button) rootView.findViewById(R.id.confirmUpload);

        if(!showReceivers){
            uploadButton.setText("Send");
        }
        else{
            uploadButton.setText("Next");
        }


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    mListener.onCofirmUpload();


                } catch (Exception exc) {
                    Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelButton = (Button) rootView.findViewById(R.id.cancelUpload);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelUpload();
            }
        });


        return rootView;
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
        void onCofirmUpload();
        void onCancelUpload();
        void onBackPressed();
    }



    private void setPic() {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        myImage.setImageBitmap(bitmap);
    }
}
