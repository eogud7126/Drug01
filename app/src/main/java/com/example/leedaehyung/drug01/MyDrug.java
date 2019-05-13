package com.example.leedaehyung.drug01;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MyDrug extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String DATABASE_NAME = "alarm.db";
    private static final int DATABASE_VERSION = 1;
    private DBHelper dbHelper;
    boolean fileReadPermission;
    boolean fileWritePermission;
    private Cursor cursor;
    private int[] to;
    private String[] columns;
    private SimpleCursorAdapter mAdapter;
    private Context context=this;
    static String no;
    static int drc;
    String drugname;
    String date;
    String requestId;
    String repeat;
    TextView textView;
    ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "SQLiteDB";           //DB 경로설정
        File dir = new File(dirPath);                                                                                       //파일 생성

        if(!dir.exists()){                                                                                                 //폴더가 없으면 폴더 생성
            dir.mkdirs();
        }
        String pathDir = dir.getAbsolutePath() +  File.separator + DATABASE_NAME;                                           //DB파일 경로 저장
        String databaseName = pathDir.toString();
        dbHelper=new DBHelper(this,databaseName,null,DATABASE_VERSION);                                     //DBHelper를 통해 DB 생성

        textView=findViewById(R.id.textView2);
        listview=(ListView)findViewById(R.id.listView);

        try{
            cursor=dbHelper.CursorQuery();                                                                                    //CursorQuery 호출하여 DB에 저장되있는 레코드를 쿼리하기
            listViewSetAdapter(cursor);                                                                                          //리스트뷰어댑터에 DB값 저장
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                                  //리스트뷰에 있는 아이템 클릭시
                    Cursor item= (Cursor) mAdapter.getItem(position);
                    no=item.getString(0);                                                                                //_id(primary key)를 변수에 저장
                    drc=Integer.parseInt(item.getString(3));                                                             //그 아이템의 requestCode 가져오기
                }
            });
        }catch(Exception e){
            Toast.makeText(getApplication(),"리스트뷰 불러오기 오류",Toast.LENGTH_LONG).show();
        }


/////////////////////////////////////////////////////권한 획득/////////////////////////////////////////////////
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            fileReadPermission=true;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            fileWritePermission=true;
        }

        if(!fileReadPermission || !fileWritePermission){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
/////////////////////////////////////////////////////권한 획득/////////////////////////////////////////////////



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void ondelBtn(View v){                       //삭제 버튼 눌렀을 때 DB에 있는 데이터를 삭제하고, SetAlarm의 removeAlarm함수를 호출하여 더 이상 알람이 울리지 않도로 설정
        dbHelper.delete(no);
        ((SetAlarm)SetAlarm.mContext).removeAlarm(drc);
        cursor= dbHelper.CursorQuery();                //삭제 후 DB에서 데이터를 가져와 리스트뷰에 데이터 띄우기
        listViewSetAdapter(cursor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200 && grantResults.length>0){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                fileReadPermission=true;
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED)
                fileWritePermission=true;
        }
    }

    public void listViewSetAdapter(Cursor cursor){                          //리스트뷰에 띄우기 위한 어댑터 설정
        startManagingCursor(cursor);
        columns= new String[] {"drugName", "date", "requestCode", "repeat"};
        to =new int[] {R.id.itemName,R.id.date,R.id.requestcode,R.id.repeat};
        mAdapter = new SimpleCursorAdapter(context,R.layout.alarm_item,cursor,columns,to);
        listview.setAdapter(mAdapter);
    }



    public void onButtonClicked(View V){                                   //'+'아이콘 눌렀을 때 알람설정 화면으로 이동
        Intent intent= new Intent(getApplicationContext(),SetAlarm.class);
        startActivityForResult(intent,999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {     //SetAlarm에서 drugName,date,requestCode,repeat 여부를 인텐트로 받아오기 위해 onActivityResult 설정
        if(requestCode==999){
            if(resultCode==RESULT_OK){
                drugname=data.getStringExtra("drugName");
                date=data.getStringExtra("date");
                requestId=data.getStringExtra("requestCode");
                repeat=data.getStringExtra("repeat");
                try {
                    dbHelper.insert(drugname, date, requestId, repeat);                  //가져온 데이터들을 DB에 insert
                    Toast.makeText(getApplication(), "알람 추가",Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    Toast.makeText(getApplication(), "error",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_drug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.search) {
            Intent intent = new Intent(getApplicationContext(),Search.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (id == R.id.home) {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}