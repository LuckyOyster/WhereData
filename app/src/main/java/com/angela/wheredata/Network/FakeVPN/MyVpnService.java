/*
** Copyright 2015, Mohamed Naufal
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package com.angela.wheredata.Network.FakeVPN;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import com.angela.wheredata.Network.Forwarder.ForwarderPools;
import com.angela.wheredata.Network.LocalServer;
import com.angela.wheredata.Network.Resolver.MyClientResolver;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MyVpnService extends VpnService implements Runnable
{
    private static boolean isRunning = false;

    // thread to maintain in the class
    private Thread mThread;
    private TunWriteThread writeThread;
    private TunReadThread readThread;

    private ParcelFileDescriptor mInterface = null;

    private ForwarderPools forwarderPools;
    private MyClientResolver clientAppResolver;
    private LocalServer localServer;

    // Services interface
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new Thread(this);
        mThread.start();

        Log.d("Service","Started");
        return START_STICKY;
    }

    //a. Configure a builder for the interface.
    private Builder setBuilder(){

        Builder builder = new Builder();
        builder.setSession("数据哪里跑");
        builder.addAddress("10.0.8.10",32);
        builder.addRoute("0.0.0.0", 0);
        builder.addDnsServer("8.8.8.8");
        builder.setMtu(1500);
        return builder;
    }


    private void startUpVPN(){
        mInterface = setBuilder().establish();
        if (mInterface == null) {
            Log.d("Error", "Failed to establish Builder interface");
            return;
        }

        forwarderPools = new ForwarderPools(this);
        clientAppResolver = new MyClientResolver(this);

        localServer = new LocalServer(this);
        localServer.start();

        readThread = new TunReadThread(mInterface.getFileDescriptor(),this);
        readThread.start();
        writeThread = new TunWriteThread(mInterface.getFileDescriptor(),this);
        writeThread.start();
    }

    private void closeVPN(){
        // 关闭VPN服务
        if (mInterface == null) return;
        Log.d("VPN", "Stopping");
        try {
            readThread.interrupt();
            writeThread.interrupt();
            localServer.interrupt();
            mInterface.close();
        } catch (IOException e) {
            Log.e("Error",e.toString());
        }
    }

    private void waitToClose(){
        try {
            while (readThread.isAlive()){
                readThread.join();
            }
            while (writeThread.isAlive()){
                writeThread.join();
            }
            while (localServer.isAlive())
                localServer.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public void run() {
        // 开启线程执行VPN启动，运行，在出现意外情况，和结束以后，执行关闭VPN的指令
        try {
            startUpVPN();
            waitToClose();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            closeVPN();
        }
    }
    public static boolean isRunning()
    {
        return isRunning;
    }

    public void fetchResponse(byte[] response) {
        writeThread.write(response);
    }

    public ForwarderPools getForwarderPools() {
        return forwarderPools;
    }

    public MyClientResolver getClientAppResolver() {
        return clientAppResolver;
    }
}
