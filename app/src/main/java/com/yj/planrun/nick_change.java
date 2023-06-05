package com.yj.planrun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class nick_change extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_change);

        EditText et_changenick = findViewById(R.id.et_changenick);
        Button btn_newnick = findViewById(R.id.btn_newnick);


    }
}