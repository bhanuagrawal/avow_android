package com.avow.bhanu.feedme.fragments.home;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.model.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDialogBox extends DialogFragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "dialog fragment";

    ArrayList<User> receivers_array;
    Boolean show_info;

    // TODO: Rename and change types and number of parameters
    public static MessageDialogBox newInstance(ArrayList<User> param1, Boolean param2) {
        MessageDialogBox fragment = new MessageDialogBox();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receivers_array = (ArrayList<User>) getArguments().getSerializable(ARG_PARAM1);
            show_info = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    public interface MessageDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String message, ArrayList<User> receivers_array, Boolean show_info);
        public void onCancelDialogInterface(DialogInterface dialog);
    }

    // Use this instance of the interface to deliver action events
    MessageDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the MessageDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MessageDialogListener so we can send events to the host
            mListener = (MessageDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MessageDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        View view = inflater.inflate(R.layout.fragment_message_dialog_box, null);
        builder.setView(view);


        final EditText messageEt = (EditText) view.findViewById(R.id.message);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(MessageDialogBox.this, messageEt.getText().toString(), receivers_array, show_info);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MessageDialogBox.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.onCancelDialogInterface(dialog);
    }
}
