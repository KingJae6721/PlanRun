package com.yj.planrun;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {   View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button btn_run = (Button) view.findViewById(R.id.btn_run);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}