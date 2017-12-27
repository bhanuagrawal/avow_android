package com.avow.bhanu.feedme.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.model.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bhanu on 30/5/17.
 */

public class MyBaseAdapter3 extends BaseAdapter {

    ArrayList<User> user_array;
    LayoutInflater inflater;
    Context context;
    Activity activity;
    HashMap<String, String> contacts;


    public MyBaseAdapter3(Context context, Activity activity, ArrayList user_array) {
        this.user_array = user_array;
        this.context = context;
        this.activity = activity;
        inflater = LayoutInflater.from(this.context);
        try {
            contacts = new ContactsManager(context, activity).getAllContacts();
        }
        catch (Exception e){
            contacts = new HashMap<String, String>();
        }

    }

    @Override
    public int getCount() {
        return user_array.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_user, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }



        mViewHolder.userTv.setText(user_array.get(position).getName());
        mViewHolder.usernameTv.setText(user_array.get(position).getUsername());




        return convertView;
    }

    public void select(String username){

    }

    public void deSelect(String username){

    }


    private class MyViewHolder {
        TextView userTv, usernameTv;

        public MyViewHolder(View item) {
            userTv = (TextView) item.findViewById(R.id.name_textview_profile);
            usernameTv = (TextView) item.findViewById(R.id.username_textview);
        }
    }
}