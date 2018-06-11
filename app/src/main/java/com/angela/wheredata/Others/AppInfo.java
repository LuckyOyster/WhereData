package com.angela.wheredata.Others;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppInfo implements Serializable{
    String appName;
    String appPackageName;
    Drawable appIcon;

    public AppInfo(String appName,String appPackageName,Drawable appIcon){
        this.appIcon=appIcon;
        this.appName=appName;
        this.appPackageName=appPackageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public String getAppName() {
        return appName;
    }
}
