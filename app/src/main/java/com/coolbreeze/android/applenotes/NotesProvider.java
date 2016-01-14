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
 * <p/>
 * Each of the methods for insert, update and delete will use the database object created earlier.
 */
public class NotesProvider extends ContentProvider {

    public static final String CONTENT_ITEM_TYPE = "Note";
    //AUTHORITY is a globally unique string that identifies the content provider to the android framework
    // Only one app on device can use a particular authority
    private static final String AUTHORITY = "com.example.plainolnotes.notesprovider";
    // Represents the entire dataset: in our app we have only one table, so we have given base path as table name
    private static final String BASE_PATH = "notes";
    // URI is a Uniform Resource Identifier, it is used to identify content provider
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    // Constant to identify the requested operation we can do with this content provider.
    private static final int NOTES = 1; // Operation Desc.: Give me the data.
    private static final int NOTES_ID = 2; // Operation Desc.: Will deal with only a single record.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /*
    The code block with the static modifier signifies a class initializer;
    without the static modifier the code block is an instance initializer.
    */
    static {
        /* Registering operations */

        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);

        // # means any number i.e we are looking for any particular row in database
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext()); // We could not use "this" here because content provider is not a context
        database = helper.getWritableDatabase();
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // It will retrieve all the notes or just single note

        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        // Specify: Table to query on, Columns to retrive, where clause to filter data, null, null, null, sort order( sort on which parameter and how )
        // If we pass NULL for selection argument then we will get all the data
        return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS, selection, null, null, null, DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // This methods return a URI which is supposed to match the URI pattern defined above.
        // ContentValue class has collection of name value pairs. It is very similar to the
        // bundle class in android but the bundle class is generally used to manage the user
        // interface, where as ContentValues is used to pass the data around in backend.
        //


        // Getting value of primary key
        long id = database.insert(DBOpenHelper.TABLE_NOTES, null, values);

        // URI should match the URI pattern: AUTHORITY, BASE_PATH + "/#", NOTES_ID
        // Make sure we are using the version of URI from Android.net
        // The parse method returns the URI equivalent to String
        return Uri.parse(BASE_PATH + "/" + id);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Return an integer value that represents, number of rows deleted.

        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Returns: The number of rows affected.
        //
        return database.update(DBOpenHelper.TABLE_NOTES,
                values, selection, selectionArgs);
    }

}
