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
import android.widget.ScrollView;
import android.widget.TabHost;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeActivity extends TabActivity {

    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스

    RunningData runningData = new RunningData();

    ImageView day_time_check, day_distance_check, day_pace_check, day_kcal_check; //일일
    ImageView week_time_1h_check, week_time_2h_check, week_distance_10km_check, week_distance_15km_check,
            week_pace_33_check, week_pace_30_check, week_kcal_500_check, week_kcal_700_check; //주간
    ImageView month_time_6h_check, month_time_10h_check, month_distance_50km_check, month_distance_70km_check,
            month_pace_37_check, month_pace_41_check, month_kcal_2400_check, month_kcal_3000_check; //월간

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        //일일
        day_time_check = findViewById(R.id.day_time_check);
        day_distance_check = findViewById(R.id.day_distance_check);
        day_pace_check = findViewById(R.id.day_pace_check);
        day_kcal_check = findViewById(R.id.day_kcal_check);

        //일일

        //주간
        week_time_1h_check = findViewById(R.id.week_time_1h_check);
        week_time_2h_check = findViewById(R.id.week_time_2h_check);
        week_distance_10km_check = findViewById(R.id.week_distance_10km_check);
        week_distance_15km_check = findViewById(R.id.week_distance_15km_check);
        week_pace_33_check = findViewById(R.id.week_pace_33_check);
        week_pace_30_check = findViewById(R.id.week_pace_30_check);
        week_kcal_500_check = findViewById(R.id.week_kcal_500_check);
        week_kcal_700_check = findViewById(R.id.week_kcal_700_check);
        //주간

        //월간
        month_time_6h_check = findViewById(R.id.month_time_6h_check);
        month_time_10h_check = findViewById(R.id.month_time_10h_check);
        month_distance_50km_check = findViewById(R.id.month_distance_50km_check);
        month_distance_70km_check = findViewById(R.id.month_distance_70km_check);
        month_pace_37_check = findViewById(R.id.month_pace_37_check);
        month_pace_41_check = findViewById(R.id.month_pace_41_check);
        month_kcal_2400_check = findViewById(R.id.month_kcal_2400_check);
        month_kcal_3000_check = findViewById(R.id.month_kcal_3000_check);
        //월간

        // 0 : 일일, 1 : 주간, 2 : 월간
        final double[] sum_calories = new double[3];
        final double[] sum_distance = new double[3];
        final double[] sum_pace = new double[3];
        final int[] sum_time = new int[3];
        final String[] runningDate = new String[1];

        mDatabaseRef.child("runningData").child(mFirebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null) {
                    String value = snapshot.getValue().toString();
                    //Log.d("Database", "Value is: " + value);

                    long now = System.currentTimeMillis();
                    Date nowDate = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
                    String getNowDate = sdf.format(nowDate);
                    int intNowDate = Integer.parseInt(getNowDate.substring(getNowDate.lastIndexOf("-")+1)); //~일 구하기
                    int intNowMonth = Integer.parseInt(getNowDate.substring(5, getNowDate.lastIndexOf("-"))); //~월 구하기
                    //Log.d("data", "runningData - StrNowMonth: " + StrNowMonth);

                    //이번주 월요일, 일요일 구하기
                    Calendar mon_cal = Calendar.getInstance(Locale.KOREA);
                    Calendar sun_cal = Calendar.getInstance(Locale.KOREA);
                    mon_cal.add(Calendar.DATE, 2 - mon_cal.get(Calendar.DAY_OF_WEEK));
                    sun_cal.add(Calendar.DATE, 8 - sun_cal.get(Calendar.DAY_OF_WEEK));
                    Date mon_date = mon_cal.getTime();
                    Date sun_date = sun_cal.getTime();
                    String getMonDate = sdf.format(mon_date);
                    String getSunDate = sdf.format(sun_date);
//                    Log.d("data", "runningData - mon_date: " + getMonDate);
//                    Log.d("data", "runningData - sun_date: " + getSunDate);
                    int intMonDate = Integer.parseInt(getMonDate.substring(getMonDate.lastIndexOf("-")+1));
                    int intSunDate = Integer.parseInt(getSunDate.substring(getSunDate.lastIndexOf("-")+1));
                    //Log.d("data", "runningData - mon_date: " + intMonDate);
                    //Log.d("data", "runningData - sun_date: " + intSunDate);

                    Double temp_pace = 0.00;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        HashMap<String, Object> runningDataSnapshot = (HashMap<String, Object>) dataSnapshot.getValue();

                        String pace = runningDataSnapshot.get("pace").toString();

                        if (runningDataSnapshot != null) {
                            String date = runningDataSnapshot.get("date").toString();
                            runningDate[0] = date;
                            int intDateDay = Integer.parseInt(date.substring(date.lastIndexOf("-")+1)); //~일 구하기
                            int intDateMonth = Integer.parseInt(date.substring(5, date.lastIndexOf("-"))); //~월 구하기
                            String date_time = runningDataSnapshot.get("date_time").toString();

                            if (getNowDate.equals(runningDate[0])) { //일일
                                String calories = runningDataSnapshot.get("calories").toString();
                                calories = calories.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                sum_calories[0] += Double.parseDouble(calories);

                                String distance = runningDataSnapshot.get("distance").toString();
                                distance = distance.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                double double_distance = Double.parseDouble(distance);
                                sum_distance[0] += double_distance;

                                pace = pace.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                sum_pace[0] = Double.parseDouble(pace);


                                String time = runningDataSnapshot.get("time").toString();
                                String[] sp_time = time.split(":");
                                String min = sp_time[0];
                                String sec = sp_time[1];
                                int intMin = Integer.parseInt(min);
                                int intSec = Integer.parseInt(sec);
                                //Log.d("data", "runningData - intMin: " + intMin);
                                //Log.d("data", "runningData - intSec: " + intSec);
                                intSec += (intMin * 60);
                                //Log.d("data", "runningData - intSec: " + intSec);
                                sum_time[0] += intSec;
                                //Log.d("data", "runningData - sum_time[0]: " + sum_time[0]);
                            } //일일
                            if (intDateDay >= intMonDate && intDateDay <= intSunDate) { //주간
                                String calories = runningDataSnapshot.get("calories").toString();
                                calories = calories.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                sum_calories[1] += Double.parseDouble(calories);

                                String distance = runningDataSnapshot.get("distance").toString();
                                distance = distance.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                double double_distance = Double.parseDouble(distance);
                                sum_distance[1] += double_distance;

                                pace = pace.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거 = Double.parseDouble(pace);
                                if (Double.parseDouble(pace) >= temp_pace) {
                                    sum_pace[1] = Double.parseDouble(pace);
                                    temp_pace = Double.parseDouble(pace);
                                } else {
                                    sum_pace[1] = temp_pace;
                                }

                                String time = runningDataSnapshot.get("time").toString();
                                String[] sp_time = time.split(":");
                                String min = sp_time[0];
                                String sec = sp_time[1];
                                int intMin = Integer.parseInt(min);
                                int intSec = Integer.parseInt(sec);
                                //Log.d("data", "runningData - intMin: " + intMin);
                                //Log.d("data", "runningData - intSec: " + intSec);
                                intSec += (intMin * 60);
                                //Log.d("data", "runningData - intSec: " + intSec);
                                sum_time[1] += intSec;
                                //Log.d("data", "runningData - sum_time[1]: " + sum_time[1]);
                            } //주간
                            if (intNowMonth == intDateMonth) { //월간
                                String calories = runningDataSnapshot.get("calories").toString();
                                calories = calories.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                sum_calories[2] += Double.parseDouble(calories);

                                String distance = runningDataSnapshot.get("distance").toString();
                                distance = distance.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거
                                double double_distance = Double.parseDouble(distance);
                                sum_distance[2] = Math.round((double_distance + sum_distance[2])*100.00)/100.00; //소수점 계산 오류 해결

                                pace = pace.replaceAll("[^0-9.]", ""); // 숫자와 소수점만 남기고 제거 = Double.parseDouble(pace);
                                if (Double.parseDouble(pace) >= temp_pace) {
                                    sum_pace[2] = Double.parseDouble(pace);
                                    temp_pace = Double.parseDouble(pace);
                                } else {
                                    sum_pace[2] = temp_pace;
                                }

                                String time = runningDataSnapshot.get("time").toString();
                                String[] sp_time = time.split(":");
                                String min = sp_time[0];
                                String sec = sp_time[1];
                                int intMin = Integer.parseInt(min);
                                int intSec = Integer.parseInt(sec);
                                //Log.d("data", "runningData - intMin: " + intMin);
                                //Log.d("data", "runningData - intSec: " + intSec);
                                intSec += (intMin * 60);
                                //Log.d("data", "runningData - intSec: " + intSec);
                                sum_time[2] += intSec;
                                //Log.d("data", "runningData - sum_time[1]: " + sum_time[1]);
                            } //월간
                        }
                    }
                    Log.d("data", "runningData - sum_calories[0]: " + sum_calories[0]);
                    Log.d("data", "runningData - sum_distance[0]: " + sum_distance[0]);
                    Log.d("data", "runningData - sum_pace[0]: " + sum_pace[0]);
                    Log.d("data", "runningData - sum_time[0]: " + sum_time[0]);
                    //일일
                    if (sum_time[0] >= 1200){
                        day_time_check.setVisibility(View.VISIBLE);
                    } else {
                        day_time_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_distance[0] >= 3){
                        day_distance_check.setVisibility(View.VISIBLE);
                    } else {
                        day_distance_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_pace[0] >= 2.7){
                        day_pace_check.setVisibility(View.VISIBLE);
                    } else {
                        day_pace_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_calories[0] >= 100){
                        day_kcal_check.setVisibility(View.VISIBLE);
                    } else {
                        day_kcal_check.setVisibility(View.INVISIBLE);
                    } //일일
                    //주간
                    Log.d("data", "runningData - sum_calories[1]: " + sum_calories[1]);
                    Log.d("data", "runningData - sum_distance[1]: " + sum_distance[1]);
                    Log.d("data", "runningData - sum_pace[1]: " + sum_pace[1]);
                    Log.d("data", "runningData - sum_time[1]: " + sum_time[1]);
                    if (sum_time[1] >= 3600){
                        week_time_1h_check.setVisibility(View.VISIBLE);
                    } else {
                        week_time_1h_check.setVisibility(View.INVISIBLE);
                    }if (sum_time[1] >= 7200){
                        week_time_2h_check.setVisibility(View.VISIBLE);
                    } else {
                        week_time_2h_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_distance[1] >= 10){
                        week_distance_10km_check.setVisibility(View.VISIBLE);
                    } else {
                        week_distance_10km_check.setVisibility(View.INVISIBLE);
                    }if (sum_distance[1] >= 15){
                        week_distance_15km_check.setVisibility(View.VISIBLE);
                    } else {
                        week_distance_15km_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_pace[1] >= 3){
                        week_pace_30_check.setVisibility(View.VISIBLE);
                    } else {
                        week_pace_30_check.setVisibility(View.INVISIBLE);
                    }if (sum_pace[1] >= 3.3){
                        week_pace_33_check.setVisibility(View.VISIBLE);
                    } else {
                        week_pace_33_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_calories[1] >= 500){
                        week_kcal_500_check.setVisibility(View.VISIBLE);
                    } else {
                        week_kcal_500_check.setVisibility(View.INVISIBLE);
                    }if (sum_calories[1] >= 700){
                        week_kcal_700_check.setVisibility(View.VISIBLE);
                    } else {
                        week_kcal_700_check.setVisibility(View.INVISIBLE);
                    }//주간
                    //월간
                    //month_time_6h_check, month_time_10h_check, month_distance_50km_check, month_distance_70km_check,
                    //month_pace_37_check, month_pace_41_check, month_kcal_2400_check, month_kcal_3000_check;
                    Log.d("data", "runningData - sum_calories[2]: " + sum_calories[2]);
                    Log.d("data", "runningData - sum_distance[2]: " + sum_distance[2]);
                    Log.d("data", "runningData - sum_pace[2]: " + sum_pace[2]);
                    Log.d("data", "runningData - sum_time[2]: " + sum_time[2]);
                    if (sum_time[2] >= 21600){
                        month_time_6h_check.setVisibility(View.VISIBLE);
                    } else {
                        month_time_6h_check.setVisibility(View.INVISIBLE);
                    }if (sum_time[2] >= 36000){
                        month_time_10h_check.setVisibility(View.VISIBLE);
                    } else {
                        month_time_10h_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_distance[2] >= 50){
                        month_distance_50km_check.setVisibility(View.VISIBLE);
                    } else {
                        month_distance_50km_check.setVisibility(View.INVISIBLE);
                    }if (sum_distance[2] >= 70){
                        month_distance_70km_check.setVisibility(View.VISIBLE);
                    } else {
                        month_distance_70km_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_pace[2] >= 3.7){
                        month_pace_37_check.setVisibility(View.VISIBLE);
                    } else {
                        month_pace_37_check.setVisibility(View.INVISIBLE);
                    }if (sum_pace[2] >= 4.1){
                        month_pace_41_check.setVisibility(View.VISIBLE);
                    } else {
                        month_pace_41_check.setVisibility(View.INVISIBLE);
                    }
                    if (sum_calories[2] >= 2400){
                        month_kcal_2400_check.setVisibility(View.VISIBLE);
                    } else {
                        month_kcal_2400_check.setVisibility(View.INVISIBLE);
                    }if (sum_calories[2] >= 3000){
                        month_kcal_3000_check.setVisibility(View.VISIBLE);
                    } else {
                        month_kcal_3000_check.setVisibility(View.INVISIBLE);
                    }//월간
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