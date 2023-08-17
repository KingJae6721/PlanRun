package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateClubActivity extends AppCompatActivity {
    private final int PICK_IMAGE_FROM_ALBUM = 0;
    private FirebaseStorage storage;
    private Uri photoUri;
    private ImageView addphoto_image;
    private Button addphoto_btn_upload,addphoto_btn_add;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText addphoto_edit_explain, createclub_edit_clubname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);
        Button cancel = findViewById(R.id.create_club_btn_cancel);//수정해야함
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        addphoto_image = findViewById(R.id.addphoto_image);
        addphoto_btn_upload = findViewById(R.id.create_club_btn_upload);
        addphoto_edit_explain = findViewById(R.id.createclub_edit_explain);
        addphoto_btn_add = findViewById(R.id.addphoto_btn_add);
        createclub_edit_clubname = findViewById(R.id.createclub_edit_clubname);
        // Initialize storage
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Open the album
        addphoto_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the album
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });
        //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //photoPickerIntent.setType("image/*");
        //startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);

        // Add image upload event
        addphoto_btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentUpload();
            }
        });



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
        StorageReference storageRef = storage.getReference().child("club").child(imageFileName);

        /*
        // File upload
        if (photoUri != null) {
            storageRef.putFile(photoUri).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(AddPhotoActivity.this, "upload", Toast.LENGTH_LONG).show();
                finish();
            });
        }
        */

        // FileUpload
        // Upload image if photoUri is not null
        if (photoUri != null) {
            storageRef.putFile(photoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return storageRef.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ClubDTO clubDTO = new ClubDTO();
                    // Insert downloadUrl of image
                    clubDTO.setImageUrl(uri.toString());
                    // Insert uid of user
                    clubDTO.setUid(auth.getCurrentUser().getUid());
                    // Insert userId
                    clubDTO.setClubName(auth.getCurrentUser().getEmail());
                    // Insert explain of content
                    clubDTO.setExplain(addphoto_edit_explain.getText().toString());
                    // Insert timestamp
                    clubDTO.setTimestamp(System.currentTimeMillis());
                    firestore.collection("club").document().set(clubDTO);
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        } else {
            // If photoUri is null, upload only text content or handle the case accordingly
            ClubDTO clubDTO = new ClubDTO();
            // Insert uid of user
            clubDTO.setUid(auth.getCurrentUser().getUid());
            // Insert userId
            clubDTO.setClubName(createclub_edit_clubname.getText().toString());
            // Insert explain of content
            clubDTO.setExplain(addphoto_edit_explain.getText().toString());
            // Insert timestamp
            clubDTO.setTimestamp(System.currentTimeMillis());
            firestore.collection("club").document().set(clubDTO);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}