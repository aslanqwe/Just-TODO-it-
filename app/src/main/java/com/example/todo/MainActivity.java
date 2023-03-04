package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int EDIT_ID = 1;
    private final int DELETE_ID = 2;

    public static final String NOTE_ID = "ID";
    public static final String NOTE_TEXT = "TEXT";
    public static final String NOTE_IMPORTANCE = "IMPORTANCE";

    private View selectedView;

    public static SQLiteDatabase notesDB;
    public static MediaPlayer player;

    private LinearLayout linearLayoutNotes;
    private ImageView imageViewBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        notesDB = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        notesDB.execSQL("CREATE TABLE IF NOT EXISTS config (id INTEGER PRIMARY KEY, settingName TEXT, value INTEGER)");
        notesDB.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, text TEXT, importancy INTEGER)");

        if (notesDB.rawQuery("SELECT * FROM config WHERE settingName = 'background'", null).getCount() == 0){
            notesDB.execSQL("INSERT INTO config (settingName, value) VALUES " + String.format("('background', '%d')", R.drawable.rock1));
        }

        updateBackground();

        player = MediaPlayer.create(this, R.raw.rock_music);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent newActivity = new Intent(this, SettingsActivity.class);
                startActivity(newActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, EDIT_ID, 0, getString(R.string.edit_item_text));
        menu.add(0, DELETE_ID, 0, getString(R.string.delete_item_text));
        selectedView = v;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        TextView textViewId = selectedView.findViewById(R.id.textViewId);
        switch (item.getItemId()){
            case EDIT_ID:
                Intent myIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                TextView textViewToEdit = selectedView.findViewById(R.id.textViewNote);
                Cursor queryResult = notesDB.rawQuery(String.format("SELECT importancy FROM notes WHERE id = '%s'", textViewId.getText().toString()), null);
                queryResult.moveToNext();
                int importance = queryResult.getInt(0);

                myIntent.putExtra(NOTE_ID, textViewId.getText().toString());
                myIntent.putExtra(NOTE_TEXT, textViewToEdit.getText().toString());
                myIntent.putExtra(NOTE_IMPORTANCE, importance);
                startActivity(myIntent);
                break;
            case DELETE_ID:
                String id = textViewId.getText().toString();
                notesDB.execSQL(String.format("DELETE FROM notes WHERE id = '%s'", id));
                break;
        }
        showNotes();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showNotes();
        updateBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDB.close();
    }

    private void initViews() {
        linearLayoutNotes = findViewById(R.id.linearLayoutNotes);
        imageViewBackground = findViewById(R.id.imageViewBackground);
    }

    public void showNotes(){
        linearLayoutNotes.removeAllViews();
        Cursor query = notesDB.rawQuery("SELECT * FROM notes", null);
        query.moveToFirst();
        for (int i = 0; i < query.getCount(); i++){

            Note note = new Note(query.getInt(0), query.getString(1), query.getInt(2));

            View view = getLayoutInflater().inflate(R.layout.note_item, linearLayoutNotes, false);
            TextView textViewNote = view.findViewById(R.id.textViewNote);
            TextView textViewId = view.findViewById(R.id.textViewId);

            textViewId.setText(String.valueOf(note.getId()));
            textViewNote.setText(note.getText());

            int colorResId;
            switch (note.getPriority()){
                case 0:
                    colorResId = R.color.light_green;
                    break;
                case 1:
                    colorResId = R.color.light_orange;
                    break;
                default:
                    colorResId = R.color.light_red;
                    break;
            }
            textViewNote.setBackgroundColor(getResources().getColor(colorResId));
            registerForContextMenu(view);
            linearLayoutNotes.addView(view, 0);

            query.moveToNext();
        }
        query.close();
    }

    private void updateBackground(){
        Cursor background = notesDB.rawQuery("SELECT value FROM config WHERE settingName = 'background'", null);
        background.moveToFirst();

        imageViewBackground.setImageDrawable(getDrawable(background.getInt(0)));
    }

    public void AddNote(View view) {
        Intent myIntent = new Intent(MainActivity.this, AddNoteActivity.class);
        startActivity(myIntent);
    }
}