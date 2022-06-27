package com.carquiz;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AdvanceQuizActivity extends AppCompatActivity {

    private static final String TAG = "AdvanceQuizActivity";

    private HashMap<Integer, Car> carHashMap;
    private Context context;
    private ImageView imgCar1, imgCar2, imgCar3;
    private TextView txtResult;
    private Button btnIdentify;
    private EditText edtCarMakerName1, edtCarMakerName2, edtCarMakerName3;
    private Car car1, car2, car3;
    private int score = 0;
    private TextInputLayout edtInput3, edtInput2, edtInput1;

    private TextView txtTimer;
    private TextView txtScore;
    private int remainingWrongTry = 3;

    private int count = 20;
    private boolean isActivityRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_quiz);

        context = this;
        init();
        setupData();
        clicks();
        setNextImages();
    }

    private void clicks() {
        btnIdentify.setOnClickListener(view -> {
            if (btnIdentify.getText().toString().equalsIgnoreCase(getString(R.string.next))) {
                setNextImages();
            } else {
                score = 0;
                if (car1.getCompanyName().equalsIgnoreCase(edtCarMakerName1.getText().toString())) {
                    score += 1;
                    edtCarMakerName1.setEnabled(false);
                    edtCarMakerName1.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                } else {
                    edtInput1.clearFocus();
                    edtCarMakerName1.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                }
                if (car2.getCompanyName().equalsIgnoreCase(edtCarMakerName2.getText().toString())) {
                    score += 1;
                    edtCarMakerName2.setEnabled(false);
                    edtCarMakerName2.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                } else {
                    edtInput2.clearFocus();
                    edtCarMakerName2.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                }
                if (car3.getCompanyName().equalsIgnoreCase(edtCarMakerName3.getText().toString())) {
                    score += 1;
                    edtCarMakerName3.setEnabled(false);
                    edtCarMakerName3.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                } else {
                    edtInput3.clearFocus();
                    edtCarMakerName3.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                }

                setQuizResult();
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        //<editor-fold desc="EditText change listener">
        edtCarMakerName1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtCarMakerName1.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCarMakerName2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtCarMakerName2.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCarMakerName3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtCarMakerName3.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //</editor-fold>
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

    private void setQuizResult() {
        setScore();
        boolean isCorrectAnswer = score == 3;
        if (!isCorrectAnswer) {
            if (remainingWrongTry > 0) {
                remainingWrongTry -= 1;
                count = 20;
            }
        }
        if (remainingWrongTry == 0) {
            edtCarMakerName1.setEnabled(false);
            edtCarMakerName2.setEnabled(false);
            edtCarMakerName3.setEnabled(false);
            count = 0;
        }
        Log.d(TAG, "setQuizResult: " + remainingWrongTry);
        txtResult.setText(isCorrectAnswer ? "CORRECT!!!" : "WRONG!!!");
        btnIdentify.setText(isCorrectAnswer ? "Next" : String.format(getString(R.string.submit_with_count), remainingWrongTry));
        if (remainingWrongTry == 0) btnIdentify.setText(getString(R.string.next));
        txtResult.setTextColor(ContextCompat.getColor(this, isCorrectAnswer ? android.R.color.holo_green_light : android.R.color.holo_red_light));
        btnIdentify.setEnabled(true);
    }

    private void setNextImages() {
        List<Integer> indexList = new ArrayList<>();
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

        car1 = carHashMap.get(indexList.get(0));
        car2 = carHashMap.get(indexList.get(1));
        car3 = carHashMap.get(indexList.get(2));

        imgCar1.setImageBitmap(getBitmapFromAssets(car1.getFileName()));
        imgCar2.setImageBitmap(getBitmapFromAssets(car2.getFileName()));
        imgCar3.setImageBitmap(getBitmapFromAssets(car3.getFileName()));

        setDefaultValues();
        setScore();
        setTime();
    }

    private void setScore() {
        txtScore.setText(String.format("Score : %s", score));
    }

    private int getRandomIndex(int uptoNumber) {
        Random r = new Random();
        return r.nextInt(uptoNumber);
    }

    private void setDefaultValues() {
        remainingWrongTry = 3;
        btnIdentify.setText(String.format("Submit (%s)", remainingWrongTry));
        txtResult.setText("");
        score = 0;
        count = 20;

        edtCarMakerName1.setEnabled(true);
        edtCarMakerName2.setEnabled(true);
        edtCarMakerName3.setEnabled(true);

        edtCarMakerName1.setText("");
        edtCarMakerName2.setText("");
        edtCarMakerName3.setText("");
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
        btnIdentify = findViewById(R.id.btnIdentify);
        txtScore = findViewById(R.id.txtScore);

        edtCarMakerName1 = findViewById(R.id.edtCarMakerName1);
        edtCarMakerName2 = findViewById(R.id.edtCarMakerName2);
        edtCarMakerName3 = findViewById(R.id.edtCarMakerName3);

        edtInput1 = findViewById(R.id.edtInput1);
        edtInput2 = findViewById(R.id.edtInput2);
        edtInput3 = findViewById(R.id.edtInput3);

        txtTimer = findViewById(R.id.txtTimer);
    }

    @Override
    protected void onDestroy() {
        count = 0;
        isActivityRunning = false;
        super.onDestroy();
    }
}