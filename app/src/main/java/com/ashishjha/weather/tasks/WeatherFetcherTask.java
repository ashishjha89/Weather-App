package com.ashishjha.weather.tasks;

import android.os.AsyncTask;

import com.ashishjha.weather.model.Atmosphere;
import com.ashishjha.weather.model.Condition;
import com.ashishjha.weather.model.Weather;
import com.ashishjha.weather.model.Wind;
import com.ashishjha.weather.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class WeatherFetcherTask extends AsyncTask<String, Void, String> {

    public interface WeatherDetailListener {
        void setWeatherData(Weather data);
    }


    private WeatherDetailListener mListener;

    public static String YAHOO_GEO_URL = "http://where.yahooapis.com/v1";

    // public static String YAHOO_WEATHER_URL = "https://query.yahooapis.com/v1/public/yq";

    // public static String YAHOO_WEATHER_URL = "http://weather.yahooapis.com/forecastrss";

    public WeatherFetcherTask(WeatherDetailListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... cityName) {
        if (cityName == null || cityName.length != 1) {
            return null;
        }
        String placesQuery = makeQueryCityURL(cityName[0]);
        String placesResponse = getContent(placesQuery);
        if (placesResponse == null || placesResponse.isEmpty()) {
            return null;
        }
        String woeid = getWoeid(placesResponse);
        if (woeid == null || woeid.isEmpty()) {
            return null;
        }
        String weatherQuery = makeWeatherURL(woeid);
        return getContent(weatherQuery);
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            mListener.setWeatherData(getWeather(response));
        }
    }

    private String getContent(String query) {
        HttpURLConnection yahooHttpConn;
        try {
            yahooHttpConn = (HttpURLConnection) (new URL(query)).openConnection();
            yahooHttpConn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String response = null;
        try {
            // int retCode = yahooHttpConn.getResponseCode();
            InputStream inputStream = yahooHttpConn.getInputStream();
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                // Log.d(TAG, "sendRequest IOException");
                e.printStackTrace();
                return null;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            response = sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    private static String makeQueryCityURL(String cityName) {
        // We remove spaces in cityName
        try {
            cityName = cityName.replaceAll(" ", "%20");
        } catch (NullPointerException e) {
            // Ignore. The issue is observed when user navigates to far distance
        }
        return YAHOO_GEO_URL + "/places.q(" + cityName + "%2A);count=" + Utils.MAX_CITY_RESULT + "?appid=" + Utils.APP_ID + "&format=json";
    }

    private static String makeWeatherURL(String woeid) {
        return "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(" + woeid + ")&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    }

    private String getWoeid(String data) {
        try {
            JSONObject responseJson = new JSONObject(data);
            if (responseJson.has(Utils.PLACES)) {
                JSONObject placesJson = responseJson.getJSONObject(Utils.PLACES);
                if (placesJson.has(Utils.PLACE)) {
                    JSONArray placeArray = placesJson.getJSONArray(Utils.PLACE);
                    if (placeArray.length() == 0) {
                        return null;
                    }
                    JSONObject firstPlace = placeArray.getJSONObject(0);
                    if (firstPlace.has(Utils.WOEID)) {
                        return firstPlace.getString(Utils.WOEID);
                    } else {
                        return null;
                    }

                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Weather getWeather(String weatherData) {
        Weather weather = new Weather();
        try {
            JSONObject weatherJson = new JSONObject(weatherData);
            if (weatherJson.has(Utils.QUERY)) {
                JSONObject queryJson = weatherJson.getJSONObject(Utils.QUERY);
                if (queryJson.has(Utils.RESULTS)) {
                    JSONObject resultsJson = queryJson.getJSONObject(Utils.RESULTS);
                    if (resultsJson.has(Utils.CHANNEL)) {
                        JSONObject channelJson = resultsJson.getJSONObject(Utils.CHANNEL);
                        if (channelJson.has(Utils.ITEM)) {
                            JSONObject itemJson = channelJson.getJSONObject(Utils.ITEM);
                            if (itemJson.has(Utils.CONDITIONS)) {
                                Condition condition = new Condition();
                                JSONObject conditionJson = itemJson.getJSONObject(Utils.CONDITIONS);
                                if (conditionJson.has(Utils.CODE)) {
                                    int code = Integer.parseInt(conditionJson.getString(Utils.CODE));
                                    condition.setCode(code);
                                }
                                if (conditionJson.has(Utils.TEMP)) {
                                    int temp = Integer.parseInt(conditionJson.getString(Utils.TEMP));
                                    condition.setTemp(temp);
                                }
                                if (conditionJson.has(Utils.TEXT)) {
                                    condition.setDescription(conditionJson.getString(Utils.TEXT));
                                }
                                weather.setCondition(condition);
                            }
                        }
                        if (channelJson.has(Utils.ATMOSPHERE)) {
                            Atmosphere atmosphere = new Atmosphere();
                            JSONObject conditionJson = channelJson.getJSONObject(Utils.ATMOSPHERE);
                            if (conditionJson.has(Utils.HUMIDITY)) {
                                int humidity = Integer.parseInt(conditionJson.getString(Utils.HUMIDITY));
                                atmosphere.setHumidity(humidity);
                            }
                            if (conditionJson.has(Utils.PRESSURE)) {
                                double pressure = Double.parseDouble(conditionJson.getString(Utils.PRESSURE));
                                atmosphere.setPressure(pressure);
                            }
                            weather.setAtmosphere(atmosphere);
                        }
                        if (channelJson.has(Utils.WIND)) {
                            Wind wind = new Wind();
                            JSONObject conditionJson = channelJson.getJSONObject(Utils.WIND);
                            if (conditionJson.has(Utils.CHILL)) {
                                int chill = Integer.parseInt(conditionJson.getString(Utils.CHILL));
                                wind.setChill(chill);
                            }
                            if (conditionJson.has(Utils.SPEED)) {
                                int speed = Integer.parseInt(conditionJson.getString(Utils.SPEED));
                                wind.setSpeed(speed);
                            }
                            weather.setWind(wind);
                        }
                    } else {
                        return null;
                    }

                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return weather;
    }
}
