package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd;          //로그인 입력필드
    private Button mBtnFindpwd, mBtnRegister, mBtnLogin;
    private CheckBox auto_login;
    private String loginId, loginPwd;
    private boolean autoLogin;


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
                                autoLoginEdit.putBoolean("checkbox_state", auto_login.isChecked());
                                autoLoginEdit.apply();

                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("checkbox_state", auto_login.isChecked());
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