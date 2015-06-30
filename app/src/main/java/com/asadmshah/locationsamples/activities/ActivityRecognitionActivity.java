package com.asadmshah.locationsamples.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.asadmshah.locationsamples.R;
import com.asadmshah.locationsamples.services.ActivityDetectionIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityRecognitionActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    public static final String TAG = ActivityRecognitionActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_recognition);
        mListView = (ListView) findViewById(R.id.listview);
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
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ActivityDetectionIntentService.RESULTS_BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            PendingIntent intent = getActivityDetectionPendingIntent();
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, intent);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        PendingIntent intent = getActivityDetectionPendingIntent();
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 1000, intent)
                .setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
        PendingIntent intent = getActivityDetectionPendingIntent();
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, R.string.activity_detection_started, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Activity detection ended: " + status.getStatusMessage());
        }
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityDetectionIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> activities = intent.getParcelableArrayListExtra(ActivityDetectionIntentService.RESULTS_EXTRA_DETECTED_ACTIVITIES);
            ArrayList<String> results = new ArrayList<>(activities.size());

            String activityFormat = getString(R.string.format_activity_name_and_change);
            for (DetectedActivity activity : activities) {
                results.add(String.format(activityFormat, getActivityName(activity.getType()), activity.getConfidence()));
            }

            mListView.setAdapter(new ArrayAdapter<>(ActivityRecognitionActivity.this, android.R.layout.simple_list_item_1, results));
        }

        private String getActivityName(int activityCode) {
            int resourceId;
            switch (activityCode) {
                case DetectedActivity.IN_VEHICLE:
                    resourceId = R.string.in_vehicle;
                    break;
                case DetectedActivity.ON_BICYCLE:
                    resourceId = R.string.on_bicycle;
                    break;
                case DetectedActivity.ON_FOOT:
                    resourceId = R.string.on_foot;
                    break;
                case DetectedActivity.RUNNING:
                    resourceId = R.string.running;
                    break;
                case DetectedActivity.STILL:
                    resourceId = R.string.still;
                    break;
                case DetectedActivity.TILTING:
                    resourceId = R.string.tilting;
                    break;
                case DetectedActivity.WALKING:
                    resourceId = R.string.walking;
                    break;
                default:
                    resourceId = R.string.unknown;
                    break;
            }
            return getString(resourceId);
        }
    };

}
