package com.angela.wheredata.Network.Protocol.UDP;

import android.util.Log;


import com.angela.wheredata.Network.Protocol.IP.IPPayLoad;
import com.angela.wheredata.Others.ByteOperations;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by frank on 2014-03-28.
 */
public class UDPDatagram extends IPPayLoad {
    private final String TAG = "UDPDatagram";

    public UDPDatagram(UDPHeader header, byte[] data) {
        this.header = header;
        this.data = data;
        if (header.getTotal_length() != data.length + header.headerLength()) {
            header.setTotal_length(data.length + header.headerLength());
        }
    }

    public static UDPDatagram create(byte[] data) {
        UDPHeader header = new UDPHeader(data);
        return new UDPDatagram(header, Arrays.copyOfRange(data, 8, header.getTotal_length()));
    }

    public void debugInfo(InetAddress dstAddress) {
        Log.d(TAG, "DstAddr=" + dstAddress.getHostName() +
                " SrcPort=" + header.getSrcPort() + " DstPort=" + header.getDstPort() +
                " Total Length=" + ((UDPHeader) header).getTotal_length() +
                " Data Length=" + this.dataLength() +
                " Data=" + ByteOperations.byteArrayToString(this.data));
    }

    public String debugString() {
        StringBuffer sb = new StringBuffer("SrcPort=");
        sb.append(header.getSrcPort());
        sb.append(" DstPort=");
        sb.append(header.getDstPort());
        sb.append(" Total Length=");
        sb.append(((UDPHeader) header).getTotal_length());
        sb.append(" Data Length=");
        sb.append(this.dataLength());
        //sb.append(" Data=" + ByteOperations.byteArrayToString(this.data));
        return sb.toString();
    }
}
