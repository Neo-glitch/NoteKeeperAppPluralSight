package com.neo.notekeeperpluralsight;

import android.content.Context;
import android.content.Intent;


/**
 * class for sending a broadcast and sends out when user starts editing a note
 */
public class CourseEventBroadcastHelper {
    public static final String ACTION_COURSE_EVENT = "com.neo.notekeeperpluralsight.action.COURSE_EVENT";   // app defined action

    public static final String EXTRA_COURSE_ID = "com.neo.notekeeperpluralsight.extra.COURSE_ID";
    public static final String EXTRA_COURSE_MESSAGE = "com.neo.notekeeperpluralsight.extra.COURSE_MESSAGE";

    public static void sendEventBroadcast(Context context, String courseId, String message){
        Intent intent = new Intent(ACTION_COURSE_EVENT);
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_COURSE_MESSAGE, message);

        context.sendBroadcast(intent);       // sends broadcast to subscribers that can handle this intent

    }
}
