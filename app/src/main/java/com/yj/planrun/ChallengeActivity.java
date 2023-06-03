package com.yj.planrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.TabActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class ChallengeActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
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