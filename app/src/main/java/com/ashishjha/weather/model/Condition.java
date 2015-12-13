package com.ashishjha.weather.model;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class Condition {

    private String mDescription;
    private int mCode;
    private int mTemp;

    public void setDescription(String d) {
        mDescription = d;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setCode(int c) {
        mCode = c;
    }

    public int getCode() {
        return mCode;
    }

    public void setTemp(int t) {
        mTemp = t;
    }

    public int getTemp() {
        return mTemp;
    }

}
