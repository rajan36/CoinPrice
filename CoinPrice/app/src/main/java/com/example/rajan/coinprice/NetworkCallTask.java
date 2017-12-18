package com.example.rajan.coinprice;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.rajan.coinprice.network.MySingleton;
import com.example.rajan.coinprice.utilities.NotificationUtils;
import com.example.rajan.coinprice.utilities.PreferenceUtilities;

import static android.content.ContentValues.TAG;

/**
 * Created by rajan on 11/12/17.
 */

public class NetworkCallTask {
    public static final String ACTION_TRIGGER_API_CALL = "trigger-api-call";
    private static final String KOINEX_API_TICKER = "https://koinex.in/api/ticker";
    private static final String COINMARKETCAP_API_TICKER = "https://api.coinmarketcap.com/v1/ticker/?convert=INR&limit=6";

    public static void executeTask(Context context, String action) {
        if (action.equals(ACTION_TRIGGER_API_CALL)) {
            Log.d(TAG, "executeTask: Task Execution called");
            volleyCall(context);
            NotificationUtils.priceAlert(context);
        }
    }

    private static void volleyCall(Context context) {
        RequestQueue queue = MySingleton.getInstance(context).
                getRequestQueue();
        StringRequest koinexRequest = getKoinexRequest(context);
        StringRequest coinMarketCapRequest = getCoinMarketCapRequest(context);
        queue.add(koinexRequest);
        queue.add(coinMarketCapRequest);

    }

    private static StringRequest getKoinexRequest(final Context context) {
        String url = KOINEX_API_TICKER;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PreferenceUtilities.setKoinexJson(context, response);
                        Log.d(TAG, "onResponse: response is " + response);
                        PreferenceUtilities.increaseSuccessRequestCount(context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error in koinex api");
                PreferenceUtilities.increaseFailureRequestCount(context);
            }
        });
        return stringRequest;
    }

    private static StringRequest getCoinMarketCapRequest(final Context context) {
        String url = COINMARKETCAP_API_TICKER;
        StringRequest gsonRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PreferenceUtilities.setCoinMarketCapJson(context, response);
                        PreferenceUtilities.increaseSuccessRequestCount(context);
                        Log.d(TAG, "onResponse: response is " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error in coinmarketcap");
                PreferenceUtilities.increaseFailureRequestCount(context);
            }
        });
        return gsonRequest;
    }


}
