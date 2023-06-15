package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firestore = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.detail_text);
        imageView = findViewById(R.id.detail_image);

        String contentId = getIntent().getStringExtra("contentId");
        if (contentId != null) {
            // Firestore에서 해당 문서를 가져오는 코드 추가
            firestore.collection("images").document(contentId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                ContentDTO contentDTO = documentSnapshot.toObject(ContentDTO.class);
                                if (contentDTO != null) {
                                    View view = View.inflate(getApplicationContext(),R.layout.item_detail,null);


                                    String imageUrl = contentDTO.getImageUrl();
                                    String text = contentDTO.getExplain();

                                    // 이미지와 텍스트를 화면에 표시
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Glide.with(DetailActivity.this)
                                                .load(imageUrl)
                                                .apply(RequestOptions.centerCropTransform())
                                                .into(imageView);
                                        imageView.setVisibility(View.VISIBLE);
                                    } else {
                                        imageView.setVisibility(View.GONE);
                                    }

                                    if (text != null && !text.isEmpty()) {
                                        textView.setText(text);
                                        textView.setVisibility(View.VISIBLE);
                                    } else {
                                        textView.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 문서 가져오기 실패 처리
                        }
                    });
        }
    }
}