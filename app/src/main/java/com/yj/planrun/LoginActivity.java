package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd;          //로그인 입력필드
    private Button mBtnFindpwd, mBtnRegister, mBtnLogin;
    private CheckBox auto_login;
    private String loginId, loginPwd;


    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        loginId = sharedPreferences.getString("userId", null);
        loginPwd = sharedPreferences.getString("passwordNo", null);

        if (loginId != null && loginPwd != null) {
            // 자동 로그인 데이터가 있으면 로그인 시도
            mFirebaseAuth.signInWithEmailAndPassword(loginId, loginPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // 로그인 성공시 자동 로그인 체크박스 상태 저장
                        if (auto_login.isChecked()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", loginId);
                            editor.putString("passwordNo", loginPwd);
                            editor.apply();
                        }

                        // 로그인 성공시 MyPageActivity.class로 인텐트 전달
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("id", loginId);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    } else {
                        Toast.makeText(LoginActivity.this, "자동 로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // 자동 로그인 데이터가 없으면 자동 로그인 체크박스 해제
            if (auto_login != null) {
                auto_login.setChecked(false);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnFindpwd = findViewById(R.id.btn_findpwd);
        mBtnRegister = findViewById(R.id.btn_register);
        auto_login = findViewById(R.id.auto_login);

        mBtnRegister.setPaintFlags(mBtnRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // 글자 밑줄 생성 코드
        mBtnFindpwd.setPaintFlags(mBtnFindpwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // 글자 밑줄 생성 코드

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (auto_login.isChecked()) {
                                // 자동 로그인 데이터 저장
                                SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLoginEdit = sharedPreferences.edit();
                                autoLoginEdit.putString("userId", strEmail);
                                autoLoginEdit.putString("passwordNo", strPwd);
                                autoLoginEdit.commit();
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); //현재 액티비티 파괴
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });





        Button mBtnRegister = findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
