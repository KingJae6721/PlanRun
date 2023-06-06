package com.yj.planrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.TabActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import com.google.android.material.tabs.TabLayout;

public class ChallengeActivity extends FragmentActivity {

    TabLayout tabs;

    DayFragment dayFragment;
    WeekFragment weekFragment;
    MonthFragment monthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        dayFragment = new DayFragment();
        weekFragment = new WeekFragment();
        monthFragment = new MonthFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, dayFragment).commit();

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("일 일"));
        tabs.addTab(tabs.newTab().setText("주 간"));
        tabs.addTab(tabs.newTab().setText("월 간"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int postion = tab.getPosition();
                Fragment selected = null;
                if(postion == 0)
                    selected = dayFragment;
                else if (postion == 1)
                    selected = weekFragment;
                else if (postion == 2)
                    selected = monthFragment;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}