package com.yj.planrun;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class StartActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private int GOOGLE_LOGIN_CODE = 9001;
    ImageButton btn_kakao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        auth = FirebaseAuth.getInstance();


        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        boolean autoLogin = sharedPreferences.getBoolean("checkbox_state", false);

        // 자동 로그인 체크박스가 체크되어 있으면, LoginActivity 대신 MainActivity로 이동
        if (autoLogin) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 파괴
        }

        Button btn_login = (Button) findViewById(R.id.btn_login);
         btn_kakao = (ImageButton) findViewById(R.id.btn_kakao);
         btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent1);
            }
        });

    //카카오로그인
        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
                    @Override
                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                        if(oAuthToken != null) {
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        if(throwable != null) {
                            Toast.makeText(getApplicationContext(), "카카오 로그인 실패", Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                };

                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(StartActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(StartActivity.this, callback);
                }
                else {
                    UserApiClient.getInstance().loginWithKakaoAccount(StartActivity.this, callback);
                }
            }
        });


        //여기서부터 구글로그인
        String id = sharedPreferences.getString("id", null);

        // 이미 로그인한 경우 메인 화면으로 이동 (자동로그인)
        if (id != null) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.google_sign_in_button).setOnClickListener(view -> {
            googleLogin();
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("628881370078-ojp7dn78h59jgpofas8alp39jki7q6na.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(StartActivity.this, gso);



    }


    private void googleLogin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_LOGIN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.e("뭐가 문제야 시!발:","그건저도 잘,...");
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return;
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = auth.getCurrentUser();
                        // 로그인 정보를 저장
                        saveLoginInfo(account);
                        moveMainPage(user);
                    } else {
                        Toast.makeText(StartActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void moveMainPage(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    //자동로그인을 위한 코드
    private void saveLoginInfo(GoogleSignInAccount account) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", account.getId());
        editor.putString("email", account.getEmail());
        editor.commit();
    }
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


}