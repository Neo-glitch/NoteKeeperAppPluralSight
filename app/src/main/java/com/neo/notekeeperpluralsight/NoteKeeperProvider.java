package com.neo.notekeeperpluralsight;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.neo.notekeeperpluralsight.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.neo.notekeeperpluralsight.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.neo.notekeeperpluralsight.NoteKeeperProviderContract.CourseIdColumns;
import com.neo.notekeeperpluralsight.NoteKeeperProviderContract.Courses;
import com.neo.notekeeperpluralsight.NoteKeeperProviderContract.Notes;

public class NoteKeeperProvider extends ContentProvider {
    private static final String TAG = "NoteKeeperProvider";
    public static final String MIME_VENDOR_TYPE = "vnd." + NoteKeeperProviderContract.AUTHORITY + ".";

    //vars
    private SQLiteOpenHelper mDbOpenHelper;             // for connecting to the apps database

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);  // NO_MATCH means matching done without an authority or path returns value NO_MATCH

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    public static final int NOTES_ROW = 3;

    static {
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH, NOTES);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED);
        sUriMatcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH + "/#", NOTES_ROW);          // matching for any row in notes table
    }

    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       // todo : to be implemented later
        return 0;
    }

    @Override
    public String getType(Uri uri) {
       String mimeType = null;
       int uriMatch = sUriMatcher.match(uri);
       switch (uriMatch){
           case COURSES:
               // vnd.android.cursor.dir/vnd.com.neo.notekeeperpluralsight.provider.courses
               mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                       MIME_VENDOR_TYPE + Courses.PATH;
               break;
           case NOTES:
               mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                       MIME_VENDOR_TYPE + Notes.PATH;
               break;
           case NOTES_EXPANDED:
               mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                       MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
               break;
           case NOTES_ROW:
               // later portion is same as note case since same table is queried but here it returns only a single item
               mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                       MIME_VENDOR_TYPE + Notes.PATH;
               break;
       }
       return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1;     // var to store the rowId of newly inserted row
        Uri rowUri = null;   // var for uri of newly inserted row
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case NOTES:
                rowId = db.insert(NoteInfoEntry.TABLE_NAME, null, values);
                // content://com.neo.notekeeperpluralsxight.provider/notes/#
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
                break;
            case COURSES:
                rowId = db.insert(CourseInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
                break;
            case NOTES_EXPANDED:
                // throw exception saying that this is a read-only joined table
                break;
        }

        return rowUri;
    }

    @Override
    public boolean onCreate() {    // where to init to openHelper class
        mDbOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);                 // does matching operation and returns the int value
        switch (uriMatch) {
            case COURSES:
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NOTES:
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NOTES_EXPANDED:
                cursor = notesExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                break;
            case NOTES_ROW:                                 // queries db for any a specific row
                long rowId = ContentUris.parseId(uri);
                String rowSelection = NoteInfoEntry._ID + " = ?";
                String[] rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs,
                        null, null, null);
        }
        return cursor;
    }

    // does work of query note_info and course_info table using join
    private Cursor notesExpandedQuery(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String[] columns = new String[projection.length];
        for (int i = 0; i < projection.length; i++) {
            // does table qualifying of the requested col if it meets the conditions else just returned it like that
            columns[i] = projection[i].equals(BaseColumns._ID) || projection[i].equals(CourseIdColumns.COLUMN_COURSE_ID) ?
                    NoteInfoEntry.getQName(projection[i]) : projection[i];
        }


        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
                CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        return db.query(tablesWithJoin, columns, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
       // todo: to be implemented later
        return 0;
    }
}
