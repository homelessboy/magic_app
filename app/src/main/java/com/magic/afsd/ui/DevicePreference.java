package com.magic.afsd.ui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.magic.afsd.R;

import java.io.IOException;
import java.util.*;

import static android.app.Activity.RESULT_FIRST_USER;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class DevicePreference extends DialogPreference{
    private Context mContext;
    BluetoothAdapter adapter;
    String deviceAddress;
    ListView listView;

    public DevicePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext=context;
        setSummary("设备未连接");
    }

    public DevicePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public DevicePreference(Context context, AttributeSet attrs) {
        this(context, attrs,android.R.attr.dialogPreferenceStyle);
    }

    public DevicePreference(Context context) {
        this(context,null);
    }

    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        final Context context=builder.getContext();
        adapter=BluetoothAdapter.getDefaultAdapter();
        if(!adapter.isEnabled())
            adapter.enable();

        if(!adapter.isEnabled()) {
            builder.setMessage("蓝牙连接不可用");
            return;
        }

        View view= LayoutInflater.from(getContext()).inflate(R.layout.device_preference,null);
        listView= (ListView) view.findViewById(R.id.device_list);
        final ListAdapter listAdapter=new ArrayAdapter<>(context,android.R.layout.simple_expandable_list_item_1,getListData());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                Iterator it=adapter.getBondedDevices().iterator();
                BluetoothDevice device= (BluetoothDevice) it.next();
                for(int i=0;i<position;i++){
                    device= (BluetoothDevice) it.next();
                }

                setSummary(device.getName());
                deviceAddress=device.getAddress();
                if (callChangeListener(deviceAddress))
                    setDeviceAddress(deviceAddress);
                getDialog().dismiss();

            }
        });

        builder.setView(view);
        builder.setPositiveButton(null,null);
    }

    public List<String> getListData(){
        List<String> list = new ArrayList<>();
        Set<BluetoothDevice> devices=adapter.getBondedDevices();
        Iterator it=devices.iterator();
        while (it.hasNext()){
            BluetoothDevice device= (BluetoothDevice) it.next();
            list.add(device.getName()+"\n"+device.getAddress());
        }
        return list;
    }

    public void setDeviceAddress(String value){
        if(!deviceAddress.equals(value)){
            deviceAddress=value;
            persistString(deviceAddress);
            notifyChanged();
        }

    }
}
