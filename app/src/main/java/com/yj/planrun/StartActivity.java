package com.yj.planrun;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        boolean autoLogin = sharedPreferences.getBoolean("checkbox_state", false);

        // 자동 로그인 체크박스가 체크되어 있으면, LoginActivity 대신 MainActivity로 이동
        if (autoLogin) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 파괴
        }

        Button imageButton = (Button) findViewById(R.id.btn_login);
        Button btn_kakao = (Button) findViewById(R.id.btn_kakao);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent1);
            }
        });

        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}