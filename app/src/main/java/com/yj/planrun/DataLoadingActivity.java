package com.yj.planrun;

import static com.yj.planrun.R.layout.activity_data_loading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawableResource;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DataLoadingActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;
    public static  ArrayList <RunningData> run_data;
    public static String email, nickname;
    public static double total_distance=0.0;
    //UserAccount정보, 러닝 데이터 받아오기
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();//파이어베이스 객체 가지고와서서
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_data_loading);
        ImageView loading_img = (ImageView) findViewById(R.id.loading_gif);
        Glide.with(this).asGif().load(R.drawable.loading_img).into(loading_img);
        //로딩화면 시작.
        Loadingstart();





        setFirebaseNicknameEmail();

    }
    private void Loadingstart(){

        if (mFirebaseAuth != null) {
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("runningData").child(firebaseUser.getUid());//루트설정
            readData(mDatabaseRef, new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    run_data = new ArrayList<RunningData>();
                    for(DataSnapshot runningData : dataSnapshot.getChildren()){
                        run_data.add(runningData.getValue(RunningData.class));
                        Log.d("로그","추가");
                    }
                    for(RunningData data1: run_data) total_distance+=Double.parseDouble(data1.getDistance());
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onStart() {
                    //when starting
                    Log.d("ONSTART", "Started");
                }

                @Override
                public void onFailure() {
                    Log.d("onFailure", "Failed");
                }
            });
        }
    }

    public void setFirebaseNicknameEmail() {


            mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");
        if (mFirebaseAuth != null) {
           /* mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataLoadingActivity.nickname = snapshot.getValue(String.class);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainFragment", "데이터 로딩 실패: " + error.getMessage());
                }
            });*/

            readData(mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("nickname"), new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataLoadingActivity.nickname = dataSnapshot.getValue(String.class);
                    }
                    Log.d("OnSUCCESS", "getEmail");
                }

                @Override
                public void onStart() {
                    Log.d("ONSTART", "getNickname");
                }

                @Override
                public void onFailure() {

                }
            });
        }

        if (mFirebaseAuth != null) {
            /*mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("emailId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataLoadingActivity.email = snapshot.getValue(String.class);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainFragment", "데이터 로딩 실패: " + error.getMessage());
                }
            });*/


            readData(mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("emailId"), new OnGetDataListener() {//이메일 받아오기
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataLoadingActivity.email = dataSnapshot.getValue(String.class);
                    }
                    Log.d("OnSUCCESS", "getEmail");
                }

                @Override
                public void onStart() {
                    Log.d("ONSTART", "getEmail");
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }

        });

    }
}