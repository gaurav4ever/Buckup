package com.bukup.gauravpc.noteit.dailyDiary;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.DatabaseHandler;
import com.bukup.gauravpc.noteit.Models.DiaryModel;
import com.bukup.gauravpc.noteit.R;

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
    int flag=0;

    String title_val="",body_val="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_page__diary);

        title=(EditText)findViewById(R.id.title);
        data=(EditText)findViewById(R.id.body);

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
                savePage();
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

    public void savePage(){
        if(savedOnce==0){
            savedOnce=1;
            final DatabaseHandler db = new DatabaseHandler(NewPage_Diary.this);
            String noteDate, noteTitle, noteData;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            noteDate=df.format(date);
            Log.e("date", noteDate);

            if(title.getText().toString().length()<1){
                title.setText("No Title");
            }
            noteTitle = makeFirstUpper(title.getText().toString());

            body_val = data.getText().toString();
            if(body_val.length()<1){
                Toast.makeText(getApplicationContext(),"Cannot save empty page",Toast.LENGTH_SHORT).show();
            }else{
                id=db.addPage_diary(new DiaryModel(noteDate, noteTitle, body_val));
                db.close();

                Intent intent = new Intent();
                intent.putExtra("status", "added");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else if(savedOnce==1){
            final DatabaseHandler db = new DatabaseHandler(NewPage_Diary.this);
            String noteDate, noteTitle, noteData;

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            noteDate=df.format(date);
            Log.e("date", noteDate);

            title_val=title.getText().toString();
            if(title_val.length()<1){
                title.setText("No Title");
            }
            noteTitle = makeFirstUpper(title_val);

            body_val = data.getText().toString();
            if(body_val.length()<1){
                Toast.makeText(getApplicationContext(),"Cannot save empty page",Toast.LENGTH_SHORT).show();
            }else{
                db.updatePage_diary(id, noteDate, noteTitle, body_val);
                db.close();
                Intent intent = new Intent();
                intent.putExtra("status", "added");
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        body_val=data.getText().toString();
        if(body_val.length()<1 || flag==1){
            super.onBackPressed();
        }else{
            //alert user to save changes or not
            final Dialog d = new Dialog(NewPage_Diary.this);
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
                    savePage();
                }
            });
            d.show();
        }
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
