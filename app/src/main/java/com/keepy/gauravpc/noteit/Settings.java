package com.keepy.gauravpc.noteit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    TextView appVersion;
    Switch newNoteAtTopSwitch,autoBackupSwitch;
    int someThingChanged=0;

    RelativeLayout settingsLayout,openSourceLibLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        String newNoteAtTop=sharedPreferences.getString("newNoteAtTop", "");

        appVersion=(TextView)findViewById(R.id.t2);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        appVersion.setText(version);

        newNoteAtTopSwitch=(Switch)findViewById(R.id.noteAtTop);
        if(newNoteAtTop.equals("true"))newNoteAtTopSwitch.setChecked(true);
        else newNoteAtTopSwitch.setChecked(false);
        newNoteAtTopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("checked","yes");
                    SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("newNoteAtTop", "true");
                    editor.commit();
                }else{
                    Log.d("checked","no");
                    SharedPreferences sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("newNoteAtTop", "false");
                    editor.commit();
                }
                someThingChanged=1;
            }
        });
        autoBackupSwitch=(Switch)findViewById(R.id.autoBackup);

        settingsLayout=(RelativeLayout)findViewById(R.id.settingsLayout);
        openSourceLibLayout=(RelativeLayout)findViewById(R.id.openSourceLibLayout);
        openSourceLibLayout.setVisibility(View.GONE);

        RelativeLayout seeosLayout=(RelativeLayout)findViewById(R.id.seeosl);
        seeosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsLayout.setVisibility(View.GONE);
                openSourceLibLayout.setVisibility(View.VISIBLE);
            }
        });

        ImageView goback1,goback2;
        goback1=(ImageView)findViewById(R.id.goback1);
        goback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        goback2=(ImageView)findViewById(R.id.goback2);
        goback2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsLayout.setVisibility(View.VISIBLE);
                openSourceLibLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(someThingChanged==0)
            super.onBackPressed();
        else{
            Intent intent=new Intent(Settings.this,viewnotes.class);
            startActivity(intent);
            finish();
        }

    }
}
