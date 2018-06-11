package com.angela.wheredata.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.VpnService;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.angela.wheredata.Database.DatabaseDao;
import com.angela.wheredata.Database.DatabaseINIT;
import com.angela.wheredata.Network.FakeVPN.MyVpnService;
import com.angela.wheredata.Others.AppInfo;
import com.angela.wheredata.Others.AppNameListAdapter;
import com.angela.wheredata.R;
import com.siberiadante.titlelayoutlib.TitleBarLayout;
import com.siberiadante.titlelayoutlib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<AppInfo> appInfoArrayList;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private TitleBarLayout titleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleBar=(TitleBarLayout)findViewById(R.id.titleBar) ;
        titleBar.setRightTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
            }
        });

        DatabaseINIT databaseINIT=new DatabaseINIT(this);
        SQLiteDatabase databaseWrite=databaseINIT.getWritableDatabase();
        SQLiteDatabase databaseRead=databaseINIT.getReadableDatabase();
        DatabaseDao.sqLiteDatabaseWriter=databaseWrite;
        DatabaseDao.sqLiteDatabaseReader=databaseRead;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.startVPN);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vpnServiceIntent= VpnService.prepare(MainActivity.this);
                if(vpnServiceIntent!=null){
                    startActivityForResult(vpnServiceIntent,0);
                }else {
                    onActivityResult(0,RESULT_OK,null);
                }
            }
        });

        appInfoArrayList=getAppList();
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter=new AppNameListAdapter(appInfoArrayList);
        recyclerView=(RecyclerView)findViewById(R.id.app_list);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        getPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            Intent serviceIntent=new Intent(MainActivity.this,MyVpnService.class);
            startService(serviceIntent);
        }

    }

    private ArrayList<AppInfo> getAppList(){


        //TODO 新建Callable线程获取程序列表，解决开启Activity白屏


        PackageManager packageManager = getApplication().getPackageManager();
        List<PackageInfo> packgeInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        /* 获取应用程序的名称，不是包名，而是清单文件中的labelname
            String str_name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(str_name);
         */
        for(PackageInfo packgeInfo : packgeInfos){
            String appName = packgeInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packgeInfo.packageName;

            Drawable drawable = packgeInfo.applicationInfo.loadIcon(packageManager);
            AppInfo appInfo = new AppInfo(appName, packageName,drawable);
            if(filterApp(packgeInfo.applicationInfo)) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    private boolean filterApp(ApplicationInfo info) {
        //原来是系统应用，用户手动升级
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
            //用户自己安装的应用程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

    private void getPermissions(){
        String permission[]={
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
        for(String item:permission){
            if(ContextCompat.checkSelfPermission(this,item)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{item},0);
            }
        }
    }

}
