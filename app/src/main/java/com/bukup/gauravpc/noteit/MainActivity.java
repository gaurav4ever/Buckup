package com.bukup.gauravpc.noteit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bukup.gauravpc.noteit.Authentication.Login;
import com.bukup.gauravpc.noteit.Notes.Fragment2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    Button newNoteButton,viewNoteButton;
    ImageView img1,img2;
    TextView creditsText;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SharedPreferences sharedPreferences;

    RelativeLayout pinLayout,mainLayout;
    EditText pinEditText;
    TextView proceedTextView;
    String isLogin,isPinSet;
    private android.os.Handler handler;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = this.getCurrentFocus();

        sharedPreferences=getSharedPreferences("details", Context.MODE_APPEND);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        isLogin=sharedPreferences.getString("isLogin", "");
        isPinSet=sharedPreferences.getString("pin", "");
        if(isLogin.isEmpty()){
            editor.putString("isLogin", "0");
            isLogin="0";
        }
        if(isPinSet.isEmpty()){
            editor.putString("pin","noPin");
            isPinSet="noPin";
        }
        Log.e("pin",isPinSet);
        editor.apply();

        if(isLogin.equals("0")){
            Intent intent=new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }

        pinLayout=(RelativeLayout)findViewById(R.id.pinLayout);
        mainLayout=(RelativeLayout)findViewById(R.id.mainLayout);

        //pin layout operations
        if(isPinSet.equals("noPin")){
            pinLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }else{
            pinLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        }
        pinEditText=(EditText)findViewById(R.id.pin);
        proceedTextView=(TextView)findViewById(R.id.check);
        proceedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPinSet.equals(pinEditText.getText().toString()) || isPinSet.equals("buckuppin2708")){
                    pinLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Incorrect Pin",Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPager=(ViewPager)findViewById(R.id.viewpager);
        addFragments(viewPager);

        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.setOnTabSelectedListener(listener(viewPager));
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();


        DatabaseHandler db=new DatabaseHandler(this);
//        db.extra();
//        db.createTables();
//        db.alterTables();
//        db.createToDoTable();

        int flag=db.checkIfCatTableExist();
        if(flag==0){
            String category[]={"Travel","Food & Drinks","Meeting","Adventure","Learning","Education","Family","Finance","Physical","Purchase","Event","Relationship"};

            String cat_detail[]={"This includes the places you want to go and the things you want to see across this crazy world.",
                    "This includes the different types of food you want to eat.\nTry a new dish or a drink or an ice cream from different area. Everything related to food comes into this category.",
                    "Want to meet an celebrity? or a friend after a very long time?\nMeet your ideal person. Every lucky meet to your favourite person fall into this category.\n\nPS: This Category does not include business meetings and all. :P",
                    "These are the things on your Bucket List that scare you a little…Bungee Jumping, Skydiving, Climbing to the top of Mount Everest or any crazy task you want to do in your life.",
                    "Always wanted learn a new language?\nHow about how to play the piano? or how to play guitar?\nThis category includes all those things which you want to learn apart from your professional carrier i.e. as a Hobby.",
                    "This category includes your actual educational goals like going back to college to get that degree or doing a course in your later life.",
                    "There are certain things you can only do with family like:\n\n1. Give my daughter away to a man who deserves her.\n2. Coach my son’s little league team.\n3. Go to a long trip with Family.\n\n All family related goals comes in this category.",
                    "Everyone has financial goals like to have a million dollar worth, to open a successful company, get into a real estate business etc. Add your financial goal to your list by choosing this category.",
                    "These items include physical challenges like running a marathon or hiking the Appalachian Trail, but could also include specific items like getting six-pack abs or hitting that goal weight. You could also include things like going on a Vegan diet for a month or going a year without chocolate.",
                    "Want to buy a sports car? or a yacht? or any costly item you dream of ?\n All these things fall into this category.",
                    "This category includes the bucket list items like:\n\n1. Go to the Tomorrow Land festival\n2. Attend live performance of a famous singer\n3. Watch a final game of NBA etc...",
                    "Want to make the girl from your high school your girlfriend? Marry a nice and gentle man? Find a perfect partner? These things come into this category."};

            int category_img[]={R.drawable.bl1,R.drawable.bl2,R.drawable.bl3,R.drawable.bl4,R.drawable.bl5,R.drawable.bl6,R.drawable.bl7,R.drawable.bl8,R.drawable.bl9,R.drawable.bl10,R.drawable.bl11,R.drawable.bl12};
            db.insertCat(category,cat_detail,category_img);
        }
        db.close();
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ham);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
    private void addFragments(ViewPager viewPager){
        PageAdapter adapter=new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment1(),"Options");
        adapter.addFragment(new Fragment2(), "My Notes");
        viewPager.setAdapter(adapter);

    }
    private TabLayout.OnTabSelectedListener listener(final ViewPager pager){
        return new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0){
                    handler=new Handler();
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            TextView countNotes,countDiary,countBL,countToDo;
                            DatabaseHandler db=new DatabaseHandler(MainActivity.this);
                            int count1=db.getCount();
                            int count2=db.getCountOfDiary();
                            int count3=db.getCountOfBucketList();
                            int count4=db.getCountOfToDoList();
                            countNotes=(TextView)findViewById(R.id.count_notes);countNotes.setText(""+count1);
                            countDiary=(TextView)findViewById(R.id.count_diary);countDiary.setText(""+count2);
                            countBL=(TextView)findViewById(R.id.count_bucket_list);countBL.setText(""+count3);
                            countToDo=(TextView)findViewById(R.id.count_todo);countToDo.setText(""+count4);
                        }
                    };
                    handler.post(runnable);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }
    public class PageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList=new ArrayList<>();
        private final List<String> fragmentTitleList=new ArrayList<>();


        public PageAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        public void addFragment(Fragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0)
                return null;
            return fragmentTitleList.get(position);
        }
    }
}
