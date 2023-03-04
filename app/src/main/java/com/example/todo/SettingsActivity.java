package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private ArrayList<Integer> backgrounds;
    private Spinner spinnerCurrentBackground;
    private ImageView imageViewCurrentBackground;

    private int selectedImageId = R.drawable.rock1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backgrounds = new ArrayList<>();
        backgrounds.add(R.drawable.rock1);
        backgrounds.add(R.drawable.rock2);
        backgrounds.add(R.drawable.rock3);
        backgrounds.add(R.drawable.rock4);

        initViews();

        spinnerCurrentBackground.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedImageId = backgrounds.get(i);
                imageViewCurrentBackground.setImageDrawable(getDrawable(backgrounds.get(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void saveSettings(View view) {
        MainActivity.notesDB.execSQL(String.format("UPDATE config SET value = '%d' WHERE settingName = 'background';", selectedImageId));
        finish();
    }

    private void initViews(){
        spinnerCurrentBackground = findViewById(R.id.spinnerCurrentBackground);
        imageViewCurrentBackground = findViewById(R.id.imageViewCurrentBackground);

        Cursor background = MainActivity.notesDB.rawQuery("SELECT value FROM config WHERE settingName = 'background'", null);
        background.moveToFirst();

        spinnerCurrentBackground.setSelection(backgrounds.indexOf(background.getInt(0)));
    }
}

