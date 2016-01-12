package com.coolbreeze.android.applenotes;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    /*
        It is a best practise to execute all database related tasks on background threads. There are
        couple of ways of doing this. We can create our own implementation of async tasks, and call all
        database operations within that implementation.

        We can also use the Loader Interface, loaders execute data operations on the background thread
        automatically. Also they elegantly handle changes in configuration such as orientation.
        LoaderCallbacks implementation is going to manage cursor object.

        We will never call the methods onCreateLoader,onLoadFinished,onLoaderReset: they are all
        Callback methods
     */
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        insertNote("New Note");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String from[] = {DBOpenHelper.NOTE_TEXT};
        int[] to = {android.R.id.text1};
        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();

        // Adding Key-Value pair: The key is the name of the column we are assigning value to.
        values.put(DBOpenHelper.NOTE_TEXT, noteText);

        // Calling content provider
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);

        // call d method(debug method)
        // Argument: MainActivity: current class
        Log.d("MainActivity", "Inserted note " + noteUri.getLastPathSegment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_create_example:
                insertExampleData();
                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertExampleData() {
        insertNote("Simple note");
        insertNote("Multi-line\nnote");
        insertNote("Very long note with a lot of text that exceeds the width of the screen");

        restartLoader();
    }


    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(
                                    NotesProvider.CONTENT_URI, null, null
                            );
                            restartLoader();

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void restartLoader() {
        // This method is called every time we update,insert or delete data

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //

        //This method is called whenever the data is needed from content provider.

        //CursorLoader class is specifically designed to manage the cursor.

        //

        //Arguments: context, URI of content provider, list of columns(projection),

        //where clause(selection), selection Arguments and sort order.

        //

        // When we create a CursorLoader object, it executes the Query method on the background
        // thread and when the data comes back: the onLoadFinished is called for us.
        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This method receives a Cursor object
        // Our job is to take the data represented by the cursor object and pass it to the cursor
        // object.
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This method is called whenever the data needs to be wiped out.
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewNote(View view) {
//        Intent intent = new Intent(this, EditorActivity.class);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }
}
