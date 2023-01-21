package com.autodialer.autodialer.cc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.autodialer.autodialer.cc.api.ApiInterface;
import com.autodialer.autodialer.cc.api.Constants;
import com.autodialer.autodialer.cc.models.Recording;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tools {
    public boolean isValid(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }


    public void getScheduleTime(Context context) {
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
                                                scheduleNotification(context,getDelay(recordingList.get(i).getDateTime()),"Call Schedule","At "+recordingList.get(i).getDateTime());
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

    public void scheduleNotification(Context context, long delay, String title, String content) {

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("title",title);
        notificationIntent.putExtra("content",content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static boolean isValidDate(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("dd MMM yyyy hh:mm a").parse(pDateString);
        return new Date().before(date);
    }

    public long getDelay (String datetime) {
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