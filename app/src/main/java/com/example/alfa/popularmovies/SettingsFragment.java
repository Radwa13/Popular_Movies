package com.example.alfa.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        int prefNum = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < prefNum; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String val = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, val);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Preference preference = findPreference(s);
            String val = sharedPreferences.getString(preference.getKey(), "");
            setPreferenceSummary(preference, val);

    }
    private void setPreferenceSummary(android.support.v7.preference.Preference preference, String val) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int indexOfValue = listPreference.findIndexOfValue(val);
            if (indexOfValue >= 0) {
                listPreference.setSummary(listPreference.getEntries()[indexOfValue]);
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }
}
