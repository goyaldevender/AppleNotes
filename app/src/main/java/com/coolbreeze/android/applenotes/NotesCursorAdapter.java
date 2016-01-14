package com.coolbreeze.android.applenotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter {

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // This method return a view, and that view will be created based on the layout defined
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Here we will receive a cursor object and it will be already point to the particular
        // row of the database which is supposed to be displayed.

        String noteText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));

        int pos = noteText.indexOf(10); // 10 is the ASCII value of line feed character ("/n")
        if (pos != -1) {
            noteText = noteText.substring(0, pos) + " ...";
        }

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(noteText);

    }
}
