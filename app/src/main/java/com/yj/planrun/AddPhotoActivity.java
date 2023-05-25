package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPhotoActivity extends AppCompatActivity {
    private final int PICK_IMAGE_FROM_ALBUM = 0;
    private FirebaseStorage storage;
    private Uri photoUri;
    private ImageView addphoto_image;
    private Button addphoto_btn_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        addphoto_image = findViewById(R.id.addphoto_image);
        addphoto_btn_upload = findViewById(R.id.addphoto_btn_upload);

        // Initialize storage
        storage = FirebaseStorage.getInstance();

        // Open the album
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);

        // Add image upload event
        addphoto_btn_upload.setOnClickListener(v -> contentUpload());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // This is the path to the selected image
                photoUri = data.getData();
                addphoto_image.setImageURI(photoUri);
            } else {
                // Exit the AddPhotoActivity if you leave the album without selecting any image
                finish();
            }
        }
    }

    private void contentUpload() {
        // Make filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timestamp + "_.png";

        StorageReference storageRef = storage.getReference().child("images").child(imageFileName);

        // File upload
        if (photoUri != null) {
            storageRef.putFile(photoUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(AddPhotoActivity.this, "upload", Toast.LENGTH_LONG).show();
                finish();
            });
        }
    }
}