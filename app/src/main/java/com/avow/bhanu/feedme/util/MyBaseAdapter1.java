package com.avow.bhanu.feedme.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.fragments.home.MessageDialogBox;
import com.avow.bhanu.feedme.model.User;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bhanu on 30/5/17.
 */

public class MyBaseAdapter1 extends BaseAdapter {

    private static final String TAG = MyBaseAdapter1.class.getSimpleName();;
    ArrayList image_arr = new ArrayList();
    ArrayList username_array;
    ArrayList<Boolean> show_info_array;
    ArrayList<ArrayList<User>> receivers_array;
    LayoutInflater inflater;
    Context context;
    public ImageLoader imageLoader;
    String loggedInUser;

    public MyBaseAdapter1(Context context, ArrayList receivers_array, ArrayList image_arr, ArrayList username_array, ArrayList show_info_array, String loggedInUser ) {
        this.image_arr = image_arr;
        this.username_array = username_array;
        this.receivers_array = receivers_array;
        this.show_info_array = show_info_array;
        this.loggedInUser = loggedInUser;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return image_arr.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_post, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }


        //mViewHolder.postLayout.setBackgroundColor(Color.parseColor("#E5E4E9"));

        //imageLoader.DisplayImage(image_arr.get(position).toString(), mViewHolder.postIv);

        Picasso.with(context).load(image_arr.get(position).toString()).into(mViewHolder.postIv);

        if(receivers_array.get(position).size() > 2){
            mViewHolder.replyIv.setImageResource(R.drawable.replyall);
        }
        else{
            mViewHolder.replyIv.setImageResource(R.drawable.reply);
        }


        mViewHolder.replyIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DialogFragment newFragment = MessageDialogBox.newInstance(receivers_array.get(position), show_info_array.get(position));
                newFragment.show(((FragmentActivity)context).getSupportFragmentManager(), "message");


            }
        });



        Collections.sort(receivers_array.get(position), new Utils.UserNameComparator());

        if(show_info_array.get(position) && receivers_array.get(position).size()>2){
           mViewHolder.infoIv.setVisibility(View.VISIBLE);
        }
        else{
            mViewHolder.infoIv.setVisibility(View.GONE);
        }

        mViewHolder.infoIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyBaseAdapter3 adapter = new MyBaseAdapter3(context, (Activity)context , receivers_array.get(position));


                new AlertDialog.Builder(context)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO - Code when list item is clicked (int which - is param that gives you the index of clicked item)
                            }
                        })
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle("Sender & Receivers")
                        .setCancelable(false)
                        .show();


            }
        });

        /*new DownloadImageTask(mViewHolder.postIv)
                .execute(image_arr.get(position).toString());*/

        return convertView;
    }

    private class MyViewHolder {
        ImageView postIv;
        LinearLayout postLayout;
        ImageView replyIv;
        ImageView infoIv;

        public MyViewHolder(View item) {
            postIv = (ImageView) item.findViewById(R.id.post_imageView);
            postLayout = (LinearLayout) item.findViewById(R.id.post_layout);
            replyIv = (ImageView) item.findViewById(R.id.reply);
            infoIv = (ImageView) item.findViewById(R.id.info);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

