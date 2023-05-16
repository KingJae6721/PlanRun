package com.yj.planrun;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.yj.planrun.MainActivity;

public class MainFragment extends Fragment {

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {   View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button btn_run = (Button) view.findViewById(R.id.btn_run);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RunActivity.class);
                startActivity(intent);
            }
        });

        Button viewAll = (Button) view.findViewById(R.id.viewAll);
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChallengeActivity.class);
                startActivity(intent);
            }
        });



        TextView nicknameTextView = view.findViewById(R.id.nicknameTextView);
        /*FirebaseAuth auth  = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseAuth = auth.getCurrentUser();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");
        if (mFirebaseAuth != null) {
            mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nickname = snapshot.getValue(String.class);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainFragment", "데이터 로딩 실패: " + error.getMessage());
                }
            });
        }*/
        nicknameTextView.setText(MainActivity.nickname);

        return view;
    }

}