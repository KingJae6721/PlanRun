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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    public TextView diaryTextView, tv_pace,tv_distance,tv_date,no_data;



    private FirebaseDatabase mDatabase;
    private FirebaseAuth mFirebaseAuth;         //파이어베이스 인증
    private DatabaseReference mDatabaseRef;     //실시간 데이터베이스
    private  ArrayList<CalendarDay> calendarDayList;
    private LinearLayout layout_record_parent;


    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        diaryTextView = view.findViewById(R.id.diaryTextView);
        ActiveRecord a = new ActiveRecord(view.getContext().getApplicationContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View additionalView = layoutInflater.inflate(R.layout.record_active, null);
        View additionalView1 = layoutInflater.inflate(R.layout.record_active, null);
        LinearLayout layout_record= (LinearLayout)view.findViewById(R.id.layout_record1);
        layout_record_parent= view.findViewById(R.id.layout_record_parent);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();//파이어베이스 객체 가지고와서서
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("runningData").child(firebaseUser.getUid());//루트설정

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                layout_record.removeAllViews();
                diaryTextView.setVisibility(View.VISIBLE);

                diaryTextView.setText(String.format("%d월 %d일", date.getMonth()+1, date.getDay()));
                boolean exist_data=false;
                ArrayList<RunningData> selected_date_data= new ArrayList<RunningData>();

                for(RunningData data1: DataLoadingActivity.run_data) {

                    if (data1.getDate() != null && data1.getDate().equals(date.getYear() + "-" + (date.getMonth()+1)+ "-" + date.getDay())) {
                        selected_date_data.add(data1);
                        exist_data=true;
                    }

                }
                layout_record_parent.setVisibility(View.VISIBLE);
                if (exist_data == false){

                    View additionalView = layoutInflater.inflate(R.layout.record_active, null);
                    tv_date=(TextView) additionalView.findViewById(R.id.tv_date);
                    layout_record.addView(additionalView);
                    RelativeLayout detail = additionalView.findViewById(R.id.layout_record);
                    no_data=layout_record.findViewById(R.id.no_data);

                    no_data.setVisibility(View.VISIBLE);
                    detail.setVisibility(View.INVISIBLE);

                }else{
                    for(RunningData a1:selected_date_data){
                        View additionalView = layoutInflater.inflate(R.layout.record_active, null);

                        tv_date=(TextView) additionalView.findViewById(R.id.tv_date);
                        tv_pace=(TextView)additionalView.findViewById(R.id.tv_pace);
                        tv_distance=(TextView)additionalView.findViewById(R.id.tv_distance);
                        TextView tv_time = (TextView) additionalView.findViewById(R.id.tv_time);
                        TextView tv_calorie = (TextView) additionalView.findViewById(R.id.tv_kcal);

                        tv_distance.setText(a1.getDistance());
                        tv_date.setText(a1.getDate()+" "+a1.getDate_time());
                        tv_pace.setText(a1.getPace());
                        tv_time.setText(a1.getTime());
                        tv_calorie.setText(a1.getCalories());

                        layout_record.addView(additionalView);



                        RelativeLayout detail = additionalView.findViewById(R.id.layout_record);
                            detail.setVisibility(View.VISIBLE);

                    }
                }

            }
        });

        //달력에 배경 그리기

        calendarDayList = new ArrayList<>();
        int year, month, day;
        for(RunningData data1: DataLoadingActivity.run_data){

            String str =data1.getDate();
            if (str != null) {
                String[] date = str.split("-");
                year=Integer.parseInt(date[0]);
                month=Integer.parseInt(date[1])-1;
                day=Integer.parseInt(date[2]);
                Log.d("로그",date[0]+date[1]+date[2]);

                calendarDayList.add(CalendarDay.from(year,month,day));
                calendarView.addDecorators(new EventDecorator(calendarDayList, getActivity()));
            }
            ///String []date=str.split("-");
        }
        return view;
    }//onCreate

    public void setRecord(){

    }

}//CalenderFragment 클래스



//캘린더에 배경 추가
class EventDecorator implements DayViewDecorator {


    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(Collection<CalendarDay> dates, Context context) {
        drawable =  ContextCompat.getDrawable(context,R.drawable.ic_calender_checked_size);
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