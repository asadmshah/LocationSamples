package com.asadmshah.locationsamples.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.SeekBar;

import com.asadmshah.locationsamples.R;
import com.asadmshah.locationsamples.services.GeofenceTransitionIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeofencingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, SeekBar.OnSeekBarChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public static final String TAG = GeofencingActivity.class.getSimpleName();

    private SeekBar mSeekBar;
    private MapFragment mMapFragment;

    private String mMarkerTitle;
    private int mCircleColor;

    private LatLng mCurrentCoordinates;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofencing);

        mMarkerTitle = getString(R.string.geofence_title);
        mCircleColor = getResources().getColor(R.color.geofence_fill_color);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(this);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mGoogleApiClient = buildGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeGeofences();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mCurrentCoordinates = latLng;
        removeGeofences();
        GoogleMap map = mMapFragment.getMap();
        if (map != null) {
            clearMap(map);
            addMarker(map, latLng);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        removeGeofences();
        GoogleMap map = mMapFragment.getMap();
        if (map != null) {
            clearMap(map);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mCurrentCoordinates != null && seekBar.getProgress() > 5) {
            GoogleMap map = mMapFragment.getMap();
            if (map != null) {
                clearMap(map);
                addMarker(map, mCurrentCoordinates);
                int radius = seekBar.getProgress() * 5;
                addCircle(map, mCurrentCoordinates, radius);
                addGeofence(mCurrentCoordinates, radius);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSeekBar.setEnabled(true);
        mMapFragment.getMap().setOnMapClickListener(this);
        mMapFragment.getMap().setOnMapLongClickListener(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onResult(Status status) {
        Log.i(TAG, "onResult: " + status.getStatusMessage());
    }

    private void clearMap(GoogleMap map) {
        map.clear();
    }

    private void addMarker(GoogleMap map, LatLng latLng) {
        map.addMarker(new MarkerOptions().position(latLng).title(mMarkerTitle));
    }

    private void addCircle(GoogleMap map, LatLng latLng, int radius) {
        map.addCircle(new CircleOptions().center(latLng).radius(radius).fillColor(mCircleColor));
    }

    private void addGeofence(LatLng latLng, int radius) {
        Geofence geofence = buildGeofence(latLng, radius);
        GeofencingRequest request = buildGeofencingRequest(geofence);
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, request, buildGeofencePendingIntent());
    }

    private void removeGeofences() {
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, buildGeofencePendingIntent());
    }

    private Geofence buildGeofence(LatLng latLng, int radius) {
        return new Geofence.Builder()
                .setRequestId(latLng.toString())
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(1000 * 60)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest buildGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent buildGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

}
