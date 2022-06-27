package com.carquiz;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class CarData {

    private static final String TAG = "CarData";
    HashMap<Integer, Car> carHashMap;
    List<String> carFileName;
    private boolean isTimeMode = false;

    private static final CarData ourInstance = new CarData();

    static CarData getInstance() {
        return ourInstance;
    }

    private CarData() {
        carHashMap = new HashMap<>();
        carFileName = new ArrayList<>();
    }

    public void addFileName(String carFileName) {
        this.carFileName.add(carFileName);
    }

    public CarData prepareCarMakerQuiz() {
        ArrayList<Integer> indexList = new ArrayList<>();
        final int size = carFileName.size();
        for (int i = 0; i < size - 1; i++) {
            indexList.add(i);
        }
        Collections.shuffle(indexList);
        for (int i = 0; i < size - 1; i++) {
            String fileName = carFileName.get(i);
            if (fileName.startsWith("_")) {
                String carCompanyName = fileName.substring(fileName.indexOf("_", fileName.indexOf("_") + 1)).replaceAll("_", "");
                carHashMap.put(indexList.get(i), new Car().setFileName(fileName).setCompanyName(carCompanyName.substring(0, carCompanyName.lastIndexOf("."))));
            }
        }
        return this;
    }

    public HashMap<Integer, Car> getCarHashMap() {
        return carHashMap;
    }

    public boolean isTimeMode() {
        return isTimeMode;
    }

    public CarData setTimeMode(boolean timeMode) {
        Log.d(TAG, "setTimeMode() called with: timeMode = [" + timeMode + "]");
        isTimeMode = timeMode;
        return this;
    }
}
