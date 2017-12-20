package com.example.rajan.coinprice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.example.rajan.coinprice.utilities.JobSchedulerUtils;
import com.example.rajan.coinprice.utilities.PreferenceUtilities;

/**
 * Created by rajan on 20/12/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceUtilities.KEY_REFRESH_INTERVAL)) {
            Log.d(TAG, "onSharedPreferenceChanged: Refresh interval is" + PreferenceUtilities.getRefreshInterval(getContext()));
            JobSchedulerUtils.scheduleNetworkCallCustomInterval(getContext());
        }

    }
}
