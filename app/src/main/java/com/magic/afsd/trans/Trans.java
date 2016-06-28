package com.magic.afsd.trans;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class Trans {
    private int bufferSize=2048;
    private byte readBuffer[]=new byte[bufferSize];
    private byte getBuffer[]=new byte[bufferSize];
    private int start=0,length=0;
    private int getLength=0;

    private InputStream inputStream;
    private OutputStream outputStream;

    public Trans(InputStream inputStream,OutputStream outputStream){
        this.inputStream=inputStream;
        this.outputStream=outputStream;
    }

    private int getLastIndex(){
        return getIndex(length);
    }

    private int getIndex(int index) {
        return (index+start)%bufferSize;
    }

    private void get(){

    }

    public void read(byte[] buffer,int size){

    }

    public void write(byte head, byte[] data, int length){

    }
}
