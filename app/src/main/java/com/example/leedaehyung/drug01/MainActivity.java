package com.example.leedaehyung.drug01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onSearchClick(View v){                          //병원 파싱 페이지 넘어가기
        Intent intent = new Intent(getApplicationContext(),Search.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void onMyDrugClick(View v){                          //알람 리스트 넘어가기
        Intent intent = new Intent(getApplicationContext(),MyDrug.class);
        startActivity(intent);
    }
}
