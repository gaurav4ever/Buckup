package com.bukup.gauravpc.noteit.Notes;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.bukup.gauravpc.noteit.BucketList.BucketList_addItem;
import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.Fragment1;
import com.bukup.gauravpc.noteit.MainActivity;
import com.bukup.gauravpc.noteit.Models.notesModel;
import com.bukup.gauravpc.noteit.MyLocListener;
import com.bukup.gauravpc.noteit.R;
import com.bumptech.glide.load.ImageHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.bukup.gauravpc.noteit.R.id.date;

public class edit_note extends AppCompatActivity {

    String user_id,id_val,tag,location_val;
    EditText noteText_title,noteText_data;
    String title="",body="";
    String titleBeforeChange="",titleAfterChange="",bodyBeforeAnyChange="",bodyAfterAnyChange="";
    private android.os.Handler handler;
    String title_val,body_val;
    RelativeLayout saveLayout,audioLayout,shareLayout;
    ImageView tickImageView,locationImageView,deleteImageView,tagImageView,infoImageView;
    SharedPreferences sharedPreferences;
    int isTagSet=0,tagChanged=0;
    String target,address_val;
    TextView locationTextView;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        user_id=sharedPreferences.getString("user_id", " ");

        pDialog = new ProgressDialog(edit_note.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        Intent intent=getIntent();

        id_val=intent.getStringExtra("id");
        titleBeforeChange=title_val=intent.getStringExtra("title");
        bodyBeforeAnyChange=body_val=intent.getStringExtra("body");
        tag=intent.getStringExtra("tag");
        address_val=location_val=intent.getStringExtra("location");

        noteText_title=(EditText)findViewById(R.id.title);noteText_title.setText(title_val);
        noteText_data=(EditText)findViewById(R.id.body);noteText_data.setText(body_val);
        locationTextView=(TextView) findViewById(R.id.location);
        if(location_val.equals("null")){
            locationTextView.setText("Add location");
        }else{
            locationTextView.setText(location_val);
        }

        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });

        audioLayout=(RelativeLayout)findViewById(R.id.audio);
        audioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

                try {
                    startActivityForResult(i, 100);
                }catch(ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(), "Speech To Text not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shareLayout=(RelativeLayout)findViewById(R.id.share);
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=noteText_title.getText().toString();
                body=noteText_data.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+body);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

