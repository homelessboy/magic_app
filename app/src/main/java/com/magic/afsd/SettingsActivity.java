package com.magic.afsd;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.magic.afsd.ui.ColorPreference;

import java.util.ArrayList;
import java.util.List;

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
    List<Preference> preferenceList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        preferenceList.add(findPreference("connection"));
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
        }
    }

    private static void bindPreferenceChange(Preference preference) {
        preference.setOnPreferenceChangeListener(preferenceChangeListener);
        System.out.println(preference.getKey());
        if (preference instanceof ListPreference)
            preferenceChangeListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
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
            return true;
        }
    };
}
