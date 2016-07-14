package com.magic.afsd.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.magic.afsd.R;


/**
 * @author: afsd
 * @version: ${VERSION}
 */
public class ColorPreference extends DialogPreference {
    private Color color=new Color();
    private Context mContext;
    private Drawable icon;
    private int iconId;
    private int mColor = 0xff000000;
    private int r = 0;
    private int g = 0;
    private int b = 0;
    private int inputColor=-1;
    EditText editTextR;
    EditText editTextG;
    EditText editTextB;

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        iconId = attrs.getAttributeResourceValue(0, -1);
        if (iconId > 0) {
            icon = context.getDrawable(iconId);
            setIcon(icon);
            if (attrs.getAttributeName(1).equals("color")) {
                setmColor(attrs.getAttributeIntValue(1, 0));
            }
        }
    }

    public void setmColor(int value) {
        if (mColor != value) {
            mColor = value;
//            persistInt(mColor);
            icon.setColorFilter(mColor, PorterDuff.Mode.SRC_OVER);
            notifyChanged();
        }
    }

    public int getmColor(){
        return mColor;
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public ColorPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        final Context context = builder.getContext();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.color_preference_layout, null);
        editTextR = (EditText) view.findViewById(R.id.color_r);
        editTextG = (EditText) view.findViewById(R.id.color_g);
        editTextB = (EditText) view.findViewById(R.id.color_b);

        editTextR.setText((mColor >> 16 & 0xFF)+ "");
        editTextG.setText((mColor >> 8 & 0xFF)+ "");
        editTextB.setText((mColor & 0xFF)+ "");
        builder.setView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult){
            r = Integer.valueOf(String.valueOf(editTextR.getText()));
            g = Integer.valueOf(String.valueOf(editTextG.getText()));
            b = Integer.valueOf(String.valueOf(editTextB.getText()));
            if (r > 255 || g > 255 || b > 255) {
                Toast toast = new Toast(mContext);
                toast.setText("输入错误");
                toast.show();
                return;
            }
            int value=0xFF000000;
            value=r<<16|value;
            value=g<<8|value;
            value=b|value;
            if(callChangeListener(value))
                setmColor(value);
        }

    }
}
