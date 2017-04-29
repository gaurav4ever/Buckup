package com.keepy.gauravpc.noteit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button newNoteButton,viewNoteButton;
    ImageView img1,img2;
    TextView creditsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String noteStyle=sharedPreferences.getString("showIn", "");
        editor.putString("newNoteAtTop","true");
        if(noteStyle.equals("listView")){
            editor.putString("showIn","listView");
        }else{
            editor.putString("showIn","gridView");
        }

        editor.apply();

        Intent intent=new Intent(MainActivity.this,viewnotes.class);
        startActivity(intent);
        finish();

    }
}
