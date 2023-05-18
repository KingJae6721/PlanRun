package com.yj.planrun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MypageFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btn_logout = (Button) view.findViewById(R.id.btn_logout);
        Button btn_setting = (Button) view.findViewById(R.id.btn_setting);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AppSettingActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        return null;
                    }
                });
                //로그아웃하기
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                onDestroy();
            }
        });
        //탈퇴처리
        //mFirebaseAuth.getCurrentUser().delete();
        return view;
    }

}

