package com.ashishjha.weather.utils;

import com.ashishjha.weather.R;
import com.ashishjha.weather.model.Weather;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class WeatherItemToIndexMapper {

    public static int getDrawableIcon(int pos, int code) {
        switch (pos) {
            case 1:
                return R.drawable.wind;
            case 2:
                return R.drawable.humidity;
            case 3:
                return R.drawable.pressure;
            case 0:
                return getConditionIcon(code);
            default:
                return -1;
        }
    }

    public static String getTextDesc(int pos, Weather weather) {
        switch (pos) {
            case 0:
                if (weather.getCondition() == null) {
                    return "Temperature Unknown";
                }
                return weather.getCondition().getDescription() + " " + weather.getCondition().getTemp() + " Fah";
            case 1:
                if (weather.getWind() != null) {
                    if (weather.getWind() == null) {
                        return "Chill Unknown";
                    }
                    return "Chill " + weather.getWind().getChill() + "\nSpeed " + weather.getWind().getSpeed();
                } else {
                    return "Chill Unknown";
                }
            case 2:
                if (weather.getAtmosphere() != null)
                    return "Humidity " + weather.getAtmosphere().getHumidity();
                else
                    return "Humidity Unknown";
            case 3:
                if (weather.getAtmosphere() != null)
                    return "Pressure " + weather.getAtmosphere().getPressure();
                else
                    return "Pressure Unknown";
            default:
                return "Unknown";
        }
    }

    private static int getConditionIcon(int code) {
        if (code >= 0 && code <= 4) {
            return R.drawable.storm;
        }
        if (code >= 5 && code <= 12) {
            return R.drawable.rain;
        }
        if (code >= 13 && code <= 17) {
            return R.drawable.snow;
        }
        if (code >= 18 && code <= 25) {
            return R.drawable.windy;
        }
        if (code >= 26 && code <= 30) {
            return R.drawable.cloudy;
        }
        if (code >= 31 && code <= 36) {
            return R.drawable.clear;
        }
        if (code >= 37 && code <= 39) {
            return R.drawable.storm;
        }
        if (code >= 40 && code <= 43) {
            return R.drawable.snow;
        }
        if (code >= 40 && code <= 43) {
            return R.drawable.rain;
        } else {
            return R.drawable.clear;
        }
    }

    public static int getBackgroundColor(int temp) {
        if (temp <= 40) {
            return R.color.cold;
        }
        if (temp <= 55) {
            return R.color.less_cold;
        }
        if (temp <= 70) {
            return R.color.pleasant;
        }
        if (temp <= 85) {
            return R.color.less_hot;
        } else {
            return R.color.hot;
        }
    }
}


