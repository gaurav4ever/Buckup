package com.keepy.gauravpc.noteit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

                noteText_title=(EditText)findViewById(R.id.title);
                if(noteText_title.getText().toString().length()<1){
                    noteText_title.setText("No Title");
                }
                noteText_data=(EditText)findViewById(R.id.body);

                String noteDate;
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss a");
                Date date = new Date();
                noteDate=df.format(date);

                final DatabaseHandler db = new DatabaseHandler(edit_note.this);
                db.updateNote(id_val,noteDate,makeFirstUpper(noteText_title.getText().toString()),noteText_data.getText().toString());

                updatedAtTextView.setVisibility(View.VISIBLE);
                updatedAtTextView.setText("Updated on " + noteDate);

                db.close();
            }
        });

        deleteNoteLayout=(RelativeLayout)findViewById(R.id.delete);
        deleteNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseHandler db = new DatabaseHandler(edit_note.this);
                db.deletNote(Integer.parseInt(id_val));
                db.close();
                Intent intent1=new Intent(edit_note.this,viewnotes.class);
                startActivity(intent1);
                finish();
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

}
