package com.example.rajan.coinprice.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rajan.coinprice.NetworkCallTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by rajan on 18/12/17.
 */

public class NetworkCallFirebaseJobService extends JobService {
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = NetworkCallFirebaseJobService.this;
                NetworkCallTask.executeTask(context, NetworkCallTask.ACTION_TRIGGER_API_CALL);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null)
            mBackgroundTask.cancel(true);

        return true;
    }
}
