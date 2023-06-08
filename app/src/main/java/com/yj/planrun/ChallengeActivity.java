package com.yj.planrun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.TabActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeActivity extends TabActivity {

    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스

    RunningData runningData = new RunningData();

    ImageView day_time_check, day_distance_check, day_pace_check, day_kcal_check;


    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        day_time_check = findViewById(R.id.day_time_check);
        day_distance_check = findViewById(R.id.day_distance_check);
        day_pace_check = findViewById(R.id.day_pace_check);
        day_kcal_check = findViewById(R.id.day_kcal_check);


        final AtomicInteger sum_calories = new AtomicInteger(0);
        final AtomicInteger sum_distance = new AtomicInteger(0);
        final AtomicInteger sum_pace = new AtomicInteger(0);
        final AtomicInteger sum_time = new AtomicInteger(0);

        mDatabaseRef.child("runningData").child(mFirebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    //Log.d("Database", "Value is: " + value);

                    int tempSumCalories = 0;
                    int tempSumDistance = 0;
                    int tempSumPace = 0;
                    int tempSumTime = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        HashMap<String, Object> runningDataSnapshot = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (runningDataSnapshot != null) {
                            String calories = runningDataSnapshot.get("calories").toString();
                            try {
                                calories = calories.substring(0, 3);
                                tempSumCalories += Integer.parseInt(calories);
                            } catch (NumberFormatException e) {
                                // 정수로 변환할 수 없는 경우 처리할 내용
                                Log.e("Firebase", "Failed to parse calories: " + calories, e);
                            }

                            String date = "";
                            Object dateObj = runningDataSnapshot.get("date");
                            if (dateObj != null) {
                                date = dateObj.toString();
                            }

                            String date_time = "";
                            Object date_timeObj = runningDataSnapshot.get("date_time");
                            if (date_timeObj != null) {
                                date_time = date_timeObj.toString();
                            }

                            String distance = runningDataSnapshot.get("distance").toString();
                           // distance = distance.substring(0,2) + distance.substring(3);
                            distance = distance.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                            double distanceValue = Double.parseDouble(distance);
                            int intValue = (int) distanceValue;
                            tempSumDistance += intValue;

                            String pace = runningDataSnapshot.get("pace").toString();
                            pace = pace.substring(0,2);
                            tempSumPace += Integer.parseInt(pace);

                            String time = runningDataSnapshot.get("time").toString();
                            String[] sp_time = time.split(":");
                            String min = sp_time[0];
                            String sec = sp_time[1];
                            sec += Integer.parseInt(min) * 60;
                            tempSumTime += Integer.parseInt(sec);
                        }
                    }
                    sum_calories.set(tempSumCalories);
                    tempSumDistance = tempSumDistance / 100;
                    sum_distance.set(tempSumDistance);
                    sum_pace.set(tempSumPace);
                    tempSumTime = tempSumTime / 10;
                    sum_time.set(tempSumTime);
                    Log.d("Firebase", "runningData - sum_calories: " + sum_calories);
                    Log.d("Firebase", "runningData - sum_distance: " + sum_distance);
                    Log.d("Firebase", "runningData - sum_pace: " + sum_pace);
                    Log.d("Firebase", "runningData - sum_time: " + sum_time);

                    //day_time_check, day_distance_check, day_pace_check, day_kcal_check
                    if (sum_time.get() >= 60){
                        day_time_check.setVisibility(View.VISIBLE);
                    } else {
                        day_time_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_distance.get() >= 5){
                        day_distance_check.setVisibility(View.VISIBLE);
                    } else {
                        day_distance_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_pace.get() >= 5){
                        day_pace_check.setVisibility(View.VISIBLE);
                    } else {
                        day_pace_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_calories.get() >= 100){
                        day_kcal_check.setVisibility(View.VISIBLE);
                    } else {
                        day_kcal_check.setVisibility(View.INVISIBLE);
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


        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpecDay = tabHost.newTabSpec("DAY").setIndicator("일 일");
        tabSpecDay.setContent(R.id.tabDay);
        tabHost.addTab(tabSpecDay);

        TabHost.TabSpec tabSpecWeek = tabHost.newTabSpec("WEEK").setIndicator("주 간");
        tabSpecWeek.setContent(R.id.tabWeek);
        tabHost.addTab(tabSpecWeek);

        TabHost.TabSpec tabSpecMonth = tabHost.newTabSpec("MONTH").setIndicator("월 간");
        tabSpecMonth.setContent(R.id.tabMonth);
        tabHost.addTab(tabSpecMonth);

        tabHost.setCurrentTab(0);
    }
}