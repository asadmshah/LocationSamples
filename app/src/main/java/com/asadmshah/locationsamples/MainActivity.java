package com.asadmshah.locationsamples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        findViewById(R.id.button_location).setOnClickListener(this);
        findViewById(R.id.button_activity_recognition).setOnClickListener(this);
        findViewById(R.id.button_geofencing).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_location:
                intent = new Intent(this, LocationDataActivity.class);
                break;
            case R.id.button_activity_recognition:
                intent = new Intent(this, ActivityRecognitionActivity.class);
                break;
            case R.id.button_geofencing:
                intent = new Intent(this, GeofencingActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

}
