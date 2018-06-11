package com.angela.wheredata.Others;

import android.app.Application;

/**
 * Created by justinhu on 16-01-15.
 */
public class TotalVpnService extends Application {
    public static boolean doFilter = true;
    public static boolean asynchronous = true;
    public static int tcpForwarderWorkerRead = 0;
    public static int tcpForwarderWorkerWrite = 0;

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}
