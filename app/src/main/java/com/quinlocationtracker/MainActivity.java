package com.quinlocationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.quinlocationtracker.data.db.DatabaseClient;
import com.quinlocationtracker.data.model.LocationTaskModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private RecyclerView mLRecyclerView;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate the UI widgets.
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);

        mLRecyclerView = findViewById(R.id.recyclerView);
        mLRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRequestingLocationUpdates = false;

        requestPermissions();

        Button maps_button = (Button) findViewById(R.id.maps_button);
        maps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRequestingLocationUpdates){
                    Intent intent = new Intent(MainActivity.this,MapViewActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,"Please Start Location",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            // startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        IntentFilter filter = new IntentFilter(MyLocationIntentService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                filter);

    }



    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    Intent myService;
    public void startUpdatesButtonHandler(View view) {

        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            //startLocationUpdates();
            myService = new Intent(MainActivity.this, MyLocationIntentService.class);
            myService.putExtra("state", "ON");
            startService(myService);
        } else {
            Toast.makeText(this,"Location is On",Toast.LENGTH_SHORT).show();
        }

           /* if (checkPermissions()){
                //locate current location and update in background

            } else if (!checkPermissions()) {
                requestPermissions();

        }*/
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("TAG", "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions( MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i("TAG", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions( MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    //Receives intent from LocationService
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("receiver", "success");

            if (mRequestingLocationUpdates){
                updateUI();
            }
        }
    };

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        setButtonsEnabledState();
        fetchLocationData();
    }

    private void fetchLocationData() {
        class GetTasks extends AsyncTask<Void, Void, List<LocationTaskModel>> {

            @Override
            protected List<LocationTaskModel> doInBackground(Void... voids) {
                List<LocationTaskModel> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<LocationTaskModel> tasks) {
                super.onPostExecute(tasks);
                LocationAdapter adapter = new LocationAdapter(MainActivity.this, tasks);
                recyclerViewState = mLRecyclerView.getLayoutManager().onSaveInstanceState();
                mLRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                mLRecyclerView.setAdapter(adapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */
    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        //stopLocationUpdates();

        mRequestingLocationUpdates = false;
        setButtonsEnabledState();

        try {
            stopService(myService);
            unregisterReceiver(mMessageReceiver);
        } catch (Exception e){
            //exception
        }

    }
}