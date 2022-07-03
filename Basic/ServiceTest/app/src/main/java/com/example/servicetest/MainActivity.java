package com.example.servicetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.btnstart);
        stop = (Button) findViewById(R.id.btnend);

        ///创建启动的Service的Intent，以及Intent的属性
        final Intent intent=new Intent();
        intent.setPackage("com.example.servicetest");
        intent.setAction("com.example.servicetest.TEST_SERVICE1");
        ///为两个按钮设置点击事件
        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startService(intent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                stopService(intent);
            }
        });
    }
}