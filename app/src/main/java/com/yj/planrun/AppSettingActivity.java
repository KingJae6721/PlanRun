package com.yj.planrun;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppSettingActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Switch notification_all, notification_commnication, notification_notice, notification_event, notification_marketing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appsetting);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("PlanRun");//루트설정
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();//파이어베이스 객체 가지고와서서

        notification_all=findViewById(R.id.notification_set_all);
        notification_commnication=findViewById(R.id.notification_set_community);
        notification_notice=findViewById(R.id.notification_set_notice);
        notification_event=findViewById(R.id.notification_set_event);
        notification_marketing=findViewById(R.id.notification_set_marketing);

        notification_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(notification_all.isChecked()==false){
                    notification_all.setChecked(false);
                    notification_commnication.setChecked(false);
                    notification_event.setChecked(false);
                    notification_notice.setChecked(false);
                    notification_marketing.setChecked(false);

                }
                else{
                    if(notification_all.isChecked()==true){
                        notification_all.setChecked(true);
                        notification_commnication.setChecked(true);
                        notification_event.setChecked(true);
                        notification_notice.setChecked(true);
                        notification_marketing.setChecked(true);

                    }
                }
            }
        });


        Button btn_acntDel = findViewById(R.id.btn_acntDel);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        Button btn_changePwd = (Button) findViewById(R.id.btn_changePwd);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), firebaseUser.getUid(), Toast.LENGTH_LONG).show();
                finish();
            }

        });
        btn_changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(AppSettingActivity.this);
                ad.setIcon(R.mipmap.ic_launcher);
                ad.setTitle("비밀번호 변경");
                ad.setMessage("비밀번호를 변경하시겠습니까?");
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        send();
                        Toast.makeText(getApplicationContext(), "이메일 전송 완료", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });

        btn_acntDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppSettingActivity.this);
                alertDialogBuilder.setMessage("정말로 회원 탈퇴를 하시겠습니까?");
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UserAccount account = new UserAccount();

                        String userToken= firebaseUser.getUid();

                        // Firebase Authentication에서 현재 로그인한 사용자 삭제
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabaseRef.child("UserAccount").child(userToken).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                        // Firebase Authentication에서 현재 로그인한 사용자 삭제
                                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(AppSettingActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("error: "+e.getMessage());
                                                Toast.makeText(getApplicationContext(), "인증 정보 삭제 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("error: "+e.getMessage());
                                        Toast.makeText(getApplicationContext(), "회원 탈퇴에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                    }
                                });
                                Intent intent = new Intent(AppSettingActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("error: "+e.getMessage());
                                Toast.makeText(getApplicationContext(), "인증 정보 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // do nothing.
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }
    private void send() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        String emailAddress = user.getEmail();
        mFirebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                }
            }
        });
    }
}
