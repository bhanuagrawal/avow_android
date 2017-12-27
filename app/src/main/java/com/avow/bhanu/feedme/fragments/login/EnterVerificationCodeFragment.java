package com.avow.bhanu.feedme.fragments.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avow.bhanu.feedme.R;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEnterVerificationFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnterVerificationCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterVerificationCodeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "verification fragment";


    // TODO: Rename and change types of parameters
    private String verficationCode;
    private String mobileNumber;
    private OnEnterVerificationFragmentInteractionListener mListener;
    private View rootView;
    private Button verifyCodeButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public EnterVerificationCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterVerificationCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterVerificationCodeFragment newInstance(String param1, String param2) {
        EnterVerificationCodeFragment fragment = new EnterVerificationCodeFragment();
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
            mVerificationId = getArguments().getString(ARG_PARAM1);
            mobileNumber = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_mobile_number_verification, container, false);


        verifyCodeButton = (Button) rootView.findViewById(R.id.btn_submit_code);

        verifyCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verficationCode =  ((EditText)rootView.findViewById(R.id.input_code)).getText().toString();
                if(verficationCode.length()==0){
                    Toast.makeText(getActivity(), "Code not Entered", Toast.LENGTH_LONG).show();
                }
                else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verficationCode);
                    mListener.loginFix(credential, mobileNumber);
                }

                
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEnterVerificationFragmentInteractionListener) {
            mListener = (OnEnterVerificationFragmentInteractionListener) context;
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
    public interface OnEnterVerificationFragmentInteractionListener {
        void loginFix(PhoneAuthCredential credential, String mob);
    }
}
