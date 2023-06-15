package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FollowlistActivity extends AppCompatActivity implements FollowingAccountsAdapter.OnItemClickListener {
    private FirebaseFirestore firestore;
    private DatabaseReference mDatabaseRef;
    private String selectedNickname; // 클릭된 닉네임을 저장할 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followlist);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("팔로우한 사용자");
        }

        String userUid = getIntent().getStringExtra("userUid");
        firestore = FirebaseFirestore.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");
        RecyclerView followingAccounts = findViewById(R.id.following_accounts);

        firestore.collection("users").document(userUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FollowDTO followDTO = document.toObject(FollowDTO.class);
                            ArrayList<String> followingAccountsList = new ArrayList<>(followDTO.getFollowingNicknames().values());
                            followingAccounts.setAdapter(new FollowingAccountsAdapter(followingAccountsList, this));
                            followingAccounts.setLayoutManager(new LinearLayoutManager(FollowlistActivity.this));
                        } else {
                            // No user account found
                        }
                    } else {
                        // request failed
                    }
                });
    }
    @Override
    public void onItemClick(String nickname) {
        selectedNickname = nickname; // 클릭된 닉네임을 저장
        // 필요한 작업 수행
        mDatabaseRef.child("UserAccount")
                .orderByChild("nickname")
                .equalTo(nickname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User found
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String uid = userSnapshot.getKey();
                                // Navigate to the user's profile page
                                showMypageFragment(uid, nickname);
                            }
                        } else {
                            // User not found
                            Toast.makeText(FollowlistActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Error handling
                        Toast.makeText(FollowlistActivity.this, "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showMypageFragment(String uid, String nickname) {
        MypageFragment mypageFragment = new MypageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("destinationUid", uid);
        bundle.putString("userId", nickname);
        mypageFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
    }

}