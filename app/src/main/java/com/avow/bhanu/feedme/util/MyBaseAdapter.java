package com.avow.bhanu.feedme.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avow.bhanu.feedme.R;

import java.util.ArrayList;

/**
 * Created by bhanu on 30/5/17.
 */

public class MyBaseAdapter extends BaseAdapter {

    ArrayList name_arr = new ArrayList();
    ArrayList username_arr = new ArrayList();
    LayoutInflater inflater;
    Context context;


    public MyBaseAdapter(Context context, ArrayList name_arr, ArrayList username_arr ) {
        this.name_arr = name_arr;
        this.username_arr = username_arr;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return name_arr.size();
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


        mViewHolder.userTv.setText(name_arr.get(position).toString());
        mViewHolder.usernameTv.setText(username_arr.get(position).toString());

        return convertView;
    }

    private class MyViewHolder {
        TextView userTv, usernameTv;

        public MyViewHolder(View item) {
            userTv = (TextView) item.findViewById(R.id.name_textview_profile);
            usernameTv = (TextView) item.findViewById(R.id.username_textview);
        }
    }
}