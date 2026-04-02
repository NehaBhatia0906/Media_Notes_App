package com.example.medianotesapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

// Use 'Worker' from androidx.work, not CoroutineScheduler
public class NotesWorker extends Worker {

    public NotesWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String chId = "6_notes";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chId, "Reminders", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), chId)
                .setSmallIcon(android.R.drawable.stat_notify_chat) // Replace with your own icon if needed
                .setContentTitle("Media Notes")
                .setContentText("Review your saved notes today") // Rule for Roll No. 6
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(6, builder.build());

        return Result.success();
    }
}