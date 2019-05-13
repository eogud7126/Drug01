package com.example.leedaehyung.drug01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;
    int requestCode=SetAlarm.requestCode;                                                                       //SetAlarm클래스에서 설정한 requestCode 가져옴
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);              //PowerManager 선언
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");      //꺼진 화면을 알람이 울리면 켜지도록 설정
        wakeLock.acquire();
        Log.d("alarm", "gogo");
        PendingIntent pendingIntent;

        Toast toast = Toast.makeText(context, "알람이 울립니다.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 200);                                                  //푸쉬알림으로 울리도록 설정
        toast.show();

        wakeLock.release();

        try {
            intent = new Intent(context, removeActivity.class);                                                     //알람이 울리면 removeActivity클래스가 호출되도록 설정
            pendingIntent = PendingIntent.getActivity(context, requestCode, intent,0);
            Log.d("ServicePending++:", "" + pendingIntent.toString());
            pendingIntent.send();

        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        notification();
    }

    void notification(){                                                                                        //알람 소리가 울리도록 하는 함수 선언
        Intent intent=new Intent();

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);                         //벨소리 가져오기
        Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),android.R.drawable.ic_menu_gallery);  //푸쉬알림에 뜨는 이미지 설정
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context).setSmallIcon(android.R.drawable.ic_menu_gallery).setLargeIcon(bitmap).setContentTitle("알람")
                .setContentText("약 먹으실 시간이에요").setAutoCancel(true).setSound(soundUri).setContentIntent(pendingIntent);  //알람이 울릴때 시간이 지나면 자동으로 꺼지게 설정,푸쉬알람 설정,텍스트 설정
        notificationBuilder.setColor(Color.RED);    //빨간색 아이콘으로 뜨도록 설정
        long[] vibrate ={0,1000,200,300};           //진동 주기 설정
        notificationBuilder.setVibrate(vibrate);    //진동 설정
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);    //NotificationManager 선언
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default","기본채널",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(0,notificationBuilder.build());
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
