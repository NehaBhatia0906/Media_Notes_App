package com.example.medianotesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {
    EditText etTitle, etDesc;
    Button btnSave;
    ImageButton btnCamera, btnGallery;
    DatabaseHelper dbHelper;

    // We will store the path of the chosen image here until the user clicks "Save"
    String savedImagePath = "";

    // Codes to identify which intent is returning data
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        dbHelper = new DatabaseHelper(this);
        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        btnSave = findViewById(R.id.btnSave);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        // 1. Hook up the Camera Button
        btnCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        });

        // 2. Hook up the Gallery Button
        btnGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_GALLERY);
        });

        // 3. Hook up the Save Button
        btnSave.setOnClickListener(v -> saveNote());
    }

    // This method catches the image coming back from the Camera or Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY) {
                // Get the URI of the chosen image
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    savedImagePath = selectedImage.toString();
                    Toast.makeText(this, "Gallery Image Attached!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                // Get the thumbnail bitmap from the camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // For this assignment, we will simulate saving the path to keep it simple
                // (Saving full-res camera photos in Android 13+ requires complex FileProviders)
                savedImagePath = "CAMERA_THUMBNAIL_ATTACHED";
                Toast.makeText(this, "Camera Photo Attached!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", desc);
        values.put("date", date);

        // This is the crucial part! We insert the path we got from the Intents
        values.put("image_path", savedImagePath);

        // Your Custom Roll No. 6 Logic
        values.put("reminder_flag", 1);

        long id = db.insert("notes_6", null, values);

        if (id != -1) {
            Toast.makeText(this, "Note saved to notes_6!", Toast.LENGTH_SHORT).show();
            finish(); // Closes the screen and takes you back to MainActivity
        } else {
            Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
        }
    }
}