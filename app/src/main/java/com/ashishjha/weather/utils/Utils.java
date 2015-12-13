package com.ashishjha.weather.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class Utils {

    public static final float ZOOM_FACTOR = (float) 15.0;
    ;

    public static int MAX_CITY_RESULT = 1;

    public static final String APP_ID = "dj0yJmk9Q0lkNGFZaVNIUkpxJmQ9WVdrOVkyRmhRVVZvTm1zbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD1jNA--";

    public static final String PLACES = "places";

    public static final String PLACE = "place";

    public static final String WOEID = "woeid";

    public static final String QUERY = "query";

    public static final String RESULTS = "results";

    public static final String CHANNEL = "channel";

    public static final String ITEM = "item";

    public static final String WIND = "wind";

    public static final String ATMOSPHERE = "atmosphere";

    public static final String ASTRONOMY = "astronomy";

    public static final String CONDITIONS = "condition";

    public static final String CODE = "code";

    public static final String TEMP = "temp";

    public static final String TEXT = "text";

    public static final String HUMIDITY = "humidity";

    public static final String PRESSURE = "pressure";

    public static final String CHILL = "chill";

    public static final String SPEED = "speed";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationOn(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }
}
