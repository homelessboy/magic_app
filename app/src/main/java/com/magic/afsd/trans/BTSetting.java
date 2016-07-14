package com.magic.afsd.trans;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

import java.util.Map;

import static com.magic.afsd.trans.SettingHead.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class BTSetting {
    private Trans trans;
    private Handler handler;
    private Map<String, Preference> preferenceMap;

    public BTSetting(Handler handler, Map<String, Preference> preferenceMap) {
        this.handler = handler;
        this.preferenceMap = preferenceMap;
    }

    public void setTrans(Trans trans) {
        this.trans = trans;
    }

    public void connectionAC() {
        trans.write(CONNECTION_AC, null, 0);
    }

    public void syncAC() {
        trans.write(SYNC_AC, null, 0);
    }

    public void hand(byte head, byte[] data, int length) {
        Message message = new Message();
        message.what = head;
        Bundle bundle = new Bundle();
        String headStr = head + "";
        bundle.putByte("head", head);
        boolean send = true;
        switch (head) {
            case CONNECTION_AC:
                System.out.println("CONNECTION_HEAD_AC");
                bundle.putBoolean(headStr, TRUE);
                break;
            case CIRCLE_STEP_AC:
                System.out.println("CIRCLE_STEP_AC");
                bundle.putByte(headStr, data[0]);
                break;
            case SURFACE_STEP_AC:
                System.out.println("SURFACE_STEP_AC");
                bundle.putByte(headStr, data[0]);
                break;
            case SINGLE_OPERATE_AC:
                System.out.println("SINGLE_OPERATE_AC");
                bundle.putBoolean(headStr, data[0] != 0);
                break;
            case DOUBLE_SAME_AC:
                System.out.println("DOUBLE_SAME_AC");
                bundle.putBoolean(headStr, data[0] != 0);
                break;
            case DOUBLE_DIS_AC:
                System.out.println("DOUBLE_DIS_AC");
                bundle.putBoolean(headStr, data[0] != 0);
                break;
            case TIME_P_AC:
                System.out.println("TIME_P_AC");
                bundle.putLong(headStr, readLong(data));
                break;
            case MASK_ROUND_AC:
                System.out.println("MASK_ROUND_AC");
                bundle.putByte(headStr, data[0]);
                break;
            case COLOR_AC:
                System.out.println("COLOR_AC");
                bundle.putIntArray(headStr, readCRGB(data));
                break;
            case STANDBY_TIME_AC:
                System.out.println("STANDBY_TIME_AC");
                bundle.putLong(headStr,readLong(data));
                break;
            default:
                send = false;
                break;
        }
        message.setData(bundle);
        if (send) handler.sendMessage(message);
        System.out.println(head);
        System.out.println(length);
    }

    private long readLong(byte[] data) {
        long read = 0;
        for (int i = 0; i < 4; i++) {
            int tmp = data[i] & 0xff;
            read = read << 8;
            read = read | tmp;
        }
        return read;
    }

    private int[] readCRGB(byte[] data) {
        int length = data[0] & 0xFF;
        int colors[] = new int[length];
        for (int i = 0; i < length; i++) {
            int r = data[1 + i * 3] & 0xff;
            int g = data[2 + i * 3] & 0xff;
            int b = data[3 + i * 3] & 0xff;

            colors[i] = Color.rgb(r,g,b);
        }
        return colors;
    }

    private byte[] writeRGB(int[] value, int length) {
        byte[] data = new byte[length * 3 + 1];
        data[0] = (byte) (length & 0xff);
        for (int i = 0; i < length; i++) {
            data[i * 3 + 1] = (byte) (value[i] >> 16 & 0xFF);
            data[i * 3 + 2] = (byte) (value[i] >> 8 & 0xFF);
            data[i * 3 + 3] = (byte) (value[i] & 0xFF);
        }
        return data;
    }

    private byte[] writeLong(long value) {
        byte[] data = new byte[4];
        for (int i = 3; i >= 0; i--) {
            int tmp = (int) (value & 0xFF);
            value = value >> 8;
            data[i] = (byte) (tmp & 0xFF);
        }
        return data;
    }


    public void setCircleNum(int circleNum) {
        trans.write(CIRCLE_STEP_AC, circleNum);
    }

    public void setSurfaceNum(int num) {
        trans.write(SURFACE_STEP_AC, num);
    }

    public void setSingleOperate(boolean boolValue) {
        trans.write(SINGLE_OPERATE_AC, boolValue);
    }

    public void setDoubleSame(boolean boolValue) {
        trans.write(DOUBLE_SAME_AC, boolValue);
    }

    public void setDoubleDis(boolean boolValue) {
        trans.write(DOUBLE_DIS_AC, boolValue);
    }

    public void setRotationTime(long num) {
        trans.write(TIME_P_AC, writeLong(num), 4);
    }

    public void setMaskRound(int num) {
        trans.write(MASK_ROUND_AC, num);
    }

    public void setColor(int[] data) {
        trans.write(COLOR_AC, writeRGB(data, 6), 19);
    }

    public void setStandbyTime(long standbyTime) {
        trans.write(STANDBY_TIME_AC,writeLong(standbyTime),4);
    }
}
