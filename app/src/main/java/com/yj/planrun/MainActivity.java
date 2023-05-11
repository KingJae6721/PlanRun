package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private Button mBtnMain;
    private Button mBtnCalender;
    private Button mBtnCommunity;
    private final long finishtimeed = 1000;
    private long presstime = 0;

    MainFragment mainFragment; //네비게이션 바(하단바)
    CalendarFragment calendarFragment;
    CommunityFragment communityFragment;
    MypageFragment mypageFragment; // 여기까지

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //네비게이션 바(하단바) start
        mainFragment = new MainFragment();
        calendarFragment = new CalendarFragment();
        communityFragment = new CommunityFragment();
        mypageFragment = new MypageFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //프래그먼트 Transaction 획득
        //프래그먼트를 올리거나 교체하는 작업을 Transaction이라고 합니다.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //프래그먼트를 FrameLayout의 자식으로 등록해줍니다.
        fragmentTransaction.add(R.id.containers, mainFragment);
        //commit을 하면 자식으로 등록된 프래그먼트가 화면에 보여집니다.
        fragmentTransaction.commit();
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) { //
                switch (item.getItemId()) {
                    case R.id.main:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, mainFragment).commit();
                        return true;
                    case R.id.calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, calendarFragment).commit();
                        return true;
                    case R.id.community:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, communityFragment).commit();
                        return true;
                    case R.id.mypage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
                        return true;
                }
                return false;
            }
        }); //네비게이션 바(하단바) end

    }
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime)
        {
            finishAffinity();
        }
        else
        {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}