package com.bukup.gauravpc.noteit.Notes;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.MainActivity;
import com.bukup.gauravpc.noteit.Models.notesModel;
import com.bukup.gauravpc.noteit.MyLocListener;
import com.bukup.gauravpc.noteit.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class addnote extends AppCompatActivity {

    String title="",body="";
    TextView locationTextView;
    EditText new_note;
    EditText noteText_title,noteText_data,noteText_date;
    RelativeLayout locationLayout;
    ImageView saveImg,crossImageView;
    int savedOnce=0;
    String id = null,address_val="null";
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        noteText_title=(EditText)findViewById(R.id.title);
        noteText_data=(EditText)findViewById(R.id.body);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        audioLayout=(RelativeLayout)findViewById(R.id.audio);
        audioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

                try {
                    startActivityForResult(i, 100);
                }catch(ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(), "Speech To Text not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationTextView=(TextView)findViewById(R.id.location);
        locationLayout=(RelativeLayout)findViewById(R.id.locationLayout);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(addnote.this);
                dialog.setContentView(R.layout.layout_location);
                final EditText editText=(EditText)dialog.findViewById(R.id.locationText);

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
                        if (ActivityCompat.checkSelfPermission(addnote.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(addnote.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                //user did not gave permission or did not gave any response
                                //Ask again for permission
                                ActivityCompat.requestPermissions(addnote.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                //Ask for permission first time
                                ActivityCompat.requestPermissions(addnote.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        } else {
                            int off = 0;
                            try {
                                off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (off == 0) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(addnote.this);
                                alertDialogBuilder.setMessage("We are unable to find any Dytila Kitchen near you because GPS is OFF... would you like to enable it ?");
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

                                MyLocListener myLocListener = new MyLocListener(addnote.this);
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
        crossImageView=(ImageView)findViewById(R.id.cross);
        crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //function to save Note
    public void saveNote(){
        if(savedOnce==0){
            savedOnce=1;
            final DatabaseHandler db = new DatabaseHandler(addnote.this);
            String noteCreatedOn,noteUpdatedOn, noteTitle, noteData;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            noteUpdatedOn=noteCreatedOn=df.format(date);

            title=noteText_title.getText().toString();
            if(title.length()<1){
                noteText_title.setText("No Title");
            }
            noteTitle=makeFirstUpper(noteText_title.getText().toString());

            body=noteData = noteText_data.getText().toString();
            if(body.length()<1){
                Toast.makeText(getApplicationContext(),"Can not save an empty note",Toast.LENGTH_SHORT).show();
            }else{
                id=db.addNote(new notesModel(noteCreatedOn,noteUpdatedOn,noteTitle, noteData,"0",address_val));
                db.close();


                Intent intent = new Intent();
                intent.putExtra("status", "added");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else if(savedOnce==1){
            final DatabaseHandler db = new DatabaseHandler(addnote.this);
            String noteDate, noteTitle, noteData;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            noteDate=df.format(date);

            noteText_title=(EditText)findViewById(R.id.title);
            if(noteText_title.getText().toString().length()<1){
                noteText_title.setText("No Title");
            }
            noteTitle=makeFirstUpper(noteText_title.getText().toString());

            noteText_data=(EditText)findViewById(R.id.body);
            noteData = noteText_data.getText().toString();
            if(noteData.length()<1){
                Toast.makeText(getApplicationContext(),"Can not save an empty note",Toast.LENGTH_SHORT).show();
            }else{
                db.updateNote(id,noteDate,noteTitle,noteData,address_val);
                db.close();
                Intent intent = new Intent();
                intent.putExtra("status", "added");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {
//        Intent intent=new Intent(addnote.this,MainActivity.class);
//        startActivity(intent);
//        finish();
        body=noteText_data.getText().toString();
        if(body.length()<1 || flag==1){
            super.onBackPressed();
        }else{
            //alert user to save changes or not
            final Dialog d = new Dialog(addnote.this);
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
                    saveNote();
                }
            });
            d.show();
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
}
