package com.bukup.gauravpc.noteit;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;

public class reminders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

//        Intent calIntent = new Intent(Intent.ACTION_INSERT);
//        Calendar cal = Calendar.getInstance();
//        calIntent.setType("vnd.android.cursor.item/event");
//        calIntent.putExtra(CalendarContract.Events.TITLE, "My House Party");
//        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Beach House");
//        calIntent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
//        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "A Pig Roast on the Beach");
//        startActivity(calIntent);
    }
}
