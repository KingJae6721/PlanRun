package com.yj.planrun;

import static com.yj.planrun.DataLoadingActivity.total_distance;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunRecord2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunRecord2Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RunRecord2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunRecord2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunRecord2Fragment newInstance(String param1, String param2) {
        RunRecord2Fragment fragment = new RunRecord2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Boolean data_exist=false;
        View view =inflater.inflate(R.layout.fragment_run_record2, container, false);

        TextView tv_nickTextView=view.findViewById(R.id.nicknameTextView);
        TextView tv_distance=view.findViewById(R.id.tv_distance);
        TextView tv_kcal= view.findViewById(R.id.tv_kcal);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String getDate = sdf.format(date);
        total_distance=0;double total_calories=0;
        for(RunningData data1: DataLoadingActivity.run_data) {

            if (data1.getDate() != null ) {
                Log.d("로그",Double.toString(total_distance));
                total_distance+=Double.parseDouble(data1.getDistance());
                total_calories+=Double.parseDouble(data1.getCalories());
                data_exist=true;
            }

        }

        if (data_exist==false){
            LinearLayout km = view.findViewById(R.id.layout_km);
            LinearLayout kcal = view.findViewById(R.id.layout_kcal);
            km.setVisibility(View.INVISIBLE);
            kcal.setVisibility(View.INVISIBLE);
            TextView no_data=view.findViewById(R.id.no_data);
            no_data.setVisibility(View.VISIBLE);
        }else{
            tv_distance.setText(Double.toString(total_distance));
            tv_kcal.setText(Double.toString(total_calories));
        }




        tv_nickTextView.setText(String.format("%s님",DataLoadingActivity.nickname));
        return view;
    }
}