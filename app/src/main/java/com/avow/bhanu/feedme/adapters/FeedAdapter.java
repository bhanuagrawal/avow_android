package com.avow.bhanu.feedme.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.fragments.home.MessageDialogBox;
import com.avow.bhanu.feedme.model.Post;
import com.avow.bhanu.feedme.util.MyBaseAdapter3;
import com.avow.bhanu.feedme.util.Setting;
import com.avow.bhanu.feedme.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by bhanu on 12/9/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private final Context context;
    private List<Post> posts;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView postIv;
        public LinearLayout postLayout;
        public ImageView replyIv;
        public ImageView infoIv;
        

        public MyViewHolder(View view) {
            super(view);

            postIv = (ImageView) view.findViewById(R.id.post_imageView);
            postLayout = (LinearLayout) view.findViewById(R.id.post_layout);
            replyIv = (ImageView) view.findViewById(R.id.reply);
            infoIv = (ImageView) view.findViewById(R.id.info);
        }
    }


    public FeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_post, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Post post = posts.get(position);

        String imageUrl;

        if(post.getImage() == null){
            imageUrl = Setting.server + post.getImage_url();
        }
        else {
            imageUrl = Setting.server + post.getImage();
        }

        Picasso.with(context).load(imageUrl).into(holder.postIv);

        if(post.getReceivers_count() > 2){
            holder.replyIv.setImageResource(R.drawable.replyall);
        }
        else{
            holder.replyIv.setImageResource(R.drawable.reply);
        }


        holder.replyIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DialogFragment newFragment = MessageDialogBox.newInstance(post.getReceivers(), post.getShow_info());
                newFragment.show(((FragmentActivity)context).getSupportFragmentManager(), "message");

            }
        });

        try {
            Collections.sort(post.getReceivers(), new Utils.NameComparator());
        }
        catch (Exception e){

        }


        if(post.getShow_info() && post.getReceivers_count()>2){
            holder.infoIv.setVisibility(View.VISIBLE);
        }
        else{
            holder.infoIv.setVisibility(View.GONE);
        }

        holder.infoIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyBaseAdapter3 adapter = new MyBaseAdapter3(context, (Activity)context , post.getReceivers());


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
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
