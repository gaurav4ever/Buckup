package com.bukup.gauravpc.noteit;

import android.*;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bukup.gauravpc.noteit.BucketList.BucketList_addItem;
import com.bukup.gauravpc.noteit.Notes.edit_note;
import com.bukup.gauravpc.noteit.dailyDiary.EditPage_Diary;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.bukup.gauravpc.noteit.R.id.date;

public class reminders extends AppCompatActivity {

    TextView timeTextView,locationTextView;
    String time,location_val,address_val;
    EditText desc;
    Calendar date = Calendar.getInstance();
    Calendar cal = Calendar.getInstance();
    RelativeLayout setReminderButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        timeTextView=(TextView)findViewById(R.id.timeText);

        desc=(EditText)findViewById(R.id.desc);

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //**************************** General Date and Time picker in Android* ********************************************

                final Calendar currentDate = Calendar.getInstance();
                new DatePickerDialog(reminders.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);
                        new TimePickerDialog(reminders.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                date.set(Calendar.MINUTE, minute);
                                Log.v("Date and Time", "The choosen one " + date.getTime());
                                timeTextView.setText(""+date.getTime());
                            }
                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                    }
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

                //********************************************* END *********************************************************
            }
        });

        locationTextView=(TextView)findViewById(R.id.locationText);
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(reminders.this);
                dialog.setContentView(R.layout.layout_location);
                final EditText editText=(EditText)dialog.findViewById(R.id.locationText);
//                if(location_val.equals("null")){
//                    editText.setText("Add Location Manually");
//                }else{
//                    editText.setText(location_val);
//                }

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
                        if (ActivityCompat.checkSelfPermission(reminders.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(reminders.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                                //user did not gave permission or did not gave any response
                                //Ask again for permission
                                ActivityCompat.requestPermissions(reminders.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                //Ask for permission first time
                                ActivityCompat.requestPermissions(reminders.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        } else {
                            int off = 0;
                            try {
                                off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (off == 0) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(reminders.this);
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

                                MyLocListener myLocListener = new MyLocListener(reminders.this);
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

//      Set the reminder by calling google calender
        setReminderButtonLayout=(RelativeLayout)findViewById(R.id.setReminderButton);
        setReminderButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setType("vnd.android.cursor.item/event");
                calIntent.putExtra(CalendarContract.Events.TITLE, "Buckup Reminders");
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationTextView.getText().toString());
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, desc.getText().toString());

                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        date.getTimeInMillis());
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        date.getTimeInMillis() + 60*1000);

                startActivity(calIntent);
            }
        });
    }
}
