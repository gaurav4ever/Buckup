package com.bukup.gauravpc.noteit;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

public class edit_note extends AppCompatActivity {

    String id_val;
    EditText new_note;
    EditText noteText_title,noteText_data,noteText_date;
    ImageView saveImg;


    String title_val,body_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent=getIntent();

        id_val=intent.getStringExtra("id");
        title_val=intent.getStringExtra("title");
        body_val=intent.getStringExtra("body");

        noteText_title=(EditText)findViewById(R.id.title);noteText_title.setText(title_val);
        noteText_data=(EditText)findViewById(R.id.body);noteText_data.setText(body_val);

        final TextView updatedAtTextView=(TextView)findViewById(R.id.updateAtText);
        updatedAtTextView.setVisibility(View.GONE);

        RelativeLayout saveLayout,audioLayout,imageLayout,backupLayout,infoLayout,deleteNoteLayout;
        saveLayout=(RelativeLayout)findViewById(R.id.save);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                noteText_title = (EditText) findViewById(R.id.title);
                if (noteText_title.getText().toString().length() < 1) {
                    noteText_title.setText("No Title");
                }
                noteText_data = (EditText) findViewById(R.id.body);

                String noteDate;
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
                Date date = new Date();
                noteDate = df.format(date);

                final DatabaseHandler db = new DatabaseHandler(edit_note.this);
                db.updateNote(id_val, noteDate, makeFirstUpper(noteText_title.getText().toString()), noteText_data.getText().toString());

                updatedAtTextView.setVisibility(View.VISIBLE);
                updatedAtTextView.setText("Updated on " + noteDate);

                db.close();
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

        deleteNoteLayout=(RelativeLayout)findViewById(R.id.delete);
        deleteNoteLayout.setOnClickListener(new View.OnClickListener() {
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
                        db.deletNote(Integer.parseInt(id_val));
                        db.close();
                        Intent intent1 = new Intent(edit_note.this, viewnotes.class);
                        startActivity(intent1);
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
    }

    public String makeFirstUpper(String val){
        String final_answer="";
        final_answer+=String.valueOf(val.charAt(0)).toUpperCase();
        final_answer+=val.substring(1);
        return final_answer;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(edit_note.this,viewnotes.class);
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
