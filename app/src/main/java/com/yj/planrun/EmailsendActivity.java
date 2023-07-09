package com.yj.planrun;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EmailsendActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailsend);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(EmailsendActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmationDialog(email);
                }
            }
        });
    }
    private void showConfirmationDialog(final String email) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("비밀번호를 변경하시겠습니까?");
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendPasswordResetEmail(email);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "비밀번호 변경 이메일이 전송되었습니다.");
                            Toast.makeText(EmailsendActivity.this, "비밀번호 변경 이메일이 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "비밀번호 변경 이메일 전송 실패: " + task.getException());
                            Toast.makeText(EmailsendActivity.this, "비밀번호 변경 이메일 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}