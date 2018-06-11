/*
 * Modify the SocketForwarder of SandroproxyLib
 * Copyright (C) 2014  Yihang Song

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.angela.wheredata.Network.Forwarder;

import android.util.Log;


import com.angela.wheredata.Network.FakeVPN.MyVpnService;
import com.angela.wheredata.Others.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalServerForwarder extends Thread {
    private static String TAG = LocalServerForwarder.class.getSimpleName();
    private static boolean DEBUG = true;
    private static int LIMIT = 1368;
    private boolean outgoing = false;
    private MyVpnService vpnService;
    private Socket inSocket;
    private InputStream in;
    private OutputStream out;
    private LinkedBlockingQueue<ByteArray> toFilter = new LinkedBlockingQueue<>();
    private SocketChannel inChannel, outChannel;

    public LocalServerForwarder(Socket inSocket, Socket outSocket, boolean isOutgoing, MyVpnService vpnService) {
        this.inSocket = inSocket;
        try {
            this.in = inSocket.getInputStream();
            this.out = outSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.outgoing = isOutgoing;
        this.vpnService = vpnService;
        setDaemon(true);
    }

    public LocalServerForwarder(SocketChannel in, SocketChannel out, boolean isOutgoing, MyVpnService vpnService) {
        this.inChannel = in;
        this.outChannel = out;
        this.outgoing = isOutgoing;
        this.vpnService = vpnService;
        setDaemon(true);
    }

    public static void connect(Socket clientSocket, Socket serverSocket, MyVpnService vpnService) throws Exception {
        if (clientSocket != null && serverSocket != null && clientSocket.isConnected() && serverSocket.isConnected()) {
            clientSocket.setSoTimeout(0);
            serverSocket.setSoTimeout(0);
            LocalServerForwarder clientServer = new LocalServerForwarder(clientSocket, serverSocket, true, vpnService);
            LocalServerForwarder serverClient = new LocalServerForwarder(serverSocket, clientSocket, false, vpnService);
            clientServer.start();
            serverClient.start();

            if (DEBUG)
                Log.d(TAG, "Start forwarding for " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "->" + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort());
            while (clientServer.isAlive())
                clientServer.join();
            while (serverClient.isAlive())
                serverClient.join();
            if (DEBUG)
                Log.d(TAG, "Stop forwarding " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "->" + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort());
            clientSocket.close();
            serverSocket.close();
        } else {
            if (DEBUG) Log.d(TAG, "skipping socket forwarding because of invalid sockets");
            if (clientSocket != null && clientSocket.isConnected()) {
                clientSocket.close();
            }
            if (serverSocket != null && serverSocket.isConnected()) {
                serverSocket.close();
            }
        }
    }

    public void run() {
        try {
            byte[] buff = new byte[LIMIT];
            int got;
            while ((got = in.read(buff)) > -1) {
                out.write(buff, 0, got);
                out.flush();
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
            Log.d(TAG, "outgoing : " + outgoing);
        }
    }
}
