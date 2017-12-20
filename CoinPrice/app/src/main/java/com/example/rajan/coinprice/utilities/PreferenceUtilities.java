package com.example.rajan.coinprice.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.rajan.coinprice.R;

/**
 * Created by rajan on 12/12/17.
 */

public final class PreferenceUtilities {
    public static final String KEY_KOINEX_JSON = "koinex-ticker";
    public static final String KEY_COINMARKETCAP_JSON = "coinmarketcap-ticker";
    public static final String KEY_SUCCESSFUL_REQUEST_COUNT = "success-request-count";
    public static final String KEY_FAILURE_REQUEST_COUNT = "failure-request-count";
    public static final String KEY_REFRESH_INTERVAL = "list_refresh_interval";
    private static final String DEFAULT_VALUE = "empty";

    synchronized public static void setKoinexJson(Context context, String json) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_KOINEX_JSON, json);
        editor.apply();
    }

    synchronized public static void setCoinMarketCapJson(Context context, String json) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_COINMARKETCAP_JSON, json);
        editor.apply();
    }

    synchronized public static void increaseSuccessRequestCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SUCCESSFUL_REQUEST_COUNT, getSuccessRequestCount(context) + 1);
        editor.apply();
    }

    synchronized public static void increaseFailureRequestCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        int prev = 0;
        if (prefs.contains(KEY_FAILURE_REQUEST_COUNT)) {
            prev = prefs.getInt(KEY_FAILURE_REQUEST_COUNT, 0);
        }
        editor.putInt(KEY_FAILURE_REQUEST_COUNT, prev + 1);
        editor.apply();
    }

    public static boolean isKoinexJsonSet(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return !prefs.getString(KEY_KOINEX_JSON, DEFAULT_VALUE).equals(DEFAULT_VALUE);
    }

    public static boolean isCoinMarketCapJsonSet(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return !prefs.getString(KEY_COINMARKETCAP_JSON, DEFAULT_VALUE).equals(DEFAULT_VALUE);
    }

    public static String getKoinexJson(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_KOINEX_JSON, DEFAULT_VALUE);
    }

    public static String getCoinMarketCapJson(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_COINMARKETCAP_JSON, DEFAULT_VALUE);
    }

    public static int getSuccessRequestCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_SUCCESSFUL_REQUEST_COUNT, 0);
    }

    public static int getFailureRequestCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_FAILURE_REQUEST_COUNT, 0);
    }

    public static String getRefreshInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_REFRESH_INTERVAL, "5");
    }
}