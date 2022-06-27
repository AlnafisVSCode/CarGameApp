package com.carquiz;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CarIdentifyActivity extends AppCompatActivity {

    private HashMap<Integer, Car> carHashMap;
    private ImageView imgCar1, imgCar2, imgCar3;
    private TextView txtResult, txtCarMakerName;
    private Button btnIdentify;
    private int selectedCarIndex;
    private List<Integer> indexList;
    private boolean canSelect;
    private TextView txtTimer;

    private int count = 20;
    private boolean isActivityRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_identify);

        init();
        setupData();
        clicks();
        setNextImages();
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
                    if (isActivityRunning) {
                        setQuizResult(txtResult.getText().toString().isEmpty());
                    }
                });
            }).start();
        }
    }

    private void clicks() {
        btnIdentify.setOnClickListener(view -> setNextImages());

        imgCar1.setOnClickListener(view -> {
            if (canSelect) {
                setQuizResult(Objects.requireNonNull(carHashMap.get(indexList.get(0))).getCompanyName()
                        .equalsIgnoreCase(Objects.requireNonNull(carHashMap.get(indexList.get(selectedCarIndex))).getCompanyName()));
            }
        });

        imgCar2.setOnClickListener(view -> {
            if (canSelect) {
                setQuizResult(Objects.requireNonNull(carHashMap.get(indexList.get(1))).getCompanyName()
                        .equalsIgnoreCase(Objects.requireNonNull(carHashMap.get(indexList.get(selectedCarIndex))).getCompanyName()));
            }
        });

        imgCar3.setOnClickListener(view -> {
            if (canSelect) {
                setQuizResult(Objects.requireNonNull(carHashMap.get(indexList.get(2))).getCompanyName()
                        .equalsIgnoreCase(Objects.requireNonNull(carHashMap.get(indexList.get(selectedCarIndex))).getCompanyName()));
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());
    }

    private void setQuizResult(boolean isCorrectAnswer) {
        count = 0;
        txtResult.setText(isCorrectAnswer ? "CORRECT!!!" : "WRONG!!!");
        txtResult.setTextColor(ContextCompat.getColor(this, isCorrectAnswer ? android.R.color.holo_green_light : android.R.color.holo_red_light));
        btnIdentify.setEnabled(true);
        canSelect = false;
    }

    private void setNextImages() {
        indexList = new ArrayList<>();
        while (indexList.size() < 3) {
            int randomIndex = getRandomIndex(carHashMap.size() - 1);
            Car randomCar = carHashMap.get(randomIndex);
            boolean isSameMakerAlreadyAvailable = false;
            if (!indexList.contains(randomIndex)) {
                for (Integer integer : indexList) {
                    if (Objects.requireNonNull(carHashMap.get(integer)).getCompanyName()
                            .equalsIgnoreCase(Objects.requireNonNull(randomCar).getCompanyName())) {
                        isSameMakerAlreadyAvailable = true;
                        break;
                    }
                }
                if (!isSameMakerAlreadyAvailable) {
                    indexList.add(randomIndex);
                }
            }
        }

        imgCar1.setImageBitmap(getBitmapFromAssets(Objects.requireNonNull(carHashMap.get(indexList.get(0))).getFileName()));
        imgCar2.setImageBitmap(getBitmapFromAssets(Objects.requireNonNull(carHashMap.get(indexList.get(1))).getFileName()));
        imgCar3.setImageBitmap(getBitmapFromAssets(Objects.requireNonNull(carHashMap.get(indexList.get(2))).getFileName()));

        selectedCarIndex = getRandomIndex(3);
        txtCarMakerName.setText(Objects.requireNonNull(carHashMap.get(indexList.get(selectedCarIndex))).getCompanyName().toUpperCase());

        setDefaultValues();
        setTime();
    }

    private int getRandomIndex(int uptoNumber) {
        Random r = new Random();
        return r.nextInt(uptoNumber);
    }

    private void setDefaultValues() {
        btnIdentify.setText(getString(R.string.next));
        btnIdentify.setEnabled(false);
        txtResult.setText("");
        canSelect = true;
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
    }

    private void init() {
        imgCar1 = findViewById(R.id.imgCar1);
        imgCar2 = findViewById(R.id.imgCar2);
        imgCar3 = findViewById(R.id.imgCar3);
        txtResult = findViewById(R.id.txtResult);
        txtCarMakerName = findViewById(R.id.txtCarMakerName);
        btnIdentify = findViewById(R.id.btnIdentify);
        txtTimer = findViewById(R.id.txtTimer);
    }

    @Override
    protected void onDestroy() {
        count = 0;
        isActivityRunning = false;
        super.onDestroy();
    }
}