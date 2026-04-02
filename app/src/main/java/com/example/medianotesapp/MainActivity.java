package com.example.medianotesapp;

// 1. ALL REQUIRED IMPORTS
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Recycler View (Staggered Grid)
        // Ensure you have a RecyclerView with android:id="@+id/recyclerView" in activity_main.xml
        RecyclerView rv = findViewById(R.id.recyclerView);
        if (rv != null) {
            rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        // Add this inside onCreate() in MainActivity.java
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        // 3. Sensor Initialization
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // 4. Periodic Task (Notification for Roll No 6)
        // Rule: 15 minutes is the minimum interval allowed by Android WorkManager
        PeriodicWorkRequest pwr = new PeriodicWorkRequest.Builder(NotesWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueue(pwr);

        loadNotes();
    }

    private void loadNotes() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> noteList = new ArrayList<>();

        // Fetch all notes from your specific Roll No. 6 table, newest first
        Cursor cursor = db.rawQuery("SELECT * FROM notes_6 ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int reminderFlag = cursor.getInt(cursor.getColumnIndexOrThrow("reminder_flag")); // Your Roll 6 logic

                // Add each note to our list
                noteList.add(new Note(id, title, desc, imagePath, date, reminderFlag));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // You have to pass 'MainActivity.this' into the adapter now!
        RecyclerView rv = findViewById(R.id.recyclerView);
        NotesAdapter adapter = new NotesAdapter(MainActivity.this, noteList);
        rv.setAdapter(adapter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Basic shake detection logic
        if (Math.abs(x - lastX) > SHAKE_THRESHOLD || Math.abs(y - lastY) > SHAKE_THRESHOLD) {
            Toast.makeText(this, "Device motion detected", Toast.LENGTH_SHORT).show();
            // This is where you'd call a method to refresh your list
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this assignment
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        loadNotes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}