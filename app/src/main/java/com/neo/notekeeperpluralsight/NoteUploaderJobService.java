package com.neo.notekeeperpluralsight;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;


/**
 * The jobService class for jobScheduler implementation
 */
public class NoteUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI = "com.neo.notekeeperpluralsight.DATA_URI";      // const key for storing and retrieving stuff from Bundle
    private NoteUploader mNoteUploader;

    public NoteUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {  // n.b: method runs on main thread, so async task is needed
        AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParams) {
                JobParameters jobParameters = backgroundParams[0];     // backgourndParams holds diff details about the job

                String stringDataUri = jobParameters.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mNoteUploader.doUpload(dataUri);     // does the upload sim work on worker this async thread

                if(!mNoteUploader.isCanceled()){     // if not canceled due to some reasons
                    jobFinished(jobParameters, false);          // tells jobScheduler work is done
                }
                return null;
            }
        };

        mNoteUploader = new NoteUploader(this);
        task.execute(params);
        return true;        // lets jobScheduler know work needs to be running until task finishes.
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mNoteUploader.cancel();
        return true;   // true means the work will be rescheduled
    }


}
