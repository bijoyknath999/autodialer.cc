package com.example.autodialer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.fragments.DialerFragment;
import com.example.autodialer.fragments.ScheduleFragment;
import com.example.autodialer.fragments.SettingsFragment;
import com.example.autodialer.fragments.LeadsFragment;
import com.example.autodialer.models.Recording;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottom_nv;
    DialerFragment df;
    LeadsFragment tf;
    SettingsFragment sf;
    ScheduleFragment Sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        df=new DialerFragment();
        tf=new LeadsFragment();
        sf=new SettingsFragment();
        Sf=new ScheduleFragment();
        bottom_nv=findViewById(R.id.bottom_nv);
        bottom_nv.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,df).commit();

        SharedPreferences pref = getSharedPreferences("MyPref", Activity.MODE_PRIVATE);
        Constants.adminID = pref.getString("admin_id","");
        Constants.id = pref.getString("id","");
        getScheduleTime();
        Constants.home_context=getApplicationContext();
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

        }
        return false;
    }

    private void getScheduleTime() {
        ApiInterface.getApiRequestInterface().getScheduleAll(Constants.id,Constants.adminID)
                .enqueue(new Callback<List<Recording>>() {
                    @Override
                    public void onResponse(Call<List<Recording>> call, Response<List<Recording>> response) {
                        if (response.isSuccessful())
                        {
                            List<Recording> recordingList = response.body();
                            if (recordingList.size()>0)
                            {
                                for (int i=0; i<recordingList.size(); i++)
                                {
                                    Tools tools = new Tools();
                                    if (tools.isValid(recordingList.get(i).getDateTime()))
                                    {
                                        try {
                                            if (isValidDate(recordingList.get(i).getDateTime()))
                                            {
                                                scheduleNotification(getDelay(recordingList.get(i).getDateTime()),"Call Schedule","At "+recordingList.get(i).getDateTime());
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Recording>> call, Throwable t) {

                    }
                });
    }

    private void scheduleNotification(long delay, String title, String content) {

        Intent notificationIntent = new Intent(HomeActivity.this, NotificationReceiver.class);
        notificationIntent.putExtra("title",title);
        notificationIntent.putExtra("content",content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static boolean isValidDate(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("dd MMM yyyy hh:mm a").parse(pDateString);
        return new Date().before(date);
    }

    private long getDelay (String datetime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");

        Date date = new Date();
        String time2 = simpleDateFormat.format(date);

        Date date1 = null, date2 = null;
        try {
            date1 = simpleDateFormat.parse(datetime);
            date2 = simpleDateFormat.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = date1.getTime() - date2.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toMillis(difference);
        return diffInSec;
    }

}