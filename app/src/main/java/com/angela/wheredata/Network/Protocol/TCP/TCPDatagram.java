package com.angela.wheredata.Network.Protocol.TCP;

import android.util.Log;


import com.angela.wheredata.Network.Protocol.IP.IPPayLoad;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by frank on 2014-03-26.
 */
public class TCPDatagram extends IPPayLoad {
    private static final String TAG = TCPDatagram.class.getSimpleName();
    private static final boolean DEBUG = false;

    public TCPDatagram(TCPHeader header, byte[] data, InetAddress dst) {
        this.header = header;
        this.data = data;
        //debugInfo();
    }

    public TCPDatagram(TCPHeader header, byte[] data, int start, int end, InetAddress dst) {
        this.header = header;
        this.data = Arrays.copyOfRange(data, start, end);
        //debugInfo();
    }

    public static TCPDatagram create(byte[] data, InetAddress dst) {
        TCPHeader header = new TCPHeader(data);
        return new TCPDatagram(header, Arrays.copyOfRange(data, header.offset(), data.length), dst);
    }

    public static TCPDatagram create(byte[] data, int offset, int len, InetAddress dst) {
        TCPHeader header = new TCPHeader(data, offset);
        //TODO    这里面的data是报文内容！！！
        /***************/
        byte[] textData=Arrays.copyOfRange(data, header.offset() + offset, len);

        Log.d("GoGoGoGoGoGo",new String(textData));

        /***************/
        return new TCPDatagram(header, Arrays.copyOfRange(data, header.offset() + offset, len), dst);
    }

    public void debugInfo() {
        //if(header.getDstPort() == 80 || header.getSrcPort() == 80)
        byte flag = ((TCPHeader)header).getFlag();
        StringBuffer flags = new StringBuffer();
        if ((flag & TCPHeader.SYN) != 0) flags.append("SYN|");
        if ((flag & TCPHeader.FIN) != 0) flags.append("FIN|");
        if ((flag & TCPHeader.ACK) != 0) flags.append("ACK|");
        if ((flag & TCPHeader.PSH) != 0) flags.append("PSH|");
        if ((flag & TCPHeader.RST) != 0) flags.append("RST|");
        Log.d(TAG, "Flags=" + flags.toString() +
                " SrcPort=" + header.getSrcPort() + " DstPort=" + header.getDstPort() + " Seq=" + Long.toString(((TCPHeader) header).getSeq_num() & 0xFFFFFFFFL) +
                " Ack=" + Long.toString(((TCPHeader) header).getAck_num() & 0xFFFFFFFFL) +
                " Data Length=" + dataLength());
    }

    public String debugString()
    {
        StringBuffer sb = new StringBuffer("Flags=");
        byte flag = ((TCPHeader)header).getFlag();
        if ((flag & TCPHeader.SYN) != 0) sb.append("SYN|");
        if ((flag & TCPHeader.FIN) != 0) sb.append("FIN|");
        if ((flag & TCPHeader.ACK) != 0) sb.append("ACK|");
        if ((flag & TCPHeader.PSH) != 0) sb.append("PSH|");
        if ((flag & TCPHeader.RST) != 0) sb.append("RST|");
        sb.append("SrcPort=");
        sb.append(header.getSrcPort());
        sb.append(" DstPort=");
        sb.append(header.getDstPort());
        sb.append(" Seq=");
        sb.append(Long.toString(((TCPHeader) header).getSeq_num() & 0xFFFFFFFFL));
        sb.append(" Ack=");
        sb.append(Long.toString(((TCPHeader) header).getAck_num() & 0xFFFFFFFFL));
        sb.append(" Data Length=");
        sb.append(dataLength());
        return sb.toString();
    }

    @Override
    public int virtualLength() {
        byte flag = ((TCPHeader)header).getFlag();
        if((flag & (TCPHeader.SYN | TCPHeader.FIN)) != 0) return 1;
        else return this.dataLength();
    }
}
