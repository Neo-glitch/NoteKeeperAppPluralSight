package com.neo.notekeeperpluralsight;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.neo.notekeeperpluralsight.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.neo.notekeeperpluralsight.NoteKeeperDatabaseContract.NoteInfoEntry;

/**
 * Created by Neo.
 */

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        this.mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPosition();
    }

    private void populateColumnPosition() {
        if(mCursor == null){
            return;
        } // gets the column indexes from the mCursor
        mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        // needed by noteActivity to get right info from db
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new ViewHolder(itemView);
    }


    /**
     * handles swapping and closing of cursors
     * @param cursor
     */
    public void changeCursor(Cursor cursor){
        if(mCursor != null){
            mCursor.close();
        } else{
            mCursor = cursor;
            populateColumnPosition();                                                       // done since new cursor may not have same cols ordering.
            notifyDataSetChanged();                                                         // notif recyclerView of dataSet changed
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);                               // moves cursor to pos being requested

        // val for course, title and id of row of current pos
        String course = mCursor.getString(mCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        holder.mTextCourse.setText(course);
        holder.mTextTitle.setText(noteTitle);
        holder.mId = id;
    }
    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mId;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}







