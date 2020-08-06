package com.neo.notekeeperpluralsight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Broadcast Receiver to be used with the AlarmManager.. receiver will call the notify() to show notification
 */
public class NoteReminderReceiver extends BroadcastReceiver {
    public static final String EXTRA_NOTE_TITLE = "com.neo.notekeeperpluralsight.extra.NOTE_TITLE";
    public static final String EXTRA_NOTE_TEXT = "com.neo.notekeeperpluralsight.extra.NOTE_TEXT";
    public static final String EXTRA_NOTE_ID = "com.neo.notekeeperpluralsight.extra.NOTE_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        String noteTitle = intent.getStringExtra(EXTRA_NOTE_TITLE);
        String noteText = intent.getStringExtra(EXTRA_NOTE_TEXT);
        int noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0);

        NoteReminderNotification.notify(context, noteTitle, noteText, noteId);

    }
}
