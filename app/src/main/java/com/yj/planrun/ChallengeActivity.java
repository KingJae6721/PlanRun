package com.yj.planrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChallengeActivity extends AppCompatActivity {

    private DayFragment dayFragment;
    private WeekFragment weekFragment;
    private MonthFragment monthFragment;
    Button day,week,month;
    View dayView,weekView,monthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        dayFragment = new DayFragment();
        weekFragment = new WeekFragment();
        monthFragment = new MonthFragment();

        day = findViewById(R.id.day);
        week = findViewById(R.id.week);
        month = findViewById(R.id.month);
        dayView = findViewById(R.id.dayView);
        weekView = findViewById(R.id.weekView);
        monthView = findViewById(R.id.monthView);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame,dayFragment);
        dayView.setBackgroundColor(Color.YELLOW);
        weekView.setBackgroundColor(Color.parseColor("#8C8A8A"));
        monthView.setBackgroundColor(Color.parseColor("#8C8A8A"));
        fragmentTransaction.commit();

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayView.setBackgroundColor(Color.YELLOW);
                weekView.setBackgroundColor(Color.parseColor("#8C8A8A"));
                monthView.setBackgroundColor(Color.parseColor("#8C8A8A"));
                FragmentManager fm1 = getSupportFragmentManager();
                FragmentTransaction ft1 = fragmentManager.beginTransaction();
                ft1.replace(R.id.fragmentFrame, dayFragment);
                ft1.commit();
            }
        });
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayView.setBackgroundColor(Color.parseColor("#8C8A8A"));
                weekView.setBackgroundColor(Color.YELLOW);
                monthView.setBackgroundColor(Color.parseColor("#8C8A8A"));
                FragmentManager fm2 = getSupportFragmentManager();
                FragmentTransaction ft2 = fragmentManager.beginTransaction();
                ft2.replace(R.id.fragmentFrame, weekFragment);
                ft2.commit();
            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayView.setBackgroundColor(Color.parseColor("#8C8A8A"));
                weekView.setBackgroundColor(Color.parseColor("#8C8A8A"));
                monthView.setBackgroundColor(Color.YELLOW);
                FragmentManager fm3 = getSupportFragmentManager();
                FragmentTransaction ft3 = fragmentManager.beginTransaction();
                ft3.replace(R.id.fragmentFrame, monthFragment);
                ft3.commit();
            }
        });

    }
}