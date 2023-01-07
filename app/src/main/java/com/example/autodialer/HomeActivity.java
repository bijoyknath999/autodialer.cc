package com.example.autodialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.example.autodialer.api.Constants;
import com.example.autodialer.fragments.DialerFragment;
import com.example.autodialer.fragments.ScheduleFragment;
import com.example.autodialer.fragments.SettingsFragment;
import com.example.autodialer.fragments.TasksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottom_nv;
    DialerFragment df;
    TasksFragment tf;
    SettingsFragment sf;
    ScheduleFragment Sf;
    final Handler handler = new Handler();
    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        df=new DialerFragment();
        tf=new TasksFragment();
        sf=new SettingsFragment();
        Sf=new ScheduleFragment();
        bottom_nv=findViewById(R.id.bottom_nv);
        bottom_nv.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.containFrag,df).commit();

        NotificationRequest nr = new NotificationRequest();
        nr.execute();

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
    public void show(String time,Context c){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c, "notify_001");


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("At "+time);
        bigText.setBigContentTitle("Call Schedule");
        //bigText.setSummaryText("Text in detail");


        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        //mBuilder.setContentTitle("Client Name: Ali");
       // mBuilder.setContentText("+91345455665");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
    public class NotificationRequest extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://192.168.0.11:8000/api/schedule/"+Constants.adminID)
                    .get()
                    .build();


            try {
                Response response = client.newCall(request).execute();
                System.out.println(response);

                JSONArray jsonArray = new JSONArray(response.body().string());
                System.out.println(jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    if(Constants.adminID.equals(jObject.getString("admin_id"))&& jObject.getString("scheduled").equals("Yes")){
                        //id = jObject.getString("id");
                       // name = jObject.getString("client_name");
                        //city = jObject.getString("client_city");
                       // phone = jObject.getString("client_pno");
                        Constants.Schedule_notification.add(jObject.getString("date_time"));
                        //tasks = new Tasks(id,name,country,city,phone);
                        //TaskList.add(tasks);
                       // System.out.println("Heloo"+tasks.toString());
                    }
                }

                System.out.println("Notification"+Constants.Schedule_notification.toString());
                for (int i = 0; i < Constants.Schedule_notification.size(); i++) {
                    String date = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(new Date());
                    System.out.println("Now Date:" + date);

                    if (Constants.Schedule_notification.get(i).equals(date)) {
                        show(Constants.Schedule_notification.get(i),getApplicationContext());
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            }

    }

}