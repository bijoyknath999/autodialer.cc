package com.autodialer.autodialer.cc;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

class NotificationHelper extends ContextWrapper {
    public static final String channelID = "ComeBackID";
    public static final String channelName = "ComebackChannel";
    private NotificationManager notificationManager;
    private final String title;
    private final String content;
    public NotificationHelper(Context base, String title, String content) {
        super(base);
        this.title = title;
        this.content = content;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
    public NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent;
        resultIntent = new Intent(this, HomeActivity.class);
        resultIntent.putExtra("notify",true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_notifications);
    }
}