package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddPhotoActivity extends AppCompatActivity {
    private final int PICK_IMAGE_FROM_ALBUM = 0;
    private FirebaseStorage storage;
    private Uri photoUri;
    private ImageView addphoto_image;
    private Button addphoto_btn_upload,addphoto_btn_add;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText addphoto_edit_explain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        addphoto_image = findViewById(R.id.addphoto_image);
        addphoto_btn_upload = findViewById(R.id.addphoto_btn_upload);
        addphoto_edit_explain = findViewById(R.id.addphoto_edit_explain);
        addphoto_btn_add = findViewById(R.id.addphoto_btn_add);

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
        StorageReference storageRef = storage.getReference().child("images").child(imageFileName);

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
                    ContentDTO contentDTO = new ContentDTO();
                    // Insert downloadUrl of image
                    contentDTO.setImageUrl(uri.toString());
                    // Insert uid of user
                    contentDTO.setUid(auth.getCurrentUser().getUid());
                    // Insert userId
                    contentDTO.setUserId(auth.getCurrentUser().getEmail());
                    // Insert explain of content
                    contentDTO.setExplain(addphoto_edit_explain.getText().toString());
                    // Insert timestamp
                    contentDTO.setTimestamp(System.currentTimeMillis());

                    // 여기에서 사용자의 닉네임을 가져와서 저장
                    String userNickname = DataLoadingActivity.nickname; // DataLoadingActivity에서 가져온 닉네임					//이부분
                    contentDTO.setNickname(userNickname);

                    firestore.collection("images").document().set(contentDTO);
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        } else {
            // If photoUri is null, upload only text content or handle the case accordingly
            ContentDTO contentDTO = new ContentDTO();
            // Insert uid of user
            contentDTO.setUid(auth.getCurrentUser().getUid());
            // Insert userId
            contentDTO.setUserId(auth.getCurrentUser().getEmail());
            // Insert explain of content
            contentDTO.setExplain(addphoto_edit_explain.getText().toString());
            // Insert timestamp
            contentDTO.setTimestamp(System.currentTimeMillis());

            // 여기에서 사용자의 닉네임을 가져와서 저장
            String userNickname = DataLoadingActivity.nickname; // DataLoadingActivity에서 가져온 닉네임						//이부분
            contentDTO.setNickname(userNickname);

            firestore.collection("images").document().set(contentDTO);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}