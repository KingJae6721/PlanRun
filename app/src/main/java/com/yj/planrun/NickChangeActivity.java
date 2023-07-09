package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NickChangeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickchange);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        EditText et_changeNick = findViewById(R.id.et_changeNick);
        Button btn_nickChange = findViewById(R.id.btn_nickChange);

        mDatabaseRef.child("PlanRun").child("UserAccount").child(mFirebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null) {
                    HashMap<String, Object> runningDataSnapshot = (HashMap<String, Object>) snapshot.getValue();
                    if (runningDataSnapshot != null) {
                        String nick = runningDataSnapshot.get("nickname").toString();
                        //Log.d("data", "runningDataSnapshot : " + nick);
                        btn_nickChange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String changeNick = et_changeNick.getText().toString();
                                //Log.d("data", "et_changeNick : " + changeNick);
                                if (changeNick.equals(nick))
                                {
                                    Toast toast = Toast.makeText(getApplicationContext(), "이전 닉네임과 동일합니다.",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else
                                {
                                    runningDataSnapshot.put("nickname", changeNick);
                                    mDatabaseRef.child("PlanRun").child("UserAccount").child(mFirebaseAuth.getUid()).updateChildren(runningDataSnapshot);
                                    Toast toast = Toast.makeText(getApplicationContext(), "닉네임이 변경되었습니다.",Toast.LENGTH_SHORT);
                                    toast.show();
                                    finish();
                                }
                            }
                        });
                    }
                }
                else {
                    Log.w("Database", "Snapshot is null or has no value.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        Button btn_cancel = findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}