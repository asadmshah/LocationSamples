package com.asadmshah.locationsamples;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;


public class ActivityDetectionIntentService extends IntentService {

    public static final String TAG = ActivityDetectionIntentService.class.getSimpleName();

    public static final String RESULTS_BROADCAST_ACTION = "com.asadmshah.locationsamples.RESULTS_BROADCAST_ACTION";

    public static final String RESULTS_EXTRA_DETECTED_ACTIVITIES = "detected_activities";

    public ActivityDetectionIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        ArrayList<DetectedActivity> detectedActivities = (ArrayList<DetectedActivity>) result.getProbableActivities();
        broadcastResult(detectedActivities);
    }

    private void broadcastResult(ArrayList<DetectedActivity> activities) {
        Intent intent = new Intent(RESULTS_BROADCAST_ACTION);
        intent.putExtra(RESULTS_EXTRA_DETECTED_ACTIVITIES, activities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
