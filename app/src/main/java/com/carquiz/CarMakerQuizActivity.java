package com.carquiz;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked", "BusyWait"})
public class CarMakerQuizActivity extends AppCompatActivity {

    private static final String TAG = "CarMakerQuiz";

    private AutoCompleteTextView autoCompleteTextView;
    private HashMap<Integer, Car> carHashMap;
    private ImageView imgCar;
    private Car currentCarForAnswer;
    private TextView txtResult, txtCarMakerName;
    private Button btnIdentify;
    private TextView txtTimer;
    private int count = 20;
    private boolean isActivityRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_maker_quiz);

        init();
        setupData();
        clicks();
        setNextImage();
    }

    private void setTime() {
        if (CarData.getInstance().isTimeMode()) {
            new Thread(() -> {
                while (count > 0 && isActivityRunning) {
                    runOnUiThread(() -> txtTimer.setText(String.format("00:%02d", count)));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count -= 1;
                }

                runOnUiThread(() -> txtTimer.setText(String.format("00:%02d", Math.max(count, 0))));
                runOnUiThread(() -> {
                    if (isActivityRunning && !btnIdentify.getText().toString().equalsIgnoreCase(getString(R.string.next))) {
                        btnIdentify.performClick();
                    }
                });
            }).start();
        }
    }

    private void clicks() {
        btnIdentify.setOnClickListener(view -> {
            final String selectedText = autoCompleteTextView.getText().toString();
            Log.d(TAG, "clicks: " + selectedText);

            if (btnIdentify.getText().toString().equalsIgnoreCase("next")) {
                setNextImage();
            } else {
                if (!selectedText.contains("Select Car ")) {
                    setQuizResult(currentCarForAnswer.getCompanyName().equalsIgnoreCase(selectedText));
                } else {
                    Toast.makeText(this, "Please select car maker from DropDown", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());
    }

    private void setQuizResult(boolean isCorrectAnswer) {
        txtCarMakerName.setText(currentCarForAnswer.getCompanyName().toUpperCase());
        txtResult.setText(isCorrectAnswer ? "CORRECT!!!" : "WRONG!!!");
        txtResult.setTextColor(ContextCompat.getColor(this, isCorrectAnswer ? android.R.color.holo_green_light : android.R.color.holo_red_light));
        autoCompleteTextView.setEnabled(false);
        btnIdentify.setText(getString(R.string.next));
        count = 0;
    }

    private void setNextImage() {
        Random r = new Random();
        int nextIndex = r.nextInt(carHashMap.size());

        currentCarForAnswer = carHashMap.get(nextIndex);
        if (currentCarForAnswer != null) {
            imgCar.setImageBitmap(getBitmapFromAssets(currentCarForAnswer.getFileName()));
        }

        setDefaultText();
        setTime();
    }

    @Override
    protected void onDestroy() {
        count = 0;
        isActivityRunning = false;
        super.onDestroy();
    }

    private void setDefaultText() {
        btnIdentify.setText(getString(R.string.identify));
        autoCompleteTextView.setEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            autoCompleteTextView.setText(autoCompleteTextView.getAdapter().getItem(0).toString(), false);
        } else {
            autoCompleteTextView.setText(autoCompleteTextView.getAdapter().getItem(0).toString());
        }
        txtResult.setText("");
        txtCarMakerName.setText(" ");
        count = 20;
    }

    private Bitmap getBitmapFromAssets(String fileName) {
        AssetManager am = getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }

    private void setupData() {
        carHashMap = CarData.getInstance().prepareCarMakerQuiz().getCarHashMap();
        txtTimer.setVisibility(CarData.getInstance().isTimeMode() ? View.VISIBLE : View.GONE);

        List<String> adapterList = new ArrayList<String>() {{
            add("Select Car maker");
        }};
        List<String> companyNames = new ArrayList<>();
        for (Car value : carHashMap.values()) {
            companyNames.add(value.getCompanyName().toUpperCase());
        }
        Set<String> set = new HashSet<>(companyNames);
        adapterList.addAll(set);
        Collections.sort(companyNames);
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.company_name_list_item, adapterList);
        autoCompleteTextView.setAdapter(adapter);
        adapter.getFilter().filter(null);
        autoCompleteTextView.setSelection(0);
    }

    private void init() {
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        imgCar = findViewById(R.id.imgCar);
        txtResult = findViewById(R.id.txtResult);
        txtCarMakerName = findViewById(R.id.txtCarMakerName);
        btnIdentify = findViewById(R.id.btnIdentify);
        txtTimer = findViewById(R.id.txtTimer);
    }
}