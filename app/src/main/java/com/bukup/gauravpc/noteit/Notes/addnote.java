package com.bukup.gauravpc.noteit.Notes;

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
import com.bukup.gauravpc.noteit.MainActivity;
import com.bukup.gauravpc.noteit.Models.notesModel;
import com.bukup.gauravpc.noteit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class addnote extends AppCompatActivity {

    String title="",body="";

    EditText new_note;
    EditText noteText_title,noteText_data,noteText_date;
    ImageView saveImg;
    int savedOnce=0;
    String id = null;
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
    }

    //function to save Note
    public void saveNote(){
        if(savedOnce==0){
            savedOnce=1;
            final DatabaseHandler db = new DatabaseHandler(addnote.this);
            String noteDate, noteTitle, noteData;

            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
            Date date = new Date();
            noteDate=df.format(date);
            Log.e("date", noteDate);


            title=noteText_title.getText().toString();
            if(title.length()<1){
                noteText_title.setText("No Title");
            }
            noteTitle=makeFirstUpper(noteText_title.getText().toString());

            body=noteData = noteText_data.getText().toString();
            if(body.length()<1){
                Toast.makeText(getApplicationContext(),"Can not save an empty note",Toast.LENGTH_SHORT).show();
            }else{
                id=db.addNote(new notesModel(noteDate, noteTitle, noteData));
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
            if(noteData.length()<1){
                Toast.makeText(getApplicationContext(),"Can not save an empty note",Toast.LENGTH_SHORT).show();
            }else{
                db.updateNote(id,noteDate,noteTitle,noteData);
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
