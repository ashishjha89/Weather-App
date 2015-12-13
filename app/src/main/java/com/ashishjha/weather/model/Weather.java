package com.ashishjha.weather.model;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class Weather {

    private Condition mCondition;

    private Atmosphere mAtmosphere;

    private Wind mWind;

    public static final int VISIBLE_ITEM_COUNT = 4;

    public void setCondition(Condition condition) {
        mCondition = condition;
    }

    public Condition getCondition() {
        return mCondition;
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        mAtmosphere = atmosphere;
    }

    public Atmosphere getAtmosphere() {
        return mAtmosphere;
    }

    public void setWind(Wind wind) {
        mWind = wind;
    }

    public Wind getWind() {
        return mWind;
    }

}
