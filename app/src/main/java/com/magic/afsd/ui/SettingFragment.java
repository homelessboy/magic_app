package com.magic.afsd.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.*;
import com.magic.afsd.R;
import com.magic.afsd.trans.BTSetting;
import com.magic.afsd.trans.ReadThread;
import com.magic.afsd.trans.SettingHead;
import com.magic.afsd.trans.Trans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


import static com.magic.afsd.trans.SettingHead.*;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class SettingFragment extends PreferenceFragment {
    private Map<String, Preference> preferenceMap = new HashMap<>();
    private BluetoothAdapter mAdapter;
    private BluetoothSocket socket;
    private Context context;
    private int enableNum;

    private String nowAddress;

    private ReadThread readThread;
    private BTSetting btSetting;
    private Trans trans = new Trans();
    private boolean sync = false;

    private String uuid = "00001101-0000-1000-8000-00805F9B34FB";

    public SettingFragment(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        setUpPreference();
        readThread = new ReadThread(mAdapter);
        btSetting = new BTSetting(mHandler, preferenceMap);
        btSetting.setTrans(trans);
        readThread.setTrans(trans);
        trans.setBtSetting(btSetting);
    }

    private void setUpPreference() {
        preferenceMap.put("devices", findPreference("devices"));
        preferenceMap.put("sync", findPreference("sync"));
        preferenceMap.put("circle_num", findPreference("circle_num"));
        preferenceMap.put("surface_num", findPreference("surface_num"));
        preferenceMap.put("operate_single", findPreference("operate_single"));
        preferenceMap.put("operate_double_same", findPreference("operate_double_same"));
        preferenceMap.put("operate_double_dis", findPreference("operate_double_dis"));
        preferenceMap.put("rotation_time", findPreference("rotation_time"));
        preferenceMap.put("mask_round", findPreference("mask_round"));
        preferenceMap.put("color_0", findPreference("color_0"));
        preferenceMap.put("color_1", findPreference("color_1"));
        preferenceMap.put("color_2", findPreference("color_2"));
        preferenceMap.put("color_3", findPreference("color_3"));
        preferenceMap.put("color_4", findPreference("color_4"));
        preferenceMap.put("color_5", findPreference("color_5"));
        preferenceMap.put("standby_index", findPreference("standby_index"));
        preferenceMap.put("standby_time", findPreference("standby_time"));
        for (Preference preference : preferenceMap.values()) {
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
                btSetting.syncAC();
                return false;
            } else {
                for (Preference preferenceItem : preferenceMap.values()) {
                    preferenceItem.setEnabled(false);
                }
                findPreference("devices").setEnabled(true);
                findPreference("sync").setEnabled(true);
                sync = false;
                System.out.println("in false");
                return true;
            }
        }
        if (preference.getKey().equals("devices")) {
            readThread.connection((String) value);
        }
        if (!sync) return true;

        if (preference.getKey().equals("circle_num")) {
            String str = (String) value;
            int num = Integer.parseInt(str);
            btSetting.setCircleNum(num);
        }
        if (preference.getKey().equals("surface_num")) {
            String str = (String) value;
            int num = Integer.parseInt(str);
            btSetting.setSurfaceNum(num);
        }
        if (preference.getKey().equals("operate_single")) {
            boolean boolValue = (boolean) value;
            btSetting.setSingleOperate(boolValue);
        }
        if (preference.getKey().equals("operate_double_same")) {
            boolean boolValue = (boolean) value;
            btSetting.setDoubleSame(boolValue);
        }
        if (preference.getKey().equals("operate_double_dis")) {
            boolean boolValue = (boolean) value;
            btSetting.setDoubleDis(boolValue);
        }
        if (preference.getKey().equals("rotation_time")) {
            String str = (String) value;
            long num = Long.parseLong(str);
            btSetting.setRotationTime(num);
        }
        if (preference.getKey().equals("mask_round")) {
            String str = (String) value;
            int num = Integer.parseInt(str);
            btSetting.setMaskRound(num);
        }
        if (preference.getKey().contains("color_")) {
            System.out.println("colorSetting");
            int data[] = new int[6];
            for (int i = 0; i < 6; i++) {
                ColorPreference colorPreference = (ColorPreference) findPreference("color_" + i);
                data[i] = colorPreference.getmColor();
            }
            String key = preference.getKey();
            String indexStr = key.substring(preference.getKey().indexOf("_") + 1);
            int index = Integer.parseInt(indexStr);
            data[index] = (int) value;
            btSetting.setColor(data);
        }
        if (preference.getKey().equals("standby_time")) {
            System.out.println("standby_time");
            String str = (String) value;
            long num = Long.parseLong(str);
            btSetting.setStandbyTime(num);
        }

        return false;
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String headStr = bundle.getByte("head") + "";
            ListPreference listPreference;
            SwitchPreference switchPreference;
            switch (msg.what) {
                case CONNECTION_AC:
                    findPreference("sync").setEnabled(true);
                    break;
                case CIRCLE_STEP_AC:
                    listPreference = (ListPreference) findPreference("circle_num");
                    if (!listPreference.isEnabled()) {
                        listPreference.setEnabled(true);
                        enableNum++;
                    }
                    listPreference.setValue(bundle.getByte(headStr) + "");
                    sync = true;
                    break;
                case SURFACE_STEP_AC:
                    listPreference = (ListPreference) findPreference("surface_num");
                    if (!listPreference.isEnabled()) {
                        listPreference.setEnabled(true);
                        enableNum++;
                    }
                    listPreference.setValue(bundle.getByte(headStr) + "");
                    break;
                case SINGLE_OPERATE_AC:
                    switchPreference = (SwitchPreference) findPreference("operate_single");
                    if (!switchPreference.isEnabled()) {
                        switchPreference.setEnabled(true);
                        enableNum++;
                    }
                    switchPreference.setChecked(bundle.getBoolean(headStr));
                    break;
                case DOUBLE_SAME_AC:
                    switchPreference = (SwitchPreference) findPreference("operate_double_same");
                    if (!switchPreference.isEnabled()) {
                        switchPreference.setEnabled(true);
                        enableNum++;
                    }
                    switchPreference.setChecked(bundle.getBoolean(headStr));
                    break;
                case DOUBLE_DIS_AC:
                    switchPreference = (SwitchPreference) findPreference("operate_double_dis");
                    if (!switchPreference.isEnabled()) {
                        switchPreference.setEnabled(true);
                        enableNum++;
                    }
                    switchPreference.setChecked(bundle.getBoolean(headStr));
                    break;
                case TIME_P_AC:
                    listPreference = (ListPreference) findPreference("rotation_time");
                    if (!listPreference.isEnabled()){
                        listPreference.setEnabled(true);
                        enableNum++;
                    }
                    listPreference.setValue(bundle.getLong(headStr) + "");
                    break;
                case MASK_ROUND_AC:
                    listPreference = (ListPreference) findPreference("mask_round");
                    if (!listPreference.isEnabled()) {
                        listPreference.setEnabled(true);
                        enableNum++;
                    }
                    listPreference.setValue(bundle.getByte(headStr) + "");
                    break;
                case COLOR_AC:
                    int[] data = bundle.getIntArray(headStr);
                    if(!findPreference("color_0").isEnabled())
                        enableNum++;
                    for (int i = 0; i < 6; i++) {
                        ColorPreference colorPreference = (ColorPreference) findPreference("color_" + i);
                        colorPreference.setEnabled(true);
                        colorPreference.setmColor(data[i]);
                    }
                    break;
                case STANDBY_TIME_AC:
                    listPreference = (ListPreference) findPreference("standby_time");
                    if (!listPreference.isEnabled()) {
                        listPreference.setEnabled(true);
                        enableNum++;
                    }
                    System.out.println(bundle.getLong(headStr));
                    listPreference.setValue(bundle.getLong(headStr) + "");
                    break;
                default:
                    break;
            }
            switchPreference= (SwitchPreference) findPreference("sync");
            if(!switchPreference.isChecked()&&enableNum==9){
                switchPreference.setChecked(true);
            }
        }
    };
}
