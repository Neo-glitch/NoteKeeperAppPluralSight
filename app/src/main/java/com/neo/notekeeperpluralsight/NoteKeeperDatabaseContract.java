package com.neo.notekeeperpluralsight;

import android.provider.BaseColumns;

/**
 * Created by Neo.
 */

public final class NoteKeeperDatabaseContract {
    private NoteKeeperDatabaseContract() {} // make non-creatable

    public static final class CourseInfoEntry {
        public static final String TABLE_NAME = "course_info";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        // create index course_info_index1 on course_info (course_title);
        public static final String INDEX1 = TABLE_NAME + "_index1";                                   // index name
        public static final String SQL_CREATE_INDEX1 =
                "create index " + INDEX1 + " on " + TABLE_NAME +
                        "(" + COLUMN_COURSE_TITLE + ");";


        public static final String  getQName(String columnName){
            // method builds qualified name for the join statement
            return TABLE_NAME + "." + columnName;
        }

        // CREATE TABLE course_info (course_id, course_title)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_COURSE_TITLE + " TEXT NOT NULL)";
    }

    public static final class NoteInfoEntry {
        public static final String TABLE_NAME = "note_info";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String INDEX1 = TABLE_NAME + "_index1";                                   // index name
        public static final String SQL_CREATE_INDEX1 =
                "create index " + INDEX1 + " on " + TABLE_NAME +
                        "(" + COLUMN_NOTE_TITLE + ");";

        public static final String getQName(String columnNane){
            return TABLE_NAME + "." + columnNane;
        }

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_NOTE_TEXT + " TEXT, " +
                        COLUMN_COURSE_ID + " TEXT NOT NULL)";
    }
}












