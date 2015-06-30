package com.asadmshah.locationsamples.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.asadmshah.locationsamples.R;

import java.util.List;
import java.util.Locale;

public class GeocoderIntentService extends IntentService {

    public static final String TAG = GeocoderIntentService.class.getSimpleName();

    private static final String ACTION_GEOCODE = "com.asadmshah.locationsamples.action.GEOCODE";

    private static final String EXTRA_LATITUDE = "latitude";
    private static final String EXTRA_LONGITUDE = "longitude";
    private static final String EXTRA_RECEIVER = "receiver";

    public static final int RESULT_CODE_FAILURE = 0;
    public static final int RESULT_CODE_SUCCESS = 1;

    public static final String RESULT_EXTRA_CITY = "city";
    public static final String RESULT_EXTRA_COUNTRY = "country";
    public static final String RESULT_EXTRA_ERROR_MESSAGE = "failed";

    public GeocoderIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GEOCODE.equals(action)) {
                ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_RECEIVER);
                double lat = intent.getDoubleExtra(EXTRA_LATITUDE, 0);
                double lon = intent.getDoubleExtra(EXTRA_LONGITUDE, 0);
                handleLocation(lat, lon, resultReceiver);
            }
        }
    }

    private void handleLocation(double latitude, double longitude, ResultReceiver resultReceiver) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        if (addressList == null || addressList.size() == 0) {
            deliverFailedResultToReceiver(resultReceiver);
        } else {
            Address address = addressList.get(0);
            String city = address.getLocality();
            String country = address.getCountryName();
            deliverSuccessfulResultToReceiver(city, country, resultReceiver);
        }
    }

    private void deliverSuccessfulResultToReceiver(String city, String country, ResultReceiver resultReceiver) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_EXTRA_CITY, city);
        bundle.putString(RESULT_EXTRA_COUNTRY, country);
        resultReceiver.send(RESULT_CODE_SUCCESS, bundle);
    }

    private void deliverFailedResultToReceiver(ResultReceiver resultReceiver) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_EXTRA_ERROR_MESSAGE, getString(R.string.no_location_found));
        resultReceiver.send(RESULT_CODE_FAILURE, bundle);
    }

    public static void startGeocoderIntentService(Context context, double lat, double lon, ResultReceiver receiver) {
        Intent intent = new Intent(context, GeocoderIntentService.class);
        intent.setAction(ACTION_GEOCODE);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lon);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

}
