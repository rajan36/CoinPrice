package com.example.rajan.coinprice.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.rajan.coinprice.NetworkCallTask;

import static android.content.ContentValues.TAG;

/**
 * Created by rajan on 11/12/17.
 */

public class NetworkCallIntentService extends IntentService {

    public NetworkCallIntentService() {
        super("NetworkCallIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        NetworkCallTask.executeTask(this, action);
        Log.d(TAG, "onHandleIntent: task execution called");
    }
}
