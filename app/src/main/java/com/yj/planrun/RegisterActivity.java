package com.yj.planrun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class    RegisterActivity extends AppCompatActivity {

    private  FirebaseAuth mFirebaseAuth;    //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd, mEtPwdCheck, mEtNickname;      //회원가입 입력필드
    private Button mBtnRegister;            //회원가입 버튼
    private TextView mPwdCondition,mEmailCondition, mNicknameCondition;
    private boolean isEmailAvailable = false;
    private boolean isNicknameAvailable = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mFirebaseAuth= FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("PlanRun");//루트설정

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwdCheck = findViewById(R.id.et_pwd_check);
        mEtNickname = findViewById(R.id.et_nickname);

        mBtnRegister =findViewById(R.id.btn_register);
        mPwdCondition = findViewById(R.id.pwd_condition);
        mEmailCondition = findViewById(R.id.email_condition);
        mNicknameCondition = findViewById(R.id.nickname_condition);



        mEtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String nickname = s.toString();
                mDatabaseRef.child("UserAccount").orderByChild("nickname").equalTo(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mNicknameCondition.setText("이미 사용중인 닉네임입니다.");
                            mNicknameCondition.setTextColor(getResources().getColor(R.color.red));
                            isNicknameAvailable = false;
                        } else {
                            mNicknameCondition.setText("사용 가능한 닉네임입니다.");
                            mNicknameCondition.setTextColor(getResources().getColor(R.color.green));
                            isNicknameAvailable = true;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //이메일 조건 검사
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();
                String Pattern = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$";
                if (email.matches(Pattern)) {
                    // 이메일 주소 형식이 올바른 경우 이메일 중복 검사
                    checkRegisteredEmail(email);
                } else {
                    // 이메일 주소 형식이 올바르지 않은 경우
                    mEmailCondition.setText("잘못된 이메일 주소입니다.");
                    mEmailCondition.setTextColor(getResources().getColor(R.color.red));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });





        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$";

                if (password.matches(pattern)) {
                    mPwdCondition.setText("조건에 맞습니다.");
                    mPwdCondition.setTextColor(getResources().getColor(R.color.green));
                } else if (password.length() < 8) {
                    mPwdCondition.setText("최소 8자 이상이어야 합니다.");
                    mPwdCondition.setTextColor(getResources().getColor(R.color.red));
                } else if (!password.matches(".*[0-9].*")) {
                    mPwdCondition.setText("숫자가 적어도 하나 이상 포함되어야 합니다.");
                    mPwdCondition.setTextColor(getResources().getColor(R.color.red));
                } else if (!password.matches(".*[a-z].*")) {
                    mPwdCondition.setText("소문자가 적어도 하나 이상 포함되어야 합니다.");
                    mPwdCondition.setTextColor(getResources().getColor(R.color.red));
                } /*else if (!password.matches(".*[@#$%^&+=!].*")) {
                    mPwdCondition.setText("특수문자가 적어도 하나 이상 포함되어야 합니다.");
                    mPwdCondition.setTextColor(getResources().getColor(R.color.red));
                }*/else {
                    mPwdCondition.setText("조건에 맞지 않습니다.");
                    mPwdCondition.setTextColor(getResources().getColor(R.color.red));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                //회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwdCheck = mEtPwdCheck.getText().toString();

                if(mEtNickname.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //아이디와 비밀번호가 입력되었는지 확인
                if (strEmail.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strPwd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strPwdCheck.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "비밀번호 확인을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isNicknameAvailable) {
                    Toast.makeText(RegisterActivity.this, "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //이메일 형식이 맞는지 확인
                if (!isValidEmail(strEmail)) {
                    Toast.makeText(RegisterActivity.this, "이메일 형식에 맞지 않는 메일 주소입니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isEmailAvailable) {
                    Toast.makeText(RegisterActivity.this, "이미 사용중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //비밀번호 형식이 맞는지 확인
                if (!isValidPassword(strPwd)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호 형식이 맞지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mEtPwd.getText().toString().equals(mEtPwdCheck.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "비밀번호와 비밀번호 확인이 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Firebase Auth진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();//파이어베이스 객체 가지고와서서
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            account.setNickname(mEtNickname.getText().toString());

                            //setValue : database에 Insert(삽입)하는 행위
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                            Toast.makeText(RegisterActivity.this,"회원가입성공!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"회원가입실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
    private boolean isValidEmail(String email){
        String Pattern = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$";
        return email.matches(Pattern);
    }
    private boolean isValidPassword(String password) {
        //정규식               0~9숫자포함    a~z포함    A~Z포함       특수문자포함   공백포함X   8자리이상
        // String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }



    //이메일 중복 여부
    private void checkRegisteredEmail(final String email) {
        mDatabaseRef.child("UserAccount").orderByChild("emailId").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 이미 사용중인 이메일
                    mEmailCondition.setText("이미 사용중인 이메일입니다.");
                    mEmailCondition.setTextColor(getResources().getColor(R.color.red));
                    isEmailAvailable = false;
                } else {
                    // 사용 가능한 이메일
                    mEmailCondition.setText("사용 가능한 이메일입니다.");
                    mEmailCondition.setTextColor(getResources().getColor(R.color.green));
                    isEmailAvailable = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 실패한 경우
                mEmailCondition.setText("이메일 검증 오류");
                mEmailCondition.setTextColor(getResources().getColor(R.color.red));
            }
        });
    }
}
