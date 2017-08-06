package com.bukup.gauravpc.noteit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bukup.gauravpc.noteit.About.AboutUs;
import com.bukup.gauravpc.noteit.BucketList.BucketListMain;
import com.bukup.gauravpc.noteit.dailyDiary.ViewDiary;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gaurav Sharma on 28-05-2017.
 */
public class Fragment1 extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private ImageView avatarImageView;
    private TextView usernameTextView,emailTextView;
    private String username_val,email_val,avatar_val,user_id;
    private CardView logoutCard;

    private ViewPager viewPager;
    private RelativeLayout Layout1,Layout2,Layout3,Layout4;
    private String data1,data2,data3;
    private android.os.Handler handler;
    private Cursor cursor1,cursor2,cursor3;
    private ProgressBar progressBar;
    private Switch syncSwitch,pinSwitch;
    private RelativeLayout backupAndSyncRelativeLayout,shareRelativeLayout,rateRelativeLayout,feedbackRelativeLayout,aboutUsRelativeLayout;
    private TextView syncStatusTextView,syncDateTextView,restoreTextView;
    private String syncAllowed,lastSyncDate;
    private ImageView backupAndSyncInfoImageView;
    private String isPinSet;

    private SharedPreferences sharedPreferences;

    private GoogleApiClient googleApiClient;

    private int restore=0;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment1, null);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Restoring data...");
        progressDialog.setCancelable(false);

        sharedPreferences=getActivity().getSharedPreferences("details", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(),this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        user_id=sharedPreferences.getString("user_id"," ");Log.d("user_id",user_id);
        username_val=sharedPreferences.getString("username", "");
        email_val=sharedPreferences.getString("email", "");
        avatar_val=sharedPreferences.getString("avatar", "");
        syncAllowed=sharedPreferences.getString("syncAllowed", "");
        lastSyncDate=sharedPreferences.getString("lastSyncDate", "");
        isPinSet=sharedPreferences.getString("pin", "");

        if(syncAllowed.isEmpty()){
            editor.putString("syncAllowed","no");
            syncAllowed="no";
        }
        if(lastSyncDate.isEmpty()){
            editor.putString("lastSyncDate", "");
        }
        editor.apply();

        usernameTextView=(TextView)v.findViewById(R.id.username);usernameTextView.setText(username_val);
        emailTextView=(TextView)v.findViewById(R.id.email);emailTextView.setText(email_val);
        avatarImageView=(ImageView)v.findViewById(R.id.img);
        if(avatar_val.equals("null")){
            avatarImageView.setImageResource(R.drawable.avatar);
        }else {
            Glide.with(this).load(avatar_val).into(avatarImageView); //glide to put google profile pic into imageView
        }

//        DatabaseHandler db=new DatabaseHandler(getActivity());
//        int count1=db.getCount();
//        int count2=db.getCountOfDiary();
//        int count3=db.getCountOfBucketList();

        pinSwitch=(Switch)v.findViewById(R.id.pinSwitch);
        syncSwitch=(Switch)v.findViewById(R.id.syncSwitch);
        backupAndSyncRelativeLayout=(RelativeLayout)v.findViewById(R.id.rl2);
        syncStatusTextView=(TextView)v.findViewById(R.id.syncStatus);
        syncDateTextView=(TextView)v.findViewById(R.id.syncDate);

        if(syncAllowed.equals("yes")){
            syncSwitch.setChecked(true);
            backupAndSyncRelativeLayout.setVisibility(View.VISIBLE);
            runnableTask();
        }else if(syncAllowed.equals("no")){
            syncSwitch.setChecked(false);
            backupAndSyncRelativeLayout.setVisibility(View.GONE);
        }

        if(isPinSet.equals("noPin")){
            pinSwitch.setChecked(false);
        }else{
            pinSwitch.setChecked(true);
        }
        pinSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pinSwitch.isChecked()){
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.layout_pin);

                    ImageView close=(ImageView)dialog.findViewById(R.id.cross);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                    final EditText pin1EditText,pin2EditText;
                    pin1EditText=(EditText)dialog.findViewById(R.id.pin1);
                    pin2EditText=(EditText)dialog.findViewById(R.id.pin2);

                    TextView setTextView=(TextView)dialog.findViewById(R.id.set);
                    setTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(pin1EditText.getText().toString().length()<1){
                                Toast.makeText(getActivity(), "Please enter PIN", Toast.LENGTH_SHORT).show();
                            }else{
                                if(pin2EditText.getText().toString().length()<1){
                                    Toast.makeText(getActivity(), "Please enter PIN again", Toast.LENGTH_SHORT).show();
                                }else{
                                    if(pin1EditText.getText().toString().length()==4){
                                        isPinSet=pin1EditText.getText().toString();
                                        if (pin1EditText.getText().toString().equals(pin2EditText.getText().toString())) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("pin", pin1EditText.getText().toString());
                                            editor.apply();
                                            Toast.makeText(getActivity(), "Pin is set. You will be asked on every startup.", Toast.LENGTH_SHORT).show();
                                            pinSwitch.setChecked(true);
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(getActivity(), "Pin does not matched!", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("pin", "noPin");
                                            editor.apply();
                                        }
                                    }else{
                                        Toast.makeText(getActivity(), "Pin must be of 4 digits!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                    if(isPinSet.equals("noPin")){
                        pinSwitch.setChecked(false);
                    }
                }else{
                    Log.e("here","heeh");


                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.layout_pin);
                    TextView t1=(TextView)dialog.findViewById(R.id.t1);
                    t1.setText("Remove PIN");
                    ImageView close=(ImageView)dialog.findViewById(R.id.cross);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    final EditText pin1EditText,pin2EditText;
                    pin1EditText=(EditText)dialog.findViewById(R.id.pin1);
                    pin2EditText=(EditText)dialog.findViewById(R.id.pin2);pin2EditText.setVisibility(View.GONE);

                    TextView setTextView=(TextView)dialog.findViewById(R.id.set);
                    setTextView.setText("Remove PIN");
                    setTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pin1EditText.getText().toString().equals(isPinSet)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("pin", "noPin");
                                editor.apply();
                                Toast.makeText(getActivity(), "Pin Removed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Wrong PIN", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
//        pinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    final Dialog dialog = new Dialog(getActivity());
//                    dialog.setContentView(R.layout.layout_pin);
//
//                    ImageView close=(ImageView)dialog.findViewById(R.id.cross);
//                    close.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//
//                    final EditText pin1EditText,pin2EditText;
//                    pin1EditText=(EditText)dialog.findViewById(R.id.pin1);
//                    pin2EditText=(EditText)dialog.findViewById(R.id.pin2);
//
//                    TextView setTextView=(TextView)dialog.findViewById(R.id.set);
//                    setTextView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if(pin1EditText.getText().toString().length()<1){
//                                Toast.makeText(getActivity(), "Please enter PIN", Toast.LENGTH_SHORT).show();
//                            }else{
//                                if(pin2EditText.getText().toString().length()<1){
//                                    Toast.makeText(getActivity(), "Please enter PIN again", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    isPinSet=pin1EditText.getText().toString();
//                                    if (pin1EditText.getText().toString().equals(pin2EditText.getText().toString())) {
//                                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                                        editor.putString("pin", pin1EditText.getText().toString());
//                                        editor.apply();
//                                        Toast.makeText(getActivity(), "Pin is set. You will be asked on every startup.", Toast.LENGTH_SHORT).show();
//                                        pinSwitch.setChecked(true);
//                                        dialog.dismiss();
//                                    } else {
//                                        Toast.makeText(getActivity(), "Pin does not matched!", Toast.LENGTH_SHORT).show();
//                                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                                        editor.putString("pin", "noPin");
//                                        editor.apply();
//                                    }
//                                }
//                            }
//                        }
//                    });
//                    if(isPinSet.equals("noPin")){
//                        pinSwitch.setChecked(false);
//                    }
//                }else{
//                    final Dialog dialog = new Dialog(getActivity());
//                    dialog.setContentView(R.layout.layout_pin);
//                    TextView t1=(TextView)dialog.findViewById(R.id.t1);
//                    t1.setText("Remove PIN");
//                    ImageView close=(ImageView)dialog.findViewById(R.id.cross);
//                    close.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    final EditText pin1EditText,pin2EditText;
//                    pin1EditText=(EditText)dialog.findViewById(R.id.pin1);
//                    pin2EditText=(EditText)dialog.findViewById(R.id.pin2);pin2EditText.setVisibility(View.GONE);
//
//                    TextView setTextView=(TextView)dialog.findViewById(R.id.set);
//                    setTextView.setText("Remove PIN");
//                    setTextView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (pin1EditText.getText().toString().equals(isPinSet)) {
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("pin", "noPin");
//                                editor.apply();
//                                Toast.makeText(getActivity(), "Pin Removed", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            } else {
//                                Toast.makeText(getActivity(), "Wrong PIN", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });

        syncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    //update shared preference
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("syncAllowed","yes");
                    editor.apply();

                    runnableTask(); //sync to the server
                    backupAndSyncRelativeLayout.setVisibility(View.VISIBLE);
                }else{
                    //update shared preference
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("syncAllowed", "no");
                    editor.apply();

                    backupAndSyncRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

        viewPager=(ViewPager)getActivity().findViewById(R.id.viewpager);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Layout1=(RelativeLayout)v.findViewById(R.id.l1);
        Layout2=(RelativeLayout)v.findViewById(R.id.l2);
        Layout3=(RelativeLayout)v.findViewById(R.id.l3);
        Layout4=(RelativeLayout)v.findViewById(R.id.l4);

        Layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        Layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ViewDiary.class);
                startActivityForResult(intent, 1);
            }
        });
        Layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BucketListMain.class);
                startActivityForResult(intent,1);
            }
        });
        Layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(),"Slam Book coming Soon!",Toast.LENGTH_SHORT).show();
