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
    private static final int DEFAULT_JOB_INTERVAL_MINUTES = 1;
    private static final int DEFAULT_JOB_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(DEFAULT_JOB_INTERVAL_MINUTES));
    private static final int DEFAULT_SYNC_FLEXTIME_SECONDS = DEFAULT_JOB_INTERVAL_SECONDS;
    private static final String NETWORK_CALL_TAG = "api-call";

    private static boolean sIntialized;

    synchronized public static void scheduleNetworkCall(final Context context) {
        if (sIntialized)
            return;
        ;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job networkCallJob = dispatcher.newJobBuilder().setService(NetworkCallFirebaseJobService.class).setTag(NETWORK_CALL_TAG).setConstraints(Constraint.ON_ANY_NETWORK).setLifetime(Lifetime.FOREVER).setRecurring(true).setTrigger(Trigger.executionWindow(DEFAULT_JOB_INTERVAL_SECONDS, DEFAULT_JOB_INTERVAL_SECONDS + DEFAULT_SYNC_FLEXTIME_SECONDS)).setReplaceCurrent(true).build();
        dispatcher.schedule(networkCallJob);
        sIntialized = true;
    }

    synchronized public static void scheduleNetworkCallCustomInterval(final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
//        dispatcher.cancelAll();
        int jobIntervalMinutes = Integer.parseInt(PreferenceUtilities.getRefreshInterval(context));
        int jobIntervalSeconds = (int) (TimeUnit.MINUTES.toSeconds(jobIntervalMinutes));
        int syncFlextimeSeconds = jobIntervalSeconds;
        Job networkCallJob = dispatcher.newJobBuilder().setService(NetworkCallFirebaseJobService.class).setTag(NETWORK_CALL_TAG).setConstraints(Constraint.ON_ANY_NETWORK).setLifetime(Lifetime.FOREVER).setRecurring(true).setTrigger(Trigger.executionWindow(jobIntervalSeconds, jobIntervalSeconds + DEFAULT_SYNC_FLEXTIME_SECONDS)).setReplaceCurrent(true).build();
        dispatcher.schedule(networkCallJob);
//        sIntialized = true;
    }
}
