package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AddNoteActivity extends AppCompatActivity {

    EditText editNote;
    RadioGroup radioGroupImportance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        editNote = findViewById(R.id.editTextNote);
        radioGroupImportance = findViewById(R.id.radioGroupImportance);
        if (getIntent().hasExtra(MainActivity.NOTE_TEXT)){
            editNote.setText(getIntent().getStringExtra(MainActivity.NOTE_TEXT));
            RadioButton toSelect = (RadioButton) radioGroupImportance.getChildAt(getIntent().getIntExtra(MainActivity.NOTE_IMPORTANCE, 0));
            toSelect.setChecked(true);
        }
    }

    public void SendNotes(View view) {
        int selectedIndex = -1;
        for (int i = 0; i < radioGroupImportance.getChildCount(); i++){
            RadioButton j = (RadioButton) radioGroupImportance.getChildAt(i);
            if (j.isChecked()){
                selectedIndex = i;
                break;
            }
        }
        if (editNote.getText().toString().trim().isEmpty() || selectedIndex == -1) return;
        if (getIntent().hasExtra(MainActivity.NOTE_TEXT)){
            MainActivity.notesDB.execSQL(String.format("UPDATE notes SET text = '%s', importancy = '%d' WHERE id = %s;", editNote.getText().toString(), selectedIndex, getIntent().getStringExtra(MainActivity.NOTE_ID)));
            finish();
            return;
        }
        MainActivity.notesDB.execSQL("INSERT INTO notes (text, importancy) VALUES " + String.format("('%s', '%d');", editNote.getText().toString(), selectedIndex));
        finish();
    }
}