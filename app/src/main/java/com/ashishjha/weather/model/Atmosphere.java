package com.ashishjha.weather.model;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class Atmosphere {

    private int mHumidity;
    private double mPressure;

    public void setHumidity(int h) {
        mHumidity = h;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setPressure(double p) {
        mPressure = p;
    }

    public double getPressure() {
        return mPressure;
    }

}
