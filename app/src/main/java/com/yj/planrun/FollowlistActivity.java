package com.yj.planrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FollowlistActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("팔로우한 사용자");
        }

        String userUid = getIntent().getStringExtra("userUid");

        firestore = FirebaseFirestore.getInstance();

        RecyclerView followingAccounts = findViewById(R.id.following_accounts);

        firestore.collection("users").document(userUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FollowDTO followDTO = document.toObject(FollowDTO.class);
                            ArrayList<String> followingAccountsList = new ArrayList<>(followDTO.getFollowings().keySet());
                            followingAccounts.setAdapter(new FollowingAccountsAdapter(followingAccountsList));
                            followingAccounts.setLayoutManager(new LinearLayoutManager(FollowlistActivity.this));
                        } else {
                            // No user account found
                        }
                    } else {
                        // request failed
                    }
                });
    }
}