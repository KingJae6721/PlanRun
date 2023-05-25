package com.yj.planrun;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "6b36a2c4390081d31d976c7437e97ad6");

    }
}
