package com.magic.afsd.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.preference.*;
import com.magic.afsd.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class SettingFragment extends PreferenceFragment {
    List<Preference> preferenceList = new ArrayList<>();
    BluetoothAdapter mAdapter;
    BluetoothSocket socket;
    Context context;

    String nowAddress;

    ReadThread readThread;

    String uuid = "00001101-0000-1000-8000-00805F9B34FB";

    public SettingFragment(Context context){
        super();
        this.context=context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        setUpPreference();
    }

    private void setUpPreference(){
        preferenceList.add(findPreference("devices"));
        preferenceList.add(findPreference("sync"));
        preferenceList.add(findPreference("circle_num"));
        preferenceList.add(findPreference("surface_num"));
        preferenceList.add(findPreference("operate_single"));
        preferenceList.add(findPreference("operate_double_same"));
        preferenceList.add(findPreference("operate_double_dis"));
        preferenceList.add(findPreference("rotation_time"));
        preferenceList.add(findPreference("mask_round"));
        preferenceList.add(findPreference("color_0"));
        preferenceList.add(findPreference("color_1"));
        preferenceList.add(findPreference("color_2"));
        preferenceList.add(findPreference("color_3"));
        preferenceList.add(findPreference("color_4"));
        preferenceList.add(findPreference("color_5"));
        preferenceList.add(findPreference("standby_index"));
        preferenceList.add(findPreference("standby_time"));
        for (Preference preference : preferenceList) {
            bindPreferenceChange(preference);
            preference.setEnabled(false);
        }
        findPreference("devices").setEnabled(true);
    }

    private void bindPreferenceChange(Preference preference) {
        preference.setOnPreferenceChangeListener(new PreferenceChangeListener());
        if (preference instanceof ListPreference)
            preference.getOnPreferenceChangeListener()
                    .onPreferenceChange(preference,
                            PreferenceManager
                                    .getDefaultSharedPreferences(preference.getContext())
                                    .getString(preference.getKey(), ""));
    }

    private class PreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            }
            return checkPreference(preference, value);
        }
    }

    private boolean checkPreference(Preference preference, Object value) {
        if (preference.getKey().equals("sync")) {
            boolean boolValue = (boolean) value;
            if (boolValue) {
                System.out.println("in true");
                return false;
            }else{
                System.out.println("in false");

            }
        }
        if (preference.getKey().equals("devices")) {
            String stringValue = (String) value;
            if (nowAddress != null && nowAddress.equals(stringValue)) {
                try {
                    socket.getOutputStream().write("ok\n".getBytes());
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
            nowAddress = stringValue;
            Iterator it = mAdapter.getBondedDevices().iterator();
            BluetoothDevice device = (BluetoothDevice) it.next();
            while (it.hasNext()) {
                if (device.getAddress().equals(stringValue))
                    break;
                device = (BluetoothDevice) it.next();
            }
            if (device.getAddress().equals(stringValue))
                try {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                    System.out.println(socket.isConnected());
                    if (!socket.isConnected())
                        socket.connect();
                    if (socket.isConnected()) {
                        readThread = new ReadThread(socket);
                        readThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null && socket.isConnected())
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private class ReadThread extends Thread {
        private BluetoothSocket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ReadThread(BluetoothSocket socket) throws IOException {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            try {
                byte[] hello = {(byte) 0xAA, (byte) 0xAA, 1};
                outputStream.write("ok\n".getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            byte[] buffer = new byte[1024];
            String read = "";
            int bytes;
            while (true) {
                try {
                    if ((bytes = inputStream.read(buffer)) > 0) {
                        System.out.println("size:" + bytes);
                        for (int i = 0; i < bytes; i++) {
                            if (buffer[i] == '\n') {
                                System.out.println(read);
                                Message message = new Message();
                                message.what = 1;
                                mHandler.sendMessage(message);
                                ((SwitchPreference)findPreference("sync")).setChecked(false);
                                ((ListPreference)findPreference("circle_num")).setValueIndex(0);
                                read = "";
                            } else
                                read += (char) buffer[i];
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    findPreference("sync").setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };
}
