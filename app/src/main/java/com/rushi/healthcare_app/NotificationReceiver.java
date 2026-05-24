package com.rushi.healthcare_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "appointment_alerts";

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d("ALARM_TEST", "Alarm fired! Receiver started.");
        android.widget.Toast.makeText(context, "Alarm Triggered!", android.widget.Toast.LENGTH_LONG).show();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Appointments",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent tapIntent = new Intent(context, DashboardActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE
        );

        String patientName = intent.getStringExtra("patient_name");
        String time = intent.getStringExtra("time");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Upcoming Appointment")
                .setContentText(patientName + " - " + time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}