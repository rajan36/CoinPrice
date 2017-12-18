package com.example.rajan.coinprice.utilities;

import android.content.Context;

import com.example.rajan.coinprice.network.NetworkCallFirebaseJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by rajan on 18/12/17.
 */

public class JobSchedulerUtils {
    private static final int JOB_INTERVAL_MINUTES = 1;
    private static final int JOB_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(JOB_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = JOB_INTERVAL_SECONDS;
    private static final String NETWORK_CALL_TAG = "api-call";

    private static boolean sIntialized;

    synchronized public static void scheduleNetworkCall(final Context context) {
        if (sIntialized)
            return;
        ;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job networkCallJob = dispatcher.newJobBuilder().setService(NetworkCallFirebaseJobService.class).setTag(NETWORK_CALL_TAG).setConstraints(Constraint.ON_ANY_NETWORK).setLifetime(Lifetime.FOREVER).setRecurring(true).setTrigger(Trigger.executionWindow(JOB_INTERVAL_SECONDS, JOB_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS)).setReplaceCurrent(true).build();
        dispatcher.schedule(networkCallJob);
        sIntialized = true;

    }
}
