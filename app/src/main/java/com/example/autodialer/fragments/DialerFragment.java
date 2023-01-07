package com.example.autodialer.fragments;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.autodialer.HomeActivity;
import com.example.autodialer.R;
import com.example.autodialer.Tasks;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.models.Completed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class DialerFragment extends Fragment {
    View view;
    Button done, pending;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialer, container, false);
        pending = (Button) view.findViewById(R.id.PendingBtn);
        if(Constants.pending<0){
            Constants.pending=0;
            pending.setText("Pending\n" + Constants.pending);
        }
        pending.setText("Pending\n" + Constants.pending);
        done = (Button) view.findViewById(R.id.DoneBtn);
        sp = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = sp.edit();
        Constants.id = sp.getString("id", "");
        Constants.done = sp.getString("done", "");
        done.setText("Done\n" + Constants.done);
        getData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void show(String time, Context c, String name, String phone) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c, "notify_001");


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
       // bigText.bigText("Client Name: "+name);
        bigText.setBigContentTitle("Call Schedule");
        bigText.setSummaryText("Schedule Notification");


        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Client Name: " + name);
        mBuilder.setContentText("At " + time+"\nClient Name: " + name+"\nPhone No: " + phone);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        if (c != null) {
            NotificationManager mNotificationManager = (NotificationManager) Constants.activity.getSystemService(c.NOTIFICATION_SERVICE);

// === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    public class NotificationRequest extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            OkHttpClient client = new OkHttpClient();
            try {
                Thread.sleep(1000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Request request = new Request.Builder()
                    .url("http://192.168.0.11:8000/api/schedule/" + Constants.adminID)
                    .get()
                    .build();


            try {
                Response response = client.newCall(request).execute();
                System.out.println(response);

                JSONArray jsonArray = new JSONArray(response.body().string());
                System.out.println(jsonArray);
                Constants.Schedule_notification.clear();
                Constants.NotificationList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    if (Constants.adminID.equals(jObject.getString("admin_id")) && jObject.getString("scheduled").equals("Yes")) {
                        //id = jObject.getString("id");
                        String name = jObject.getString("client_name");
                        String city = jObject.getString("client_city");
                        String phone = jObject.getString("client_pno");
                        Constants.Schedule_notification.add(jObject.getString("date_time"));
                        Tasks tasks = new Tasks("", name, "", city, phone);
                        tasks.setDate_time(jObject.getString("date_time"));
                        Constants.NotificationList.add(tasks);
                        //TaskList.add(tasks);
                        // System.out.println("Heloo"+tasks.toString());
                    }
                }

                System.out.println("Notification" + Constants.NotificationList.toString());
                for (int i = 0; i < Constants.NotificationList.size(); i++) {
                    String date = new SimpleDateFormat("MM-dd-yy HH:mm:ss", Locale.getDefault()).format(new Date());
                    System.out.println("Now Date:" + date);

                    if (Constants.NotificationList.get(i).getDate_time().equals(date)) {
                        System.out.println("Notification displayyyy");
                        show(Constants.NotificationList.get(i).getDate_time(), Constants.home_context, Constants.NotificationList.get(i).getName(), Constants.NotificationList.get(i).getPhone());
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
            NotificationRequest nr1 = new NotificationRequest();
            nr1.execute();

            view.postDelayed(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < Constants.NotificationList.size(); i++) {
                        String date = new SimpleDateFormat("MM-dd-yy HH:mm:ss", Locale.getDefault()).format(new Date());
                        System.out.println("Now Date:" + date);

                        if (Constants.NotificationList.get(i).getDate_time().equals(date)) {
                            System.out.println("Notification displayyyy");
                            show(Constants.NotificationList.get(i).getDate_time(),Constants.home_context, Constants.NotificationList.get(i).getName(), Constants.NotificationList.get(i).getPhone());
                        }
                    }
                }
            }, 10 * 1000);
        }

        }

    private void getData(){
        ApiInterface.getApiRequestInterface().getCompleted()
                .enqueue(new Callback<List<Completed>>() {
                    @Override
                    public void onResponse(Call<List<Completed>> call, retrofit2.Response<List<Completed>> response) {
                        if (response.isSuccessful())
                        {
                            List<Completed> completedList = response.body();
                            for (Completed completed : completedList)
                            {
                                if (completed.getUserId().equals(Constants.id)) {
                                    editor.putString("done", completed.getTotal().toString());
                                    editor.commit();
                                    Constants.done = completed.getTotal().toString();
                                    done.setText("Done\n" + completed.getTotal());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Completed>> call, Throwable t) {
                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
