package com.avow.bhanu.feedme.fragments.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.util.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRegistrationFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MobileNumberRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MobileNumberRegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "abc";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootView;
    Button sendCodeButton;
    String mobileNumber;
    private Boolean verificationInprocess;

    private OnRegistrationFragmentInteractionListener mListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog progressDialog;
    private String countryCode;

    public MobileNumberRegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MobileNumberRegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MobileNumberRegistrationFragment newInstance(Boolean flag, String param1, String param2) {
        MobileNumberRegistrationFragment fragment = new MobileNumberRegistrationFragment();
        Bundle args = new Bundle();

        args.putBoolean("verificationInprocessFlag", flag);
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
            verificationInprocess = getArguments().getBoolean("verificationInprocessFlag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_mobile_number_registration, container, false);


        ((EditText)rootView.findViewById(R.id.input_mobile)).requestFocus();
        sendCodeButton = (Button) rootView.findViewById(R.id.btn_seend_code);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileNumber =  ((EditText)rootView.findViewById(R.id.input_mobile)).getText().toString();

                countryCode = ((EditText)rootView.findViewById(R.id.input_country_code)).getText().toString();

                mobileNumber = Utils.parsePhoneNumber(mobileNumber);


                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Verifying ....");
                progressDialog.setCancelable(false);
                progressDialog.show();


                System.out.println("+91" + mobileNumber);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        countryCode+mobileNumber,        // Phone number to verify
                        10,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        getActivity(),               // Activity (for callback binding)
                        mCallbacks);

/*                if (mListener != null && !verificationInprocess) {

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Verifying ....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mobileNumber,        // Phone number to verify
                            30,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            getActivity(),               // Activity (for callback binding)
                            mCallbacks);


                    mListener.setVerificationInProcessFlag(true);

                }*/
            }
        });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                credential.getSmsCode();

                Toast.makeText(getActivity(), "Verification Successful", Toast.LENGTH_LONG).show();

                progressDialog.dismiss();


                mListener.setCodeInTextView(credential.getSmsCode());

                mListener.setVerificationInProcessFlag(false);

                mListener.login(credential, mobileNumber);
            }


            @Override
            public void onVerificationFailed(FirebaseException e) {

                mListener.setVerificationInProcessFlag(false);


                progressDialog.dismiss();

                Toast.makeText(getActivity(), "Verification failed", Toast.LENGTH_LONG).show();
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                progressDialog.dismiss();
                getFragmentManager().
                        beginTransaction().
                        replace(R.id.login_fragment_container, EnterVerificationCodeFragment.newInstance(mVerificationId,  mobileNumber), "enter_code_fragment").
                        addToBackStack("mobile_number_registration_fragment").
                        commit();

/*
                progressDialog.hide();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Waiting for SMS....");
                progressDialog.setCancelable(false);
                progressDialog.show();
*/



                // ...
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                progressDialog.hide();
                Toast.makeText(getActivity(), "Counld not detect SMS, enter code manually", Toast.LENGTH_LONG).show();
                getFragmentManager().
                        beginTransaction().
                        replace(R.id.login_fragment_container, EnterVerificationCodeFragment.newInstance(mVerificationId,  mobileNumber), "enter_code_fragment").
                        addToBackStack("mobile_number_registration_fragment").
                        commit();

            }
        };

        // Inflate the layout for this fragment
        return rootView;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegistrationFragmentInteractionListener) {
            mListener = (OnRegistrationFragmentInteractionListener) context;
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
    public interface OnRegistrationFragmentInteractionListener {

        void setVerificationInProcessFlag(Boolean flag);
        void login(PhoneAuthCredential credential, String mob);

        void setCodeInTextView(String smsCode);
    }
}
