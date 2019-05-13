package com.example.leedaehyung.drug01;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lee DaeHyung on 2018-06-09.
 */

public class DBHelper extends SQLiteOpenHelper{

    Context context;
    SQLiteDatabase db;
    Cursor cursor;

    private static final String TABLE_NAME ="alarmTable"; //db table네임 설정

    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context, name, null, version);
        this.context=context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +"( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "drugName , date , requestCode , repeat );"); //sql문
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public void insert(String drugName,String date,String requestCode, String repeat){
        db=getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '" + drugName + "' , '" + date + "' , '" + requestCode +  "' , '" + repeat + "' ) ;");

    }

    public void delete(String _id){
        db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME + " WHERE _id = '"+ _id + "';");
    }

    public Cursor CursorQuery() {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " +  TABLE_NAME, null);
        return cursor;
    }


}