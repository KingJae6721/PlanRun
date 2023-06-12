package com.yj.planrun;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CommunityFragment extends Fragment {
    private LinearLayout slidingPanel, slidingBackground;
    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        RelativeLayout club_btn = view.findViewById(R.id.club_btn);
        ImageView add_post = view.findViewById(R.id.add_post);
        Toolbar community_toolbar = view.findViewById(R.id.community_toolbar);
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ImageView search = view.findViewById(R.id.search);
        slidingPanel = view.findViewById(R.id.slidingPanel);
        slidingBackground = view.findViewById(R.id.slidingBackground);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingBackground.getVisibility() == View.VISIBLE) {
                    hideSlidingPanel();
                    slidingBackground.setVisibility(View.GONE);
                } else {
                    showSlidingPanel();
                    slidingBackground.setVisibility(View.VISIBLE);
                }
            }
        });
        club_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                ClubFragment clubFragment = new ClubFragment();
                transaction.replace(R.id.community_layout, clubFragment);
                community_toolbar.setVisibility(View.GONE);
                transaction.commit();
            }
        });

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                startActivity(intent);
            }
        });

        return view;


    }
    private void showSlidingPanel() {
        slidingPanel.setVisibility(View.VISIBLE);

        Animation slideUpAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        slideUpAnimation.setDuration(500);
        slidingPanel.startAnimation(slideUpAnimation);
    }

    // 슬라이딩 패널을 숨기는 메소드
    private void hideSlidingPanel() {

        Animation slideDownAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f
        );
        slideDownAnimation.setDuration(500);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                slidingPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        slidingPanel.startAnimation(slideDownAnimation);
    }
}