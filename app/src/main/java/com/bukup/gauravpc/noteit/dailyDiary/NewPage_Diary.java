package com.bukup.gauravpc.noteit.dailyDiary;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.DiaryModel;
import com.bukup.gauravpc.noteit.R;
import com.bukup.gauravpc.noteit.notesModel;
import com.bukup.gauravpc.noteit.viewnotes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewPage_Diary extends AppCompatActivity {

    EditText new_note;
    EditText title,data;
    TextView day,day_ordinal,month,year;
    ImageView saveImg;
    int savedOnce=0;
    String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_page__diary);

        final TextView saveAtTextView=(TextView)findViewById(R.id.savedAtText);
        saveAtTextView.setVisibility(View.GONE);

        day=(TextView)findViewById(R.id.day);
        day_ordinal=(TextView)findViewById(R.id.ordinal);
        month=(TextView)findViewById(R.id.month);
        year=(TextView)findViewById(R.id.year);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day_now= String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String day_now_ordinal= ordinal(cal.get(Calendar.DAY_OF_MONTH));
        String month_now= month_date.format(cal.getTime())+", ";
        String year_now= String.valueOf(cal.get(Calendar.YEAR));

        day.setText(day_now);
        day_ordinal.setText(day_now_ordinal);
        month.setText(month_now);
        year.setText(year_now);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savedOnce==0){
                    savedOnce=1;
                    final DatabaseHandler db = new DatabaseHandler(NewPage_Diary.this);
                    String noteDate, noteTitle, noteData;

                    DateFormat df = new SimpleDateFormat("dd/MM/yy");
                    Date date = new Date();
                    noteDate=df.format(date);
                    Log.e("date", noteDate);

                    title=(EditText)findViewById(R.id.title);
                    if(title.getText().toString().length()<1){
                        title.setText("No Title");
                    }
                    noteTitle=makeFirstUpper(title.getText().toString());

                    data=(EditText)findViewById(R.id.body);
                    noteData = data.getText().toString();

                    id=db.addPage_diary(new DiaryModel(noteDate, noteTitle, noteData));
                    Log.d("id",id);

                    db.close();
                    saveAtTextView.setVisibility(View.VISIBLE);
                    saveAtTextView.setText("Diary page saved");
                }
                else if(savedOnce==1){
                    final DatabaseHandler db = new DatabaseHandler(NewPage_Diary.this);
                    String noteDate, noteTitle, noteData;

                    DateFormat df = new SimpleDateFormat("dd/MM/yy");
                    Date date = new Date();
                    noteDate=df.format(date);
                    Log.e("date", noteDate);

                    title=(EditText)findViewById(R.id.title);
                    if(title.getText().toString().length()<1){
                        title.setText("No Title");
                    }
                    noteTitle=makeFirstUpper(title.getText().toString());

                    data=(EditText)findViewById(R.id.body);
                    noteData = data.getText().toString();

                    db.updatePage_diary(id, noteDate, noteTitle, noteData);

                    db.close();
                    saveAtTextView.setVisibility(View.VISIBLE);
                    saveAtTextView.setText("Diary page updated");
                }
            }
        });

        audioLayout=(RelativeLayout)findViewById(R.id.audio);
        audioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

                try {
                    startActivityForResult(i, 100);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Speech To Text not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(NewPage_Diary.this,ViewDiary.class);
        startActivity(intent);
        finish();
    }

    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data_) {
        super.onActivityResult(requestCode, resultCode, data_);
        switch(requestCode){

            case 100: if(resultCode==RESULT_OK && data!=null){
                data=(EditText)findViewById(R.id.body);
                String text=data.getText().toString();
                ArrayList<String> result=data_.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(text.length()<1){
                    text+=""+result.get(0);
                }
                else
                    text+="\n"+result.get(0);
                data.setText(text);
            }
                break;
        }
    }
    public static String ordinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
            default:
                return sufixes[i % 10];

        }
    }
}
