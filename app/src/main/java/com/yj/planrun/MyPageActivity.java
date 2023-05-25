package com.yj.planrun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPageActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃하기
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MyPageActivity.this, StartActivity.class);
                startActivity(intent);
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
        });


        //탈퇴처리
        //mFirebaseAuth.getCurrentUser().delete();
    }

}