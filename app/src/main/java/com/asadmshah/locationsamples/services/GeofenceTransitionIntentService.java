package com.asadmshah.locationsamples.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.asadmshah.locationsamples.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeofenceTransitionIntentService extends IntentService {

    public static final String TAG = GeofenceTransitionIntentService.class.getSimpleName();

    public static final String ACTION_FOO = "com.asadmshah.locationsamples.services.action.GEOFENCE_TRANSITION";

    private static final int NOTIFICATION_ID = 298343;

    public GeofenceTransitionIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (!event.hasError()) {
            String transition;
            if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
                transition = getString(R.string.geofence_entered);
            } else {
                transition = getString(R.string.geofence_exited);
            }

            List<String> idsList = new ArrayList<>();
            for (Geofence geofence : event.getTriggeringGeofences()) {
                idsList.add(geofence.getRequestId());
            }
            String idsString = TextUtils.join(", ", idsList);
            buildNotification(getString(R.string.geofence_notification_text, transition, idsString));
        }
    }

    private void buildNotification(String contentText) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.geofence_notification_title))
                .setContentText(contentText);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

}
