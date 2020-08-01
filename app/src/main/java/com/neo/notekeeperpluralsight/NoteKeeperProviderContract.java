package com.neo.notekeeperpluralsight;


import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Contract class for the noteKeeper Content Provider
 */
public final class NoteKeeperProviderContract {
    private NoteKeeperProviderContract(){}                  // for singleton implementation

    public static final String AUTHORITY = "com.neo.notekeeperpluralsight.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CourseIdColumns{
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    // protected and can't be used outside this class unless superclass
    protected interface CoursesColumns{
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NotesColumns{
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
    }

    // class for course_info table
    public static final class Courses implements CourseIdColumns, CoursesColumns,
            BaseColumns{    // to get the _ID constant col
        public static final String PATH = "courses";
        // content://com.neo.notekeeperpluralsight.provider/courses. used to access CP courses table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }


    public static final class Notes implements CourseIdColumns, NotesColumns, BaseColumns,
            CoursesColumns {   // used only for the notes_expanded content uri for joining
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

        // expanded version of note table that can join on course table using the CourseColumns interface
        public static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }


}
