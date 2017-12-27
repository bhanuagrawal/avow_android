package com.avow.bhanu.feedme.util;

/**
 * Created by bhanu on 1/6/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.avow.bhanu.feedme.R;
import com.avow.bhanu.feedme.model.User;
import com.google.firebase.iid.FirebaseInstanceId;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {

            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                //Read byte from input stream

                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;

                //Write byte from output stream
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static String createCompressedImage(String path){
        return path;
    }

    public static void deleteImage(String path){

    }

    public static String createImageFromText(String text, Context context) throws IOException {


/*        final Paint textPaint = new Paint() {
            {
                setColor(Color.WHITE);
                setTextAlign(Paint.Align.LEFT);
                setTextSize(50f);
                setAntiAlias(true);
            }
        };*/



//        String[] messageLines = new String[0];
//        messageLines = text.split("\n");








        TextPaint mTextPaint=new TextPaint();

        mTextPaint.setTextSize(40);


        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/micross.ttf");
        mTextPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/g1.ttf"));
        mTextPaint.setColor(Color.BLACK);
        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint, 490, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        final Bitmap bmp = Bitmap.createBitmap(500, mTextLayout.getHeight()>300?mTextLayout.getHeight()+100:300, Bitmap.Config.RGB_565); //use ARGB_8888 for better quality
        final Canvas canvas = new Canvas(bmp);

        Bitmap bgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.paper);
        bgBitmap = Bitmap.createScaledBitmap(bgBitmap, canvas.getWidth(),canvas.getHeight(),true);
        canvas.drawBitmap(bgBitmap, 0, 0, null);

/*        Paint gradPaint = new Paint();
        gradPaint.setShader(new LinearGradient(0,0,0,canvas.getHeight(),Color.rgb(208, 219, 237),Color.rgb(204, 212, 226), Shader.TileMode.CLAMP));
        canvas.drawPaint(gradPaint);*/
        //canvas.drawColor(Color.rgb(255, 226, 170));
        //canvas.drawColor(Color.WHITE);
        canvas.save();


        canvas.translate(5, (canvas.getHeight()-mTextLayout.getHeight())/2);
        mTextLayout.draw(canvas);
        canvas.restore();

/*
        int j=0;
        for(String messageLine: messageLines){
            j++;
            canvas.drawText(messageLine, 10, textPaint.getTextSize()*j, textPaint);
        }*/


        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/FeedMe";
        File dir = new File(file_path);


        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, "message" + ts  + ".png");

        FileOutputStream fOut = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 20, fOut);
        bmp.recycle();
        fOut.close();

        return file.getAbsolutePath();
    }

    public static String parsePhoneNumber(String number){

        //System.out.println(number);

        number = number.replace("+91", "");
        number = number.replaceAll("[^0-9]+", "");


        if(number.length() > 10){
            number = number.substring(number.length()-10, number.length());
        }

        return number;

    }

    public static void uploadImage(String filePath, Context context, String username, ArrayList<User> receivers_array, Boolean show_info){

        String url = Setting.server + "/feed/image/";
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            String receivers = "";
            for(User receiver : receivers_array){
                receivers += (receiver.getUsername() + " ");
            }


            receivers += username;
            //receivers = receivers.substring(1, receivers.length());



            Log.d("receivers", receivers);
            String imagePath = Utils.createCompressedImage(filePath);
            new MultipartUploadRequest(context, uploadId, url)
                    .addFileToUpload(imagePath, "file") //Adding file
                    .addParameter("username", username)
                    .addParameter("show_info", String.valueOf(show_info))
                    .addParameter("receivers", receivers)//Adding text parameter to the request
                    .addHeader("token", Utils.getMobileNumber(context) + " " + Utils.getDeviceID(context))
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .startUpload(); //Starting the upload

            Utils.deleteImage(imagePath);



        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public static class UserNameComparator implements Comparator<User> {
        @Override
        public int compare(User o1, User o2) {
            int i = o1.getStatus().compareToIgnoreCase(o2.getStatus());
            if(i!=0) return i;
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }



    public static class NameComparator implements Comparator<User> {
        @Override
        public int compare(User o1, User o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }


    public static void  saveDevice(String username, final String device_id, final Activity activity){



        String device_save_URL = Setting.server + "/users/device/";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", username);
        params.put("device_id", device_id);

        JsonObjectRequest req = new JsonObjectRequest(device_save_URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if(response.getBoolean("success")){


                                saveDeviceID(activity.getApplicationContext(), device_id);

                                //Toast.makeText(activity, device_id, Toast.LENGTH_LONG).show();
                            }
                            else{
                                //    Toast.makeText(Home.this, "Some error occured in adding device", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(activity);
        rQueue.add(req);

    }

    public static void saveDeviceID(Context context, String device_id) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("device_id",  device_id);
        editor.commit();
    }

    public static String getDeviceID(Context context) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String device_id = settings.getString("device_id", null);
        Log.d("New token", FirebaseInstanceId.getInstance().getToken());
        if(device_id!=null){
            Log.d("Old token", device_id);
            return device_id;
        }
        else {
            return FirebaseInstanceId.getInstance().getToken();
        }
    }

    public static String getMobileNumber(Context context) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return  settings.getString("username", null);
    }



}