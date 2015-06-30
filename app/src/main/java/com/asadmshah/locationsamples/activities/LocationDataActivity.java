package com.asadmshah.locationsamples.activities;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.asadmshah.locationsamples.R;
import com.asadmshah.locationsamples.services.GeocoderIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;

public class LocationDataActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = LocationDataActivity.class.getSimpleName();

    private static final DecimalFormat COORDINATES_FORMAT = new DecimalFormat("#.##");

    private TextView mViewLatitude;
    private TextView mViewLongitude;
    private TextView mViewCity;
    private TextView mViewCountry;

    private GeocodingResultsReceiver mResultsReceiver;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_data);

        mResultsReceiver = new GeocodingResultsReceiver(new Handler());

        mViewLatitude = (TextView) findViewById(R.id.latitude);
        mViewLongitude = (TextView) findViewById(R.id.longitude);
        mViewCity = (TextView) findViewById(R.id.city);
        mViewCountry = (TextView) findViewById(R.id.country);

        mLocationRequest = buildLocationRequest();
        mGoogleApiClient = buildGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mViewLatitude.setText(COORDINATES_FORMAT.format(latitude));
        mViewLongitude.setText(COORDINATES_FORMAT.format(longitude));
        GeocoderIntentService.startGeocoderIntentService(this, latitude, longitude, mResultsReceiver);
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private LocationRequest buildLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(1000 * 10);
        request.setFastestInterval(1000 * 5);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    private class GeocodingResultsReceiver extends ResultReceiver {

        public GeocodingResultsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == GeocoderIntentService.RESULT_CODE_SUCCESS) {
                mViewCity.setText(resultData.getString(GeocoderIntentService.RESULT_EXTRA_CITY));
                mViewCountry.setText(resultData.getString(GeocoderIntentService.RESULT_EXTRA_COUNTRY));
            } else {
                String message = resultData.getString(GeocoderIntentService.RESULT_EXTRA_ERROR_MESSAGE);
                Toast.makeText(LocationDataActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
