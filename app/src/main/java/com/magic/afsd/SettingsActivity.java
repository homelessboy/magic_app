package com.magic.afsd;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.preference.*;
import com.magic.afsd.ui.ColorPreference;
import com.magic.afsd.ui.SettingFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    BluetoothAdapter mAdapter;
    SettingFragment settingFragment;
    List<BluetoothDevice> deviceList = new ArrayList<>();

    boolean haveRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingFragment = new SettingFragment(getApplicationContext());
        getFragmentManager().beginTransaction().replace(android.R.id.content, settingFragment).commit();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!haveRegister) {
//            haveRegister = true;
//            IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            IntentFilter filterEnd = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//            registerReceiver(deviceReceiver,filterFound);
//            registerReceiver(deviceReceiver,filterEnd);
//        }
//    }

//    private class DeviceReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(action)){
//                BluetoothDevice btd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if(btd.getBondState()!=BluetoothDevice.BOND_BONDED){
//                    deviceList.add()
//                }
//            }
//
//        }
//    }

}
