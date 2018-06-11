package com.angela.wheredata.Network;

import android.util.Log;


import com.angela.wheredata.Network.FakeVPN.MyVpnService;
import com.angela.wheredata.Network.Forwarder.LocalServerForwarder;

import org.sandrop.webscarab.model.ConnectionDescriptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
     * Created by frank on 2014-06-03.
 */
public class LocalServer extends Thread {
    private static final boolean DEBUG = true;
    private static final String TAG = LocalServer.class.getSimpleName();
    public static int port = 10240;
    private ServerSocketChannel serverSocketChannel;
    private MyVpnService vpnService;

    public LocalServer(MyVpnService vpnService) {
        if(serverSocketChannel == null || !serverSocketChannel.isOpen())
            try {
                listen();
            } catch (IOException e) {
                if(DEBUG) Log.d(TAG, "Listen error");
                e.printStackTrace();
            }
        this.vpnService = vpnService;
    }


    private void listen() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();////新建NIO通道
        serverSocketChannel.socket().setReuseAddress(true);//使通道为阻塞 
        serverSocketChannel.socket().bind(null);//创建基于NIO通道的socket连接 bind()//绑定IP 及 端口  
        port = serverSocketChannel.socket().getLocalPort();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Log.d(TAG, "Accepting");
                SocketChannel socketChannel = serverSocketChannel.accept();// 得到客户端的SocketChannel
                Socket socket = socketChannel.socket();
                Log.d(TAG, "Receiving : " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                new Thread(new LocalServerHandler(socket)).start();
                Log.d(TAG, "Not blocked");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Stop Listening");
    }

    private class LocalServerHandler implements Runnable {
        private final String TAG = LocalServerHandler.class.getSimpleName();
        String string=new String();
        private Socket client;
        public LocalServerHandler(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            try {
                ConnectionDescriptor descriptor = vpnService.getClientAppResolver().getClientDescriptorByPort(client.getPort());
                SocketChannel targetChannel = SocketChannel.open();
                Socket target = targetChannel.socket();
                vpnService.protect(target);
                targetChannel.connect(new InetSocketAddress(descriptor.getRemoteAddress(), descriptor.getRemotePort()));
                LocalServerForwarder.connect(client, target, vpnService);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}
