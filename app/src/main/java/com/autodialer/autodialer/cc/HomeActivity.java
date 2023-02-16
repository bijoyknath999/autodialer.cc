package com.autodialer.autodialer.cc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.autodialer.autodialer.cc.fragments.NoAnswerFragment;
import com.autodialer.autodialer.cc.fragments.SettingsFragment;
import com.autodialer.autodialer.cc.api.Constants;
import com.autodialer.autodialer.cc.fragments.DialerFragment;
import com.autodialer.autodialer.cc.fragments.ScheduleFragment;
import com.autodialer.autodialer.cc.fragments.LeadsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottom_nv;
    DialerFragment df;
    LeadsFragment tf;
    SettingsFragment sf;
    ScheduleFragment Sf;
    NoAnswerFragment nf;
    Tools tools = new Tools();

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        df=new DialerFragment();
        tf=new LeadsFragment();
        sf=new SettingsFragment();
        Sf=new ScheduleFragment();
        nf=new NoAnswerFragment();
        bottom_nv=findViewById(R.id.bottom_nv);
        bottom_nv.setOnNavigationItemSelectedListener(this);

        SharedPreferences pref = getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
        Constants.adminID = pref.getString("admin_id","");
        id = pref.getString("id","");
        Constants.id = pref.getString("id","");
        Constants.home_context=getApplicationContext();



        if (!id.isEmpty() && id!=null)
        {
            boolean notify = getIntent().getBooleanExtra("notify",false);
            if (notify)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,Sf).commit();
                bottom_nv.setSelectedItemId(R.id.ScheduleF);
            }
            else
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,df).commit();
                bottom_nv.setSelectedItemId(R.id.Dialer);
            }
        }

        tools.getScheduleTime(HomeActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Dialer:
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,df).commit();
                return true;
            case R.id.Tasks:
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,tf).commit();
                return true;
            case R.id.Setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,sf).commit();
                return true;
            case R.id.ScheduleF:
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,Sf).commit();
                return true;
            case R.id.NoAnswer:
                getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,nf).commit();
                return true;

        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        tools.getScheduleTime(HomeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tools.getScheduleTime(HomeActivity.this);
    }
}