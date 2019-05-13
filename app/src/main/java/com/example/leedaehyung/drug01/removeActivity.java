package com.example.leedaehyung.drug01;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.leedaehyung.drug01.SetAlarm;

import java.util.Calendar;


public class removeActivity extends AppCompatActivity {
    private Intent intent;
    private PendingIntent ServicePending;
    private AlarmManager alarmManager;
    private SetAlarm setAlarm;
    Calendar Time;
    int requestCode=setAlarm.requestCode;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);                      //알람매니저 설정

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.song);       //이 액티비티가 뜨면 음악이 울리도록 설정한다.
        mediaPlayer.start();

        Button.OnClickListener bClickListener = new View.OnClickListener() {                //알람 해제 버튼을 누르면 음악이 멈추고 전의 화면으로 이동
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.removeAlarm:
                        mediaPlayer.stop();                                             //음악 재생 중지
                        finish();
                        break;
                }
            }
        };
        findViewById(R.id.removeAlarm).setOnClickListener(bClickListener);
    }


}