//        tickImageView=(ImageView)findViewById(R.id.tick_img);
//        tickImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        tagImageView=(ImageView)findViewById(R.id.tag_img);
        DatabaseHandler db=new DatabaseHandler(edit_note.this);
        boolean isTagSetBoolean=db.hasTag(id_val);
        if(isTagSetBoolean){
            tagImageView.setImageResource(R.drawable.tag2);
            isTagSet=1;
        }else{
            tagImageView.setImageResource(R.drawable.tag);
            isTagSet=0;
        }

        tagImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagChanged=1;
                DatabaseHandler db=new DatabaseHandler(edit_note.this);
                if(isTagSet==0){
                    isTagSet=1;
                    tagImageView.setImageResource(R.drawable.tag2);
                    db.setNoteTag(id_val);
                }else if (isTagSet==1){
                    isTagSet=0;
                    tagImageView.setImageResource(R.drawable.tag);
                    db.removeNoteTag(id_val);
                }
            }
        });

        locationImageView=(ImageView)findViewById(R.id.location_img);
        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(edit_note.this);
                dialog.setContentView(R.layout.layout_location);
                final EditText editText=(EditText)dialog.findViewById(R.id.locationText);
                if(location_val.equals("null")){
                    editText.setText("Add Location Manually");
                }else{
                    editText.setText(location_val);
                }

                ImageView imageView=(ImageView)dialog.findViewById(R.id.goBackImg);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                TextView findLocationTextView=(TextView)dialog.findViewById(R.id.findLocation);
                findLocationTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(edit_note.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(edit_note.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                //user did not gave permission or did not gave any response
                                //Ask again for permission
                                ActivityCompat.requestPermissions(edit_note.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                //Ask for permission first time
                                ActivityCompat.requestPermissions(edit_note.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        } else {
                            int off = 0;
                            try {
                                off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (off == 0) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(edit_note.this);
                                alertDialogBuilder.setMessage("We are unable to find your location because GPS is OFF... would you like to enable it ?");
                                alertDialogBuilder.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(onGPS);
                                            }
                                        });

                                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {

                                MyLocListener myLocListener = new MyLocListener(edit_note.this);
                                if (myLocListener.canGetLocation()) {
                                    double myLat = myLocListener.getLat();
                                    double myLang = myLocListener.getLang();

                                    Log.e("loc",""+myLat+" "+myLang);

                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    try {
                                        List<Address> addressList = geocoder.getFromLocation(
                                                myLat, myLang, 1);
                                        if (addressList != null && addressList.size() > 0) {
                                            Address address = addressList.get(0);
                                            StringBuilder sb = new StringBuilder();
                                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                                sb.append(address.getAddressLine(i)).append("\n");
                                            }
                                            sb.append(address.getLocality());
                                            address_val = sb.toString();
                                            Log.e("add",address_val);
                                            editText.setText(address_val);
                                        }
                                    } catch (IOException e) {
                                        Log.e("status", "Unable connect to Geocoder", e);
                                    }
                                }
                            }
                        }
                    }
                });

                TextView save=(TextView)dialog.findViewById(R.id.save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationTextView.setText(editText.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        deleteImageView=(ImageView)findViewById(R.id.delete_img);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(edit_note.this);
                dialog.setContentView(R.layout.layout_ask_before_delete);
                CardView yes,no;
                yes=(CardView)dialog.findViewById(R.id.yesLayout);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseHandler db = new DatabaseHandler(edit_note.this);
                        db.deleteNote(Integer.parseInt(id_val));
                        db.close();

                        runnable_delete(id_val);

                        Toast.makeText(getApplicationContext(),"Note Deleted",Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent();
                        intent1.putExtra("status", "deleted");
                        setResult(RESULT_OK, intent1);
                        finish();
                    }
                });
                no=(CardView)dialog.findViewById(R.id.noLayout);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        infoImageView=(ImageView)findViewById(R.id.info_img);
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(edit_note.this);
                View parentView=getLayoutInflater().inflate(R.layout.layout_info_bottom_sheet, null);

                ImageView crossImageView=(ImageView)parentView.findViewById(R.id.goBackImg);
                crossImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                TextView wordCount,created_on,updated_on,synced,tagged;
                wordCount=(TextView)parentView.findViewById(R.id.count);
                created_on=(TextView)parentView.findViewById(R.id.created_on_date);
                updated_on=(TextView)parentView.findViewById(R.id.updated_on_date);
                synced=(TextView)parentView.findViewById(R.id.isSynced);
                tagged=(TextView)parentView.findViewById(R.id.isTagged);

                DatabaseHandler db=new DatabaseHandler(edit_note.this);
                SQLiteDatabase sql = db.getWritableDatabase();

                String sql_query="SELECT * FROM notes WHERE id = "+id_val;
                Cursor cursor = sql.rawQuery(sql_query, null);
                if(cursor.moveToFirst()){
                    Log.w("date",""+cursor.getString(1));
                    wordCount.setText(""+cursor.getString(4).split(" ").length);
                    created_on.setText(parseDate(cursor.getString(1)));
                    updated_on.setText(parseDate(cursor.getString(2)));
                    if(cursor.getString(5).toString().equals("1")){
                        tagged.setText("yes");
                    }else{
                        tagged.setText("No");
                    }
                    if(cursor.getString(6).toString().equals("1")){
                        synced.setText("Yes");
                    }else{
                        synced.setText("Yes");
                    }
                }
                db.close();

                bottomSheetDialog.setContentView(parentView);
                bottomSheetDialog.show();
            }
        });

        ImageView imageView=(ImageView)findViewById(R.id.goBackImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void runnable_delete(final String id_val){
        handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                String url="https://buckupapp.herokuapp.com/backup/delete";
                RequestQueue requestQueue=new Volley().newRequestQueue(getApplicationContext());
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("status","note deleted");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("status","failed");
                        Log.d("error",""+error);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String,String>();
                        params.put("user_id",user_id);
                        params.put("table","notes");
                        params.put("id",id_val);
                        return params;
                    }
                };
                int socketTimeout = 10000;//30 seconds
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            }
        };
        handler.post(runnable);
    }
    public void updateNote(){
        title=noteText_title.getText().toString();
        body=noteText_data.getText().toString();
        if (title.length() < 1) {
            noteText_title.setText("No Title");
        }

        String noteDate;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        noteDate = df.format(date);

        final DatabaseHandler db = new DatabaseHandler(edit_note.this);
        db.updateNote(id_val, noteDate, makeFirstUpper(title),body,address_val);

        db.close();

        Intent intent = new Intent();
        intent.putExtra("status", "ok");
        setResult(RESULT_OK, intent);
        finish();
    }
    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {

        if((title.length()<1 && body.length()<1) || tagChanged==1){
            Log.d("called","haas");
            titleAfterChange=noteText_title.getText().toString();
            bodyAfterAnyChange=noteText_data.getText().toString();

            if((titleAfterChange.equals(titleBeforeChange) && bodyAfterAnyChange.equals(bodyBeforeAnyChange))){
                if(tagChanged==1){
                    Intent intent = new Intent();
                    intent.putExtra("status", "ok");
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    super.onBackPressed();
                }
            }else{
                final Dialog d = new Dialog(edit_note.this);
                d.setContentView(R.layout.layout_discard_changes);
                TextView discardTextView=(TextView)d.findViewById(R.id.discard);
                TextView saveTextView=(TextView)d.findViewById(R.id.save);
                discardTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("status", "discarded");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                saveTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNote();
                    }
                });
                d.show();
            }
        }else{
            super.onBackPressed();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){

            case 100: if(resultCode==RESULT_OK && data!=null){
                noteText_data=(EditText)findViewById(R.id.body);
                String text=noteText_data.getText().toString();
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(text.length()<1){
                    text+=""+result.get(0);
                }
                else
                    text+="\n"+result.get(0);
                noteText_data.setText(text);
            }
                break;
        }
    }
    public String parseDate(String date){
        String time=date.split(" ")[1];
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        String month_now = month_date.format(cal.getTime())+",";
        String year_now= String.valueOf(cal.get(Calendar.YEAR));

        return day_now+""+day_now_ordinal+" "+month_now+" "+year_now+" @"+time;
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
