package com.angela.wheredata.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.angela.wheredata.Database.DatabaseINIT;
import com.angela.wheredata.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);//线程休眠1.5s
                    WelcomeActivity.this.finish();//结束当前Activity
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));//开启下一个Activity
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
