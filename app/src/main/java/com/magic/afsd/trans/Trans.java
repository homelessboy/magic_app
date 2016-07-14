package com.magic.afsd.trans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class Trans {
    private int bufferSize = 2048;
    private byte readBuffer[] = new byte[bufferSize];
    private byte getBuffer[] = new byte[bufferSize];
    private int length = 0;
    private int getLength = 0;

    private InputStream inputStream;
    private OutputStream outputStream;
    private BTSetting btSetting;


    public void setInputStream(InputStream inputStream) {
        this.inputStream=inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream=outputStream;
    }

    public void setBtSetting(BTSetting btSetting){
        this.btSetting=btSetting;
    }

    private void get() {
        byte head = readBuffer[0];
        getLength = 0;
        for (int i = 2; i < length; i++) {
            if (i % 2 == 0) {
                getBuffer[getLength++] = readBuffer[i];
            }
        }
        btSetting.hand(head,getBuffer,getLength);
    }

    public void read() {
        int bytes;
        byte buffer[] = new byte[2048];
        try {
            if ((bytes = inputStream.read(buffer)) > 0) {
                System.out.println("size"+bytes);
                read(buffer, bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(byte[] buffer, int bytes) {
        for (int i = 0; i < bytes; i++) {
            if (length%2==1 && buffer[i] == '\n') {
                get();
                length = 0;
            } else {
                readBuffer[length++] = buffer[i];
            }
        }
    }

    public static void main(String[] args) {
        byte data[]={5,2};

    }

    public void write(byte head, byte[] data, int length) {
        byte out[] = new byte[length * 2 + 2];
        out[0] = head;
        out[1] = 0;
        for (int i = 0; i < length; i++) {
            out[i * 2 + 2] = data[i];
            out[i * 2 + 3] = 0;
        }
        out[length * 2 + 1] = '\n';
        try {
            outputStream.write(out);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte head,int value){
        byte data[]=new byte[1];
        data[0]= (byte) (value & 0xFF);
        write(head,data,1);
    }

    public void wrtie(byte head,long value){
        byte data[]=new byte[4];

    }

    public void write(byte head,boolean value){
        byte data[]=new byte[1];
        data[0]= (byte) (value?1:0);
        write(head,data,1);
    }

    public void connection(){
        btSetting.connectionAC();
    }
}
