package com.neo.notekeeperpluralsight;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.neo.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_COURSE_TITLE = "com.neo.notekeeper.ORIGINAL_NOTE_COURSE_TITLE";
    public static final String ORIGINAL_NOTE_COURSE_TEXT = "com.neo.notekeeper.ORIGINAL_NOTE_COURSE_TEXT";


    // the instance state fields
    public String mOriginalNoteCourseId;
    public String mOriginalNoteTitle;
    public String mOriginalNoteText;
    // tells us if new instance of this ViewModel class is created.
    public boolean mIsNewlyCreated = true;


    /**
     *
     * @param outState
     * writes the courseid, title and text saved in our view model fields to the bundle
     */
    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_COURSE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_COURSE_TEXT, mOriginalNoteText);

    }


    /**
     *
     * @param inState
     * gets the course id, title and text stored in the bundle and give it back to the view holder fields
     */
    public void restoreState(Bundle inState){
        mOriginalNoteCourseId = inState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = inState.getString(ORIGINAL_NOTE_COURSE_TITLE);
        mOriginalNoteText = inState.getString(ORIGINAL_NOTE_COURSE_TEXT);
    }
}