//                Calendar cal = Calendar.getInstance();
//                Intent intent = new Intent(Intent.ACTION_EDIT);
//                intent.setType("vnd.android.cursor.item/event");
//                intent.putExtra("beginTime", cal.getTimeInMillis());
//                intent.putExtra("allDay", true);
//                intent.putExtra("rrule", "FREQ=DAILY");
//                intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
//                intent.putExtra("title", "From Buckup");
//                intent.putExtra("description", "hello From Buckup");
//                startActivity(intent);

                Intent i=new Intent(getActivity(),reminders.class);
                startActivity(i);
            }
        });

        //4 operations
        shareRelativeLayout=(RelativeLayout)v.findViewById(R.id.shareLayout);
        rateRelativeLayout=(RelativeLayout)v.findViewById(R.id.rateLayout);
        feedbackRelativeLayout=(RelativeLayout)v.findViewById(R.id.feedbackLayout);
        aboutUsRelativeLayout=(RelativeLayout)v.findViewById(R.id.aboutUsLayout);

        shareRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I just found an awesome app. Its called BUCKUP.\n\nIt let's you capture and organize all your stuffs and memories in a nutshell.\n\nDownload Now!\nhttps://goo.gl/bo4CHg");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        rateRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.bukup.gauravpc.noteit")));
            }
        });
        feedbackRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        aboutUsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AboutUs.class);
                startActivity(intent);
            }
        });


        backupAndSyncInfoImageView=(ImageView)v.findViewById(R.id.backupAndSyncInfo);
        backupAndSyncInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.layout_back_and_sync_info);
                ImageView close=(ImageView)dialog.findViewById(R.id.cross);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });



        logoutCard=(CardView)v.findViewById(R.id.info4);
        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {

                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("details", Context.MODE_APPEND);
                                        sharedPreferences.edit().clear().apply();

                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        restoreTextView=(TextView)v.findViewById(R.id.restore);
        restoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getActivity());
                }
                builder.setTitle("Restore Data")
                        .setMessage("Are you sure you want to restore all your data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                restore=1;
                                progressDialog.show();
                                backup();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");
                if (status.equals("update")) {
                    runnableTask();
                    TextView countNotes,countDiary,countBL;
                    DatabaseHandler db=new DatabaseHandler(getActivity());
                    FragmentActivity v=getActivity();
                    int count1=db.getCount();
                    int count2=db.getCountOfDiary();
                    int count3=db.getCountOfBucketList();
                    countNotes=(TextView)v.findViewById(R.id.count_notes);
                    countDiary=(TextView)v.findViewById(R.id.count_diary);
                    countBL=(TextView)v.findViewById(R.id.count_bucket_list);
                    countNotes.setText(""+count1);
                    countDiary.setText(""+count2);
                    countBL.setText("" + count3);
                }
            }
        }
    }

    public void runnableTask(){
        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //sync data through a runnable thread on startup

        handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                backup();
            }
        };
        handler.post(runnable);

        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    }

    public void get(){
        Log.d("name", "gaurav");
    }
    //backup all  the data to online database
    public void backup() {
        new JSONTask().execute();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class JSONTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            syncStatusTextView.setText("Sync in Progress...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            DatabaseHandler db=new DatabaseHandler(getActivity());

                    cursor1=db.getNotesData();
                    cursor2=db.getDiaryData();
                    cursor3=db.getBucketListData();

            JSONObject  jsonObject1,
                        jsonObject2,
                        jsonObject3;

            JSONArray   jsonArray1=new JSONArray(),
                        jsonArray2=new JSONArray(),
                        jsonArray3=new JSONArray();

            //data1
            if(cursor1.moveToFirst()){
                do{
                    jsonObject1=new JSONObject();
                    try {

                        jsonObject1.put("id",cursor1.getString(0));
                        jsonObject1.put("title",cursor1.getString(3));
                        jsonObject1.put("body",cursor1.getString(4));
                        jsonObject1.put("created_on",cursor1.getString(1));
                        jsonObject1.put("updated_on",cursor1.getString(2));
                        jsonObject1.put("tag",cursor1.getString(5));
                        jsonObject1.put("location",cursor1.getString(6));
                        jsonArray1.put(jsonObject1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }while(cursor1.moveToNext());
            }
            jsonObject1=new JSONObject();
            try {

                jsonObject1.put("data",jsonArray1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            data1=jsonObject1.toString();

            //data2
            if(cursor2.moveToFirst()){
                do{
                    jsonObject2=new JSONObject();
                    try {

                        jsonObject2.put("id",cursor2.getString(0));
                        jsonObject2.put("title",cursor2.getString(2));
                        jsonObject2.put("body",cursor2.getString(3));
                        jsonObject2.put("date",cursor2.getString(1));
                        jsonArray2.put(jsonObject2);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }while(cursor2.moveToNext());
            }
            jsonObject2=new JSONObject();
            try {

                jsonObject2.put("data",jsonArray2);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            data2=jsonObject2.toString();


            //data3
            if(cursor3.moveToFirst()){
                do{
                    jsonObject3=new JSONObject();
                    try {

                        jsonObject3.put("id",cursor3.getString(0));
                        jsonObject3.put("title",cursor3.getString(1));
                        jsonObject3.put("body",cursor3.getString(2));
                        jsonObject3.put("target_date",cursor3.getString(3));
                        jsonObject3.put("created_on",cursor3.getString(4));
                        jsonObject3.put("updated_on",cursor3.getString(5));
                        jsonObject3.put("cat_id",cursor3.getString(6));
                        jsonArray3.put(jsonObject3);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }while(cursor3.moveToNext());
            }
            jsonObject3=new JSONObject();
            try {

                jsonObject3.put("data",jsonArray3);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            data3=jsonObject3.toString();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sendDataToServer();
        }
    }
    public void sendDataToServer(){
        String url="https://buckupapp.herokuapp.com/backup";
        RequestQueue requestQueue=new Volley().newRequestQueue(getActivity());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        //make all the synced 0 field to 1
                        DatabaseHandler db=new DatabaseHandler(getActivity());
                        SQLiteDatabase sql_db = db.getWritableDatabase();
                        if(cursor1.moveToFirst()){
                            do{
                                sql_db.execSQL("UPDATE notes SET isSynced = '1' WHERE id= "+cursor1.getString(0));
                            }while(cursor1.moveToNext());
                        }
                        if(cursor2.moveToFirst()){
                            do{
                                sql_db.execSQL("UPDATE diary SET isSynced = '1' WHERE id= "+cursor2.getString(0));
                            }while(cursor2.moveToNext());
                        }
                        if(cursor3.moveToFirst()){
                            do{
                                sql_db.execSQL("UPDATE bucket_list SET isSynced = '1' WHERE id= "+cursor3.getString(0));
                            }while(cursor3.moveToNext());
                        }

                        DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
                        DateFormat df_time = new SimpleDateFormat("HH:mm:ss a");
                        Date date = new Date();
                        String noteDate=parseDate(df.format(date));
                        String time=df_time.format(date);

                        syncStatusTextView.setText("Sync Successful");
                        syncStatusTextView.setTextColor(Color.parseColor("#2e7d32"));
                        syncDateTextView.setText("Last Synced on: " + noteDate + " @" + time);


                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("lastSyncDate","Last Synced on: " + noteDate + " @" + time);
                        editor.apply();

                        Log.d("sync status","successful");

                        if(restore==1){
                            restore();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                syncStatusTextView.setText("Sync Failed");
                syncStatusTextView.setTextColor(Color.parseColor("#ab0303"));
                syncDateTextView.setText(lastSyncDate);
                Log.d("sync status", "failed");
                Log.d("error", "" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("user_id",user_id);
                params.put("notesData",data1);
                params.put("diaryData",data2);
                params.put("bucketListData",data3);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void restore(){
        DatabaseHandler db=new DatabaseHandler(getActivity());
        db.truncateTables();

        String url="https://buckupapp.herokuapp.com/restore";
        RequestQueue requestQueue=new Volley().newRequestQueue(getActivity());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            JSONObject notesObject=jsonObject.getJSONObject("notes_data");
                            JSONObject diaryObject=jsonObject.getJSONObject("diary_data");
                            JSONObject blObject=jsonObject.getJSONObject("bl_data");

                            JSONArray notesArray = notesObject.getJSONArray("data");
                            JSONArray diaryArray = diaryObject.getJSONArray("data");
                            JSONArray blArray = blObject.getJSONArray("data");

                            DatabaseHandler db=new DatabaseHandler(getActivity());
                            SQLiteDatabase sql_db = db.getWritableDatabase();

                            //insert values into notes table
                            Log.d("length",""+notesArray.length());
                            for(int i=0;i<notesArray.length();i++){
                                JSONObject o = notesArray.getJSONObject(i);
                                ContentValues contentValues=new ContentValues();
                                contentValues.put("id",Integer.parseInt(o.getString("id")));
                                contentValues.put("created_on",o.getString("created_on"));
                                contentValues.put("updated_on",o.getString("updated_on"));
                                contentValues.put("title",o.getString("title"));
                                contentValues.put("data",o.getString("body"));
                                contentValues.put("tag",o.getString("tag"));
                                contentValues.put("location",o.getString("location"));
                                contentValues.put("isSynced", "1");
                                // Inserting Row
                                sql_db.insert("notes", null, contentValues);
                            }

                            //insert values into diary table
                            for(int i=0;i<diaryArray.length();i++){
                                JSONObject o = diaryArray.getJSONObject(i);
                                ContentValues contentValues=new ContentValues();
                                contentValues.put("id",Integer.parseInt(o.getString("id")));
                                contentValues.put("date",o.getString("date"));
                                contentValues.put("title",o.getString("title"));
                                contentValues.put("data",o.getString("body"));
                                contentValues.put("isSynced", "1");
                                // Inserting Row
                                sql_db.insert("diary", null, contentValues);
                            }

                            //insert values into bucket_list table
                            for(int i=0;i<blArray.length();i++){
                                JSONObject o = blArray.getJSONObject(i);

                                ContentValues contentValues=new ContentValues();
                                contentValues.put("id",Integer.parseInt(o.getString("id")));
                                contentValues.put("title",o.getString("title"));
                                contentValues.put("desc",o.getString("body"));
                                contentValues.put("start_date", o.getString("target_date"));
                                contentValues.put("created_on", o.getString("created_on"));
                                contentValues.put("updated_on", o.getString("updated_on"));
                                contentValues.put("cat_id",o.getString("cat_id"));
                                contentValues.put("isSynced", "1");
                                // Inserting Row
                                sql_db.insert("bucket_list", null, contentValues);
                            }

                            restore=0;
                            Intent intent=new Intent(getActivity(),MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("user_id",user_id);
                return params;
            }
        };
        int socketTimeout = 10000;//30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public String parseDate(String date){
        DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        Date fetchedDate = null;
        try {
            fetchedDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fetchedDate);
        String day_now= ""+cal.get(Calendar.DAY_OF_MONTH);
        String day_now_ordinal=ordinal(cal.get(Calendar.DAY_OF_MONTH));
        String month_now = month_date.format(cal.getTime())+"";
        String year_now= String.valueOf(cal.get(Calendar.YEAR));

        return day_now+""+day_now_ordinal+" "+month_now+" "+year_now;
    }
    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return suffixes[i % 10];

        }
    }
}
