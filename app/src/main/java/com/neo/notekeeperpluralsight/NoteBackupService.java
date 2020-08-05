package com.neo.notekeeperpluralsight;

import android.app.IntentService;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NoteBackupService extends IntentService {
    public static final String EXTRA_COURSE_ID = "com.neo.notekeeperpluralsight.extra.COURSE_ID";     // key for the value passed to the intent b4 starting service

    public NoteBackupService() {
        super("NoteBackupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {                  // where the work is done
        if (intent != null) {
            String backupCourseId = intent.getStringExtra(EXTRA_COURSE_ID);
            NoteBackup.doBackup(this, backupCourseId);
        }
    }


}
