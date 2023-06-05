package com.yj.planrun;

import static android.content.Context.MODE_NO_LOCALIZED_COLLATORS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

public class CalendarFragment extends Fragment {


    public MaterialCalendarView calendarView;
    public TextView diaryTextView, tv_pace,tv_distance,tv_date;


    private FirebaseDatabase mDatabase;
    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스
    private  ArrayList<CalendarDay> calendarDayList;



    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        diaryTextView = view.findViewById(R.id.diaryTextView);
        tv_date=view.findViewById(R.id.tv_date);
        tv_pace=view.findViewById(R.id.tv_pace);
        tv_distance=view.findViewById(R.id.tv_distance);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();//파이어베이스 객체 가지고와서서
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("runningData").child(firebaseUser.getUid());//루트설정

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                diaryTextView.setVisibility(View.VISIBLE);

                diaryTextView.setText(String.format("%d / %d / %d", date.getYear(), date.getMonth(), date.getDay()));


                for(RunningData data1: DataLoadingActivity.run_data) {
                    if(data1.getDate().equals(date.getYear()+"-"+ date.getMonth()+"-"+date.getDay())){
                        tv_distance.setText(data1.getDistance());
                        tv_date.setText(data1.getDate());
                        tv_pace.setText(data1.getPace());
                    }
                }

            }
        });

        //달력에 배경 그리기

        calendarDayList = new ArrayList<>();
        int year, month, day;
        for(RunningData data1: DataLoadingActivity.run_data){

            String str =data1.getDate();
            String []date=str.split("-");
            year=Integer.parseInt(date[0]);
            month=Integer.parseInt(date[1]);
            day=Integer.parseInt(date[2]);
            Log.d("로그",date[0]+date[1]+date[2]);

            calendarDayList.add(CalendarDay.from(year,month,day));
            calendarView.addDecorators(new EventDecorator(calendarDayList, getActivity()));
        }
        return view;
    }//onCreate
}//CalenderFragment 클래스



//캘린더에 배경 추가
class EventDecorator implements DayViewDecorator {


    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(Collection<CalendarDay> dates, Context context) {
        drawable =  ContextCompat.getDrawable(context,R.drawable.ic_calender_checked);

        this.dates = new HashSet<>(dates);

    }

    @Override
    public boolean shouldDecorate(CalendarDay day){
        return dates.contains(day);
    }


    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }


}