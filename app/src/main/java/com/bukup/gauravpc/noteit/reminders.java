package com.bukup.gauravpc.noteit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bukup.gauravpc.noteit.BucketList.BucketList_addItem;
import com.bukup.gauravpc.noteit.dailyDiary.EditPage_Diary;

import java.util.Calendar;

import static com.bukup.gauravpc.noteit.R.id.date;

public class reminders extends AppCompatActivity {

    TextView timeTextView;
    String time;
    EditText title,desc;
    Calendar date = Calendar.getInstance();
    Calendar cal = Calendar.getInstance();
    RelativeLayout setReminderButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        timeTextView=(TextView)findViewById(R.id.timeText);

        title=(EditText)findViewById(R.id.title);
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

//      Set the reminder by calling google calender
        setReminderButtonLayout=(RelativeLayout)findViewById(R.id.setReminderButton);
        setReminderButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setType("vnd.android.cursor.item/event");
                calIntent.putExtra(CalendarContract.Events.TITLE, title.getText().toString());
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Beach House");
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
