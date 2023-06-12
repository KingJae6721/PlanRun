package com.yj.planrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ClubFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        View commuLayout = inflater.inflate(R.layout.fragment_community, container, false);
        RelativeLayout post_btn = view.findViewById(R.id.post_btn);
        Toolbar club_toolbar = view.findViewById(R.id.club_toolbar);
        Toolbar community_toolbar = commuLayout.findViewById(R.id.community_toolbar);
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                CommunityFragment communityFragment = new CommunityFragment();
                transaction.replace(R.id.club_layout, communityFragment);
                club_toolbar.setVisibility(View.GONE);
                community_toolbar.setVisibility(View.VISIBLE);
                transaction.commit();
            }
        });
        return view;
    }
}
