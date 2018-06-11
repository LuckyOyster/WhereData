package com.angela.wheredata.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseINIT extends SQLiteOpenHelper {

    public String AppListTable="AppTable";
    public String PhoneData="PrivacyData";
    public String PrivacyDangers="FuckData";
    public String Port_UID="PortSource";

    public DatabaseINIT(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseINIT(Context context){
        this(context, "data.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String str1="create table if not exists AppTable(" +
                "app_id integer primary key autoincrement," +
                "package_name varchar(128) not null," +
                "white_flag boolean not null" +
                ")";
        String str2="create table if not exists PrivacyData(" +
                "id integer primary key autoincrement," +
                "privacy_data varchar(32767) not null," +
                "data_length integer not null," +
                "data_source int not null" +
                ")";
        String str3="create table if not exists FuckData(" +
                "data_id integer primary key autoincrement," +
                "data_item varchar(32767) not null," +
                "port integer not null," +
                "foreign key(port) references PortSource(port)" +
                ")";
        String str4="create table if not exists PortSource(" +
                "port integer not null," +
                "uid integet not null," +
                "app_id int not null," +
                "foreign key(app_id) references AppTable(app_id)" +
                ")";

        db.execSQL(str1);
        db.execSQL(str2);
        db.execSQL(str3);
        db.execSQL(str4);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        super.onOpen(db);
        if(!db.isReadOnly()) { // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
