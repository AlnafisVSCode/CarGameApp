package com.carquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;
        listAssetFiles("");

        findViewById(R.id.linearSearchMaker).setOnClickListener(view -> startActivity(new Intent(context, CarMakerQuizActivity.class)));
        findViewById(R.id.linearHint).setOnClickListener(view -> startActivity(new Intent(context, HintQuizActivity.class)));
        findViewById(R.id.linearSearchCar).setOnClickListener(view -> startActivity(new Intent(context, CarIdentifyActivity.class)));
        findViewById(R.id.linearAdvanceLevel).setOnClickListener(view -> startActivity(new Intent(context, AdvanceQuizActivity.class)));

        final SwitchMaterial timerSwitch = findViewById(R.id.timeSwitch);
        timerSwitch.setOnCheckedChangeListener((compoundButton, b) -> CarData.getInstance().setTimeMode(b));
    }

    private boolean listAssetFiles(String path) {
        String[] list;
        try {
            list = getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                        if (file.startsWith("_"))
                            CarData.getInstance().addFileName(file);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}