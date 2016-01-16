package com.coolbreeze.android.applenotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = (EditText) findViewById(R.id.editText);
//        editText.getBackground().clearColorFilter();

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            setTitle(getString(R.string.edit_note));
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);
            editor.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
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
            case android.R.id.home: //Id which automatically gets generated when we click back button;
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();

        // setResult: Call this to set the result that your activity will return to its caller

        // In the main activity we will use the value of result to restart our loader(async. call
        // to database)

        setResult(RESULT_OK);
        // So that we go back to previous activity
        finish();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0)
                    setResult(RESULT_CANCELED);
                else
                    insertNote(newText);
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0)
                    deleteNote();
                else if (oldText.equals(newText))
                    setResult(RESULT_CANCELED);
                else
                    updateNote(newText);
        }
        // This is called when the current activity is finished and should be closed.
        // The ActivityResult is propagated back to whoever launched you via onActivityResult()
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        // noteFilter tells which note to be updated
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String newText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }
}
