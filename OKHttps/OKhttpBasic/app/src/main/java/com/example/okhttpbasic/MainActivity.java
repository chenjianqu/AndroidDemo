package com.example.okhttpbasic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_start_okhttps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start_okhttps = findViewById(R.id.btn_start_okhttps_activity);

        btn_start_okhttps.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,ActivityOKHttpsTest.class);
            startActivity(intent);
        });
    }
}