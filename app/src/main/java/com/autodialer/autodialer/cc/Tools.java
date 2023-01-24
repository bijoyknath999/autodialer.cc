package com.autodialer.autodialer.cc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.CallLog;
import android.widget.Toast;

import com.autodialer.autodialer.cc.api.ApiInterface;
import com.autodialer.autodialer.cc.api.Constants;
import com.autodialer.autodialer.cc.models.Recording;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tools {
    public static Timer timer;
    public static TimerTask timerTask;
    public static Double time = 0.0;

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

    /*public static String getCallLogs(Context context, String usernumber) {
        String exactDuration = "00:00:00";
        ContentResolver cr = context.getContentResolver();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

        Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI,
                null, null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);

            if (phNumber.contains(usernumber))
            {
                if(dircode==CallLog.Calls.OUTGOING_TYPE)
                {
                    Date date1 = new Date();
                    long difference_In_Time
                            = date1.getTime() - callDayTime.getTime();

                    // Calculate time difference in
                    // seconds, minutes, hours, years,
                    // and days
                    long difference_In_Seconds
                            = (difference_In_Time
                            / 1000)
                            % 60;

                    long difference_In_Minutes
                            = (difference_In_Time
                            / (1000 * 60))
                            % 60;

                    long difference_In_Hours
                            = (difference_In_Time
                            / (1000 * 60 * 60))
                            % 24;

                    long difference_In_Years
                            = (difference_In_Time
                            / (1000l * 60 * 60 * 24 * 365));

                    long difference_In_Days
                            = (difference_In_Time
                            / (1000 * 60 * 60 * 24))
                            % 365;

                    if (difference_In_Years==0 && difference_In_Days==0 && difference_In_Hours==0 && difference_In_Minutes<=5)
                    {
                        int dura = Integer.parseInt(callDuration);
                        int hours = (int) dura / 3600;
                        int remainder = (int) dura - hours * 3600;
                        int mins = remainder / 60;
                        remainder = remainder - mins * 60;
                        int secs = remainder;
                        exactDuration = twoDigitString(hours) + ":" + twoDigitString(mins) + ":" + twoDigitString(secs);
                        return exactDuration;
                    }
                }
            }
        }
        managedCursor.close();
        return exactDuration;
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }*/

    public static void startTimer(Context context)
    {
        time = 0.0;
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        timer = new Timer();

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                time++;
                editor.putString("callduration",getTimerText());
                editor.apply();
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    public static void stoptimer(){
        if (timerTask!=null)
            timerTask.cancel();
    }


    public static String getTimerText()
    {
        int rounded;
        if (time<10.0)
            rounded = (int) Math.round(time-5.0);
        else
            rounded = (int) Math.round(time-10.0);


        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    public static String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + ":" + String.format("%02d",minutes) + ":" + String.format("%02d",seconds);
    }
}
