package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button mBtnMain;
    private Button mBtnCalender;
    private Button mBtnCommunity;
    private Button mBtnMyPage;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBtnMain=findViewById(R.id.btn_main);
        mBtnCalender=findViewById(R.id.btn_calender);
        mBtnCommunity=findViewById(R.id.btn_cmnity);
        mBtnMyPage=findViewById(R.id.btn_mypage);


        mBtnCalender.setOnClickListener(new View.OnClickListener() {//캘린더
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, CalenderActivity.class);
                startActivity(intent);
            }
        });

        mBtnMyPage.setOnClickListener(new View.OnClickListener() {//마이페이지
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);

            }
        });
    }
}
