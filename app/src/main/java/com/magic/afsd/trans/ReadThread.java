package com.magic.afsd.trans;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.preference.Preference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class ReadThread extends Thread{
    private BluetoothAdapter adapter;
    private Trans trans;
    private BTSetting btSetting;

    private String connAddress;
    private BluetoothSocket socket;


    private static String uuid = "00001101-0000-1000-8000-00805F9B34FB";

    public ReadThread(BluetoothAdapter adapter){
        this.adapter=adapter;
    }

    public void setTrans(Trans trans){
        this.trans=trans;
    }

    public void setBtSetting(BTSetting btSetting){
        this.btSetting=btSetting;
    }

    public void connection(String address){
        if(trans!=null && connAddress!=null && address!=null && address.equals(connAddress)){
            trans.connection();
            return;
        }
        Iterator it=adapter.getBondedDevices().iterator();
        BluetoothDevice device=(BluetoothDevice) it.next();
        while (it.hasNext()){
            if(device.getAddress().equals(address))
                break;
            device = (BluetoothDevice) it.next();
        }
        if(device.getAddress().equals(address)){
            try{
                socket=device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                if(!socket.isConnected())
                    socket.connect();
                if(socket.isConnected()){
                    connAddress=address;
                    trans.setInputStream(socket.getInputStream());
                    trans.setOutputStream(socket.getOutputStream());
                    this.start();
                    trans.connection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run(){
        while (true){
            trans.read();
//            System.out.println("in read thread");
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
