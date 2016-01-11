package com.coolbreeze.android.applenotes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Devender Goyal on 1/11/2016.
 */
public class NotesProvider extends ContentProvider {

    //AUTHORITY is a globally unique string that identifies the content provider to the android framework
    // Only one app on device can use a particular authority
    private static final String AUTHORITY = "com.example.plainolnotes.notesprovider";

    // Represents the entire dataset: in our app we have only one table, so we have given base path as table name
    private static final String BASE_PATH = "notes";

    // URI is a Uniform Resource Identifier, it is used to identify content provider
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation we can do with this content provider.
    private static final int NOTES = 1; // Operation Desc.: Give me the data.
    private static final int NOTES_ID = 2; // Operation Desc.: Will deal with only a single record.

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    // The code block with the static modifier signifies a class initializer;
    // without the static modifier the code block is an instance initializer.
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
