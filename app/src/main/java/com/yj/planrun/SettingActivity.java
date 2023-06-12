package com.yj.planrun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
public class SettingActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private CircleImageView circle_iv;
    private String profilePath;
    private ActivityResultLauncher<Intent> imageCaptureLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private View dl_camera_choice;
    private Button btn_camera, btn_gallery, btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mFirebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("PlanRun");

        Button btn_logout = findViewById(R.id.btn_logout);
        LinearLayout btn_setting = findViewById(R.id.btn_setting);

        circle_iv = findViewById(R.id.circle_iv);
        btn_back=findViewById(R.id.btn_back);
        TextView nicknameTextView = findViewById(R.id.nicknameTextView);
        /*if (mFirebaseAuth != null) {
            mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nickname = snapshot.getValue(String.class);
                        nicknameTextView.setText(nickname);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainFragment", "데이터 로딩 실패: " + error.getMessage());
                }
            });
        }*/
        nicknameTextView.setText(DataLoadingActivity.nickname);
        TextView emailTextView = findViewById(R.id.emailTextView);
        /* if (mFirebaseAuth != null) {
            mDatabaseRef.child("UserAccount").child(mFirebaseAuth.getUid()).child("emailId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String email = snapshot.getValue(String.class);
                        emailTextView.setText(email);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainFragment", "데이터 로딩 실패: " + error.getMessage());
                }
            });
        }*/
        emailTextView.setText(DataLoadingActivity.email);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, AppSettingActivity.class);
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

                //구글 로그아웃
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(SettingActivity.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
                googleSignInClient.signOut();

                Intent intent = new Intent(SettingActivity.this, StartActivity.class);
                startActivity(intent);
                SharedPreferences sharedPreferences = SettingActivity.this.getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                finish();
                //onDestroy();
            }
        });

        //탈퇴처리
        //mFirebaseAuth.getCurrentUser().delete();

        imageCaptureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();

                profilePath = data.getStringExtra("profilePath");
                Log.e("로그 : ", "profilePath" + profilePath);
                Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                circle_iv.setImageBitmap(bmp);
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            Glide.with(SettingActivity.this).load(uri).override(80, 80).into(circle_iv);
                        }
                    }
                }
        );

        circle_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl_camera_choice = View.inflate(view.getContext(), R.layout.activity_camera_choice, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setView(dl_camera_choice);
                btn_camera = dl_camera_choice.findViewById(R.id.btn_camera);
                btn_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SettingActivity.this, CameraActivity.class);
                        imageCaptureLauncher.launch(intent);
                    }
                });

                btn_gallery = dl_camera_choice.findViewById(R.id.btn_gallery);

                btn_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        galleryLauncher.launch(intent);
                    }
                });
                dlg.show();
            }
        });

        /*
        circle_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraChoiceDialog();
            }
        });
        */
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });
    }//OnCreate
    /*
    private void showCameraChoiceDialog() {
        View dl_camera_choice = View.inflate(this, R.layout.activity_camera_choice, null);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setView(dl_camera_choice);

        Button btn_camera = dl_camera_choice.findViewById(R.id.btn_camera);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, CameraActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        Button btn_gallery = dl_camera_choice.findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });

        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                profilePath = data.getStringExtra("profilePath");
                Log.e("로그 : ", "profilePath" + profilePath);
                Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                circle_iv.setImageBitmap(bmp);
            } else if (requestCode == 2) {
                Uri uri = data.getData();
                Glide.with(this).load(uri).override(80, 80).into(circle_iv);
            }
        }

    }
    */
}