package com.yj.planrun;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.checkerframework.checker.units.qual.C;

public class CommunityFragment extends Fragment {

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        RelativeLayout club_btn = view.findViewById(R.id.club_btn);

        club_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ClubFragment clubFragment = new ClubFragment();
                transaction.replace(R.id.community_layout, clubFragment);
                transaction.commit();
            }
        });

        return view;
    }
}