package com.ashishjha.weather.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ashishjha.weather.R;
import com.ashishjha.weather.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class AddressFetcherTask extends AsyncTask<Double, Address, Address> {

    /* Interface responsible for updating marker or city information.*/
    public interface MarkerTitleUpdateListener {
        void updateMarkerTitle(Address address, double[] coordinates);

        void setCity(String city);
    }

    private Context mContext;

    private MarkerTitleUpdateListener mListener;

    private double[] selectedLocation;

    private boolean mShouldUpdateAddress;

    public AddressFetcherTask(Context context, MarkerTitleUpdateListener listener, boolean shouldUpdateAddress) {
        mContext = context;
        mListener = listener;
        mShouldUpdateAddress = shouldUpdateAddress;
    }

    @Override
    protected Address doInBackground(Double... coordinates) {
        if (coordinates == null || coordinates.length != 2) {
            return null;
        }
        selectedLocation = new double[2];
        selectedLocation[0] = coordinates[0];
        selectedLocation[1] = coordinates[1];
        return getAddress(coordinates[0], coordinates[1]);
    }

    @Override
    protected void onPostExecute(Address address) {
        if (mShouldUpdateAddress) {
            // When user selects a new map location, we want address data to be updated
            mListener.updateMarkerTitle(address, selectedLocation);
        } else {
            // Sometime we may not want marker title/address to be updated (like for current location)
            mListener.setCity(address.getLocality());
        }
    }

    private Address getAddress(double latitude, double longitude) {
        if (!Utils.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
            return null;
        }
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (addresses.size() > 0) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

}
