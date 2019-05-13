package com.example.leedaehyung.drug01;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.leedaehyung.drug01.MyDrug;
import com.example.leedaehyung.drug01.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetAlarm extends AppCompatActivity {
    Calendar Time;
    private Intent intent;
    private PendingIntent ServicePending;
    private AlarmManager alarmManager;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년MM월dd일mm분ss초");
    static int requestCode;
    static String drugName;
    static String date;
    static String RC;
    public static Context mContext;

    DatePickerDialog.OnDateSetListener eDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Time.set(Calendar.YEAR, year);                                                                          //년도 설정
            Time.set(Calendar.MONTH, month);                                                                          //월 설정
            Time.set(Calendar.DAY_OF_MONTH, dayOfMonth);                                                              //일 설정
        }
    };

    private TimePickerDialog.OnTimeSetListener sTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Time.set(Calendar.HOUR_OF_DAY, hourOfDay);                                           //시간 설정
            Time.set(Calendar.MINUTE, minute);                                                   //분 설정
            Time.set(Calendar.SECOND, 0);                                                       //초를 0으로 설정
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        mContext=this;
        Button confirm = findViewById(R.id.confirm);
        Button clock = findViewById(R.id.clock);
        Button cancel = findViewById(R.id.cancel);
        final EditText alarmId=findViewById(R.id.alarmId);
        final EditText dName = (EditText)findViewById(R.id.drugName);
        final CheckBox repeatAlarm=(CheckBox)findViewById(R.id.repeatAlarm);
        Time = Calendar.getInstance();

        Button.OnClickListener bClickListener = new View.OnClickListener() {                                //버튼 클릭시 리스너
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.confirm:                                                                      //확인버튼 눌렀을 때 처리
                        if(repeatAlarm.isChecked()){                                                        //만약 반복 체크박스에 체크가 되있으면.
                            requestCode=Integer.parseInt(alarmId.getText().toString());                      //설정한 고유 알람 아이디를 Int값으로 변환하여 저장
                            RC=alarmId.getText().toString();                                                 //디비에 들어갈 고유 알람값을 String으로 변환하여 저장
                            drugName=dName.getText().toString();                                             //약 이름을 edittext에서 값을 받아와 String으로 저장
                            setRepeatAlarm();                                                                //setRepeatAlarm 함수 호출
                        }
                        else {                                                                               //체크박스에 체크 안되있으면,
                            requestCode=Integer.parseInt(alarmId.getText().toString());
                            RC=alarmId.getText().toString();
                            drugName=dName.getText().toString();
                            setAlarm();                                                                      //setAlarm 함수 호출
                        }
                        break;
                    case R.id.cancel:                                                                       //취소버튼 눌렀을 때
                        finish();
                        break;
                    case R.id.clock:                                                                         //시계 모양 눌렀을때
                        new TimePickerDialog(SetAlarm.this, sTimeSetListener, Time.get(Calendar.HOUR_OF_DAY), Time.get(Calendar.MINUTE), false).show();     //Calendar에서 TImePickerDialog불러와 시간 설정
                        new DatePickerDialog(SetAlarm.this, eDateSetListener, Time.get(Calendar.YEAR), Time.get(Calendar.MONTH), Time.get(Calendar.DAY_OF_MONTH)).show();//Calendar에서 DatePickerDialog불러와 날짜 설정
                        break;
                }

            }
        };
        findViewById(R.id.confirm).setOnClickListener(bClickListener);
        findViewById(R.id.clock).setOnClickListener(bClickListener);
        findViewById(R.id.cancel).setOnClickListener(bClickListener);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);                                   //AlarmManager 선언


    }


    void setAlarm() {                                                                                      //setAlarm: 알람리시버에 PendingIntent 보냄
        intent = new Intent("AlarmReceiver");                                                       //PendingIntent에는 알람고유값(requestCode) 인텐트 값이 들어간다.
        ServicePending = PendingIntent.getBroadcast(SetAlarm.this, requestCode, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Time.getTimeInMillis(), ServicePending);                 //정해진 시간에 알람설정
        intent.putExtra("drugName",drugName);                                                       //DB에 데이터를 저장하기 위해 인텐트에 drugName, date,requestCode,repeat여부 를 저장하여 보낸다
        intent.putExtra("date",Time.getTime().toString().substring(11,16));
        intent.putExtra("requestCode",RC);
        intent.putExtra("repeat","반복안함");
        setResult(RESULT_OK,intent);
        finish();
    }


    void removeAlarm(int requestCode) {                                                                 //DB에서 데이터를 삭제할때 데이터만 삭제하는것이 아니라, 등록한 알람이 울리지 않도록 다시 알람리시버에 인텐트를 보냄
        intent = new Intent("AlarmReceiver");
        ServicePending = PendingIntent.getBroadcast(SetAlarm.this, requestCode, intent, 0);
        Toast.makeText(getBaseContext(), "알람해제" + Time.getTime(), Toast.LENGTH_SHORT).show();
        alarmManager.cancel(ServicePending);

    }

    void setRepeatAlarm() {
        intent = new Intent("AlarmReceiver");
        ServicePending = PendingIntent.getBroadcast(SetAlarm.this, requestCode, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Time.getTimeInMillis(), 24*60*60*1000, ServicePending);//알람이 24*60*60*1000 Mills뒤(24시간뒤)에 울리도록 설정..
        Toast.makeText(getBaseContext(), Time.getTime()+"\n매일 이 시간 알람 설정" , Toast.LENGTH_SHORT).show();
        intent=new Intent(getApplicationContext(),MyDrug.class);
        intent.putExtra("drugName",drugName);
        intent.putExtra("date",Time.getTime().toString().substring(11,16));
        intent.putExtra("requestCode",RC);
        intent.putExtra("repeat","매일 알람");
        setResult(RESULT_OK,intent);
        finish();

    }

}