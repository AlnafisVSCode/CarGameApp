package com.carquiz;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

public class HintQuizActivity extends AppCompatActivity {

    private static final String TAG = "HintQuizActivity";
    private HashMap<Integer, Car> carHashMap;
    private ImageView imgCar;
    private Car currentCarForAnswer;
    private TextView txtResult, txtCarMakerName;
    private Button btnIdentify;
    private LinearLayout linearDashHolder;
    private int remainingWrongTry = 3;
    private EditText edtCharacterInput;
    private TextView txtTimer;
    private int count = 20;
    private boolean isActivityRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint_quiz);

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
            if (btnIdentify.getText().toString().equalsIgnoreCase("next")) {
                setNextImage();
            } else {
                String companyName = currentCarForAnswer.getCompanyName();
                final String inputCharacter = edtCharacterInput.getText().toString();
                Log.d(TAG, "clicks: " + inputCharacter);
                if (companyName.contains(inputCharacter) && !inputCharacter.isEmpty()) {
                    int index = -1;
                    while ((index = companyName.indexOf(inputCharacter, index + 1)) != -1) {
                        Log.d(TAG, "clicks: " + index);
                        TextView textView = (TextView) linearDashHolder.getChildAt(index);
                        if (textView != null) {
                            textView.setText(inputCharacter);
                        }
                    }

                    boolean isCompleted = true;
                    for (int i = 0; i < companyName.length(); i++) {
                        if (((TextView) linearDashHolder.getChildAt(i)).getText().equals("_")) {
                            isCompleted = false;
                            break;
                        }
                    }
                    if (isCompleted) {
                        count = 0;
                        setQuizResult(true);
                    }
                } else {
                    remainingWrongTry -= 1;
                    if (remainingWrongTry == 0) {
                        count = 0;
                        setQuizResult(false);
                    } else {
                        if (count == 0) {
                            count = 20;
                            setTime();
                        } else {
                            count = 20;
                        }
                        btnIdentify.setText(String.format(getString(R.string.submit_with_count), remainingWrongTry));
                    }
                }
                edtCharacterInput.setText("");
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());
    }

    private void setQuizResult(boolean isCorrectAnswer) {
        txtCarMakerName.setText(currentCarForAnswer.getCompanyName().toUpperCase());
        txtResult.setText(isCorrectAnswer ? "CORRECT!!!" : "WRONG!!!");
        txtResult.setTextColor(ContextCompat.getColor(this, isCorrectAnswer ? android.R.color.holo_green_light : android.R.color.holo_red_light));
        btnIdentify.setText(getString(R.string.next));
        edtCharacterInput.setEnabled(false);
    }

    private void setNextImage() {
        Random r = new Random();
        int nextIndex = r.nextInt(carHashMap.size());

        currentCarForAnswer = carHashMap.get(nextIndex);
        if (currentCarForAnswer != null) {
            imgCar.setImageBitmap(getBitmapFromAssets(currentCarForAnswer.getFileName()));
        }

        setDefaultText();
        setHintLayout();
        setTime();
    }

    private void setHintLayout() {
        int companyNameLength = currentCarForAnswer.getCompanyName().length();
        linearDashHolder.removeAllViews();

        for (int i = 0; i < companyNameLength; i++) {
            TextView dashTextView = new TextView(this);
            dashTextView.setTag(String.format("dash%s", 1));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            dashTextView.setLayoutParams(params);
            dashTextView.setText("_");
            if (linearDashHolder != null) {
                linearDashHolder.addView(dashTextView);
            }
        }
    }

    private void setDefaultText() {
        remainingWrongTry = 3;
        btnIdentify.setText(String.format(getString(R.string.submit_with_count), remainingWrongTry));
        txtResult.setText("");
        txtCarMakerName.setText(" ");
        edtCharacterInput.setEnabled(true);
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
        imgCar = findViewById(R.id.imgCar);
        txtResult = findViewById(R.id.txtResult);
        txtCarMakerName = findViewById(R.id.txtCarMakerName);
        btnIdentify = findViewById(R.id.btnIdentify);
        linearDashHolder = findViewById(R.id.linearDashHolder);
        edtCharacterInput = findViewById(R.id.edtCharacterInput);
        txtTimer = findViewById(R.id.txtTimer);
    }

    @Override
    protected void onDestroy() {
        count = 0;
        isActivityRunning = false;
        super.onDestroy();
    }
}