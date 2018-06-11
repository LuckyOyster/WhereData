package com.angela.wheredata.Network.Protocol.IP;

import android.util.Log;


import com.angela.wheredata.Network.Protocol.TCP.TCPDatagram;
import com.angela.wheredata.Network.Protocol.UDP.UDPDatagram;
import com.angela.wheredata.Others.ByteOperations;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by frank on 2014-03-26.
 */

public class IPDatagram {
    public final static String TAG = IPDatagram.class.getSimpleName();

    public static final int TCP = 6;
    public static final int UDP = 17;

    IPHeader header;
    IPPayLoad data;

    public IPDatagram(IPHeader header, IPPayLoad data) {
        this.header = header;
        this.data = data;
        int totalLength = header.headerLength() + data.length();
        if (this.header.length() != totalLength) {
            this.header.setLength(totalLength);
            this.header.setCheckSum(new byte[]{0, 0});
            byte[] toComputeCheckSum = this.header.toByteArray();
            this.header.setCheckSum(ByteOperations.computeCheckSum(toComputeCheckSum));
        }
    }


    public static IPDatagram create(ByteBuffer packet) {
        IPHeader header = IPHeader.create(packet.array());
        IPPayLoad payLoad;
        if (header.protocol() == TCP) {
            payLoad = TCPDatagram.create(packet.array(), header.headerLength(), packet.limit(), header.getDstAddress());
        } else if (header.protocol() == UDP) {
            payLoad = UDPDatagram.create(Arrays.copyOfRange(packet.array(), header.headerLength(), packet.limit()));
        } else return null;
        return new IPDatagram(header, payLoad);
    }

    public IPHeader header() {
        return header;
    }

    public IPPayLoad payLoad() {
        return data;
    }

    public byte[] toByteArray() {
        return ByteOperations.concatenate(header.toByteArray(), data.toByteArray());
    }

    public void debugInfo() {
        Log.d(TAG, "DstAddr=" + header.getDstAddress() + " SrcAddr=" + header.getSrcAddress());
    }

    public String debugString()
    {
        StringBuffer sb = new StringBuffer("DstAddr=");
        sb.append(header.getDstAddress());
        sb.append(" SrcAddr=");
        sb.append(header.getSrcAddress());
        sb.append(" ");
        //if (payLoad() instanceof TCPDatagram) {
        //    sb.append(((TCPDatagram)payLoad()).debugString());
        //}
        //if (payLoad() instanceof UDPDatagram) {
        //    sb.append(((UDPDatagram)payLoad()).debugString());
        //}
        return sb.toString();
    }
}