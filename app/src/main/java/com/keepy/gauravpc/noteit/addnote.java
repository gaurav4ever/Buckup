package com.keepy.gauravpc.noteit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.richeditor.RichEditor;

public class addnote extends AppCompatActivity {

    EditText new_note;
    EditText noteText_title,noteText_data,noteText_date;
    ImageView saveImg;
    int savedOnce=0;
    String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        final TextView saveAtTextView=(TextView)findViewById(R.id.savedAtText);
        saveAtTextView.setVisibility(View.GONE);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savedOnce==0){
                    savedOnce=1;
                    final DatabaseHandler db = new DatabaseHandler(addnote.this);
                    String noteDate, noteTitle, noteData;

                    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
                    Date date = new Date();
                    noteDate=df.format(date);
                    Log.e("date", noteDate);

                    noteText_title=(EditText)findViewById(R.id.title);
                    if(noteText_title.getText().toString().length()<1){
                        noteText_title.setText("No Title");
                    }
                    noteTitle=makeFirstUpper(noteText_title.getText().toString());

                    noteText_data=(EditText)findViewById(R.id.body);
                    noteData = noteText_data.getText().toString();

                    id=db.addNote(new notesModel(noteDate, noteTitle, noteData));
                    Log.d("id",id);

                    db.close();
                    saveAtTextView.setVisibility(View.VISIBLE);
                    saveAtTextView.setText("Saved on " + noteDate);
                }
                else if(savedOnce==1){
                    final DatabaseHandler db = new DatabaseHandler(addnote.this);
                    String noteDate, noteTitle, noteData;

                    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
                    Date date = new Date();
                    noteDate=df.format(date);
                    Log.e("date", noteDate);

                    noteText_title=(EditText)findViewById(R.id.title);
                    if(noteText_title.getText().toString().length()<1){
                        noteText_title.setText("No Title");
                    }
                    noteTitle=makeFirstUpper(noteText_title.getText().toString());

                    noteText_data=(EditText)findViewById(R.id.body);
                    noteData = noteText_data.getText().toString();

                    db.updateNote(id,noteDate,noteTitle,noteData);

                    db.close();
                    saveAtTextView.setVisibility(View.VISIBLE);
                    saveAtTextView.setText("Saved on " + noteDate);
                }
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
    }

    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(addnote.this,viewnotes.class);
        startActivity(intent);
        finish();
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
