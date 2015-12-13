package com.ashishjha.weather.activities;

import android.app.Dialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ashishjha.weather.R;
import com.ashishjha.weather.list.WeatherListAdapter;
import com.ashishjha.weather.model.Weather;
import com.ashishjha.weather.tasks.AddressFetcherTask;
import com.ashishjha.weather.tasks.WeatherFetcherTask;
import com.ashishjha.weather.utils.Utils;
import com.ashishjha.weather.utils.WeatherItemToIndexMapper;

/**
 * Created by ashish.jha on 1/12/15.
 */


public class MapActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AddressFetcherTask.MarkerTitleUpdateListener,
        WeatherFetcherTask.WeatherDetailListener {

    private static final int ERROR_DIALOG_REQUEST = 1;

    //map object
    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    //Location object used for getting latitude and longitude
    private Location mLastLocation;

    private LinearLayout mMapFrameLayout;

    private ListView mWeatherListView;

    private WeatherListAdapter mAdapter;

    private int mPrevBgColor = -1;

    private static final String TAG = MapActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapFrameLayout = (LinearLayout) findViewById(R.id.map_layout);
        mWeatherListView = (ListView) findViewById(R.id.weather_list_view);
        mAdapter = new WeatherListAdapter(this);
        mWeatherListView.setAdapter(mAdapter);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*
    Checking the google play services is available
    */
    private boolean checkServices() {
        //returns a integer value
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        //if connection is available
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MapActivity.this, "Cannot connect to mapping Service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /*
    Initializing the map
    */
    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
        }

        if (mPrevBgColor == -1) {
            setBackgroundColor(R.color.cold);
        } else {
            setBackgroundColor(mPrevBgColor);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Clears the previously touched position
                mMap.clear();
                // Update marker in map
                updateMap(latLng.latitude, latLng.longitude, latLng.latitude + " : " + latLng.longitude);
                // refresh weather details. First obtain marker address. Then fetch its weather details
                updateAddressDetails(latLng.latitude, latLng.longitude, true);
            }
        });
        return (mMap != null);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected()");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //getting the latitude value
            double latitudeValue = mLastLocation.getLatitude();
            //getting the longitude value
            double longitudeValue = mLastLocation.getLongitude();
            if (checkServices()) {
                if (initMap()) {
                    //update the map with the current location
                    gotoLocation(latitudeValue, longitudeValue, Utils.ZOOM_FACTOR);
                    // Update marker in map
                    updateMap(latitudeValue, longitudeValue, getResources().getString(R.string.current_location));
                    // refresh weather details. First obtain marker address. Then fetch its weather details
                    updateAddressDetails(latitudeValue, longitudeValue, false);
                }
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    private void updateAddressDetails(double latitude, double longitude, boolean updateAddress) {
        // 1. Update Address - in the callback method, initiate update weather
        Log.d(TAG, "updateAddressDetails()");
        if (Utils.isNetworkAvailable(this)) {
            AddressFetcherTask addressFetcherTask = new AddressFetcherTask(this, this, updateAddress);
            Double[] coordinates = {latitude, longitude};
            addressFetcherTask.execute(coordinates);
        } else {
            Toast.makeText(this, getResources().getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
        }

    }

    private void updateWeatherDetails(String city) {
        WeatherFetcherTask weatherFetcherTask = new WeatherFetcherTask(this);
        weatherFetcherTask.execute(city);
    }

    private void setBackgroundColor(int colorId) {
        mPrevBgColor = colorId;
        mMapFrameLayout.setBackgroundColor(getResources().getColor(colorId));
        if (mWeatherListView != null) {
            mWeatherListView.setBackgroundColor(getResources().getColor(colorId));
        }
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    private void updateMap(double latitude, double longitude, String title) {
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        MarkerOptions marker = new MarkerOptions()
                .title(title)
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(marker);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void updateMarkerTitle(Address address, double[] coordinates) {
        Log.d(TAG, "updateMarkerTitle()");
        if (address != null) {
            // set Marker
            String title = address.getAddressLine(0);
            updateMap(coordinates[0], coordinates[1], title);
            // start weather detail fetcher task
            if (Utils.isLocationOn(this)) {
                updateWeatherDetails(address.getLocality());
            } else {
                Toast.makeText(this, getResources().getString(R.string.location_unavailable), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void setCity(String city) {
        Log.d(TAG, "setCity()");
        updateWeatherDetails(city);
        if (Utils.isLocationOn(this)) {
            updateWeatherDetails(city);
        } else {
            Toast.makeText(this, getResources().getString(R.string.location_unavailable), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setWeatherData(Weather weather) {
        mAdapter.setWeather(weather);
        mAdapter.notifyDataSetChanged();
        if (weather.getCondition() != null) {
            setBackgroundColor(WeatherItemToIndexMapper.getBackgroundColor(weather.getCondition().getTemp()));
            // mWeatherListView.setBackgroundColor(WeatherItemToIndexMapper.getBackgroundColor(weather.getCondition().getTemp()));
            // mMapFrameLayout.setBackgroundColor(WeatherItemToIndexMapper.getBackgroundColor(weather.getCondition().getTemp()));
        }
    }
}
