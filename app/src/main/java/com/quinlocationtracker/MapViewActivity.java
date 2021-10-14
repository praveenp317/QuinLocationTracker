package com.quinlocationtracker;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.quinlocationtracker.databinding.ActivityMapViewBinding;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapViewBinding binding;

    //Get Current location global accessible if needed
    private LatLng mCurrentLocation, mFirstLocation;

    boolean isCurrentLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Receives intent from LocationService
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO receives current location updates from loactionService.class
            // Get extra data included in the Intent
            //   Toast.makeText(DashboardActivity.this, "BROADCAST", Toast.LENGTH_SHORT).show();
            Log.e("receiver", "success");
            //Latitude //Longitude

            if (mCurrentLocation == null){
                isCurrentLocation = true;
            } else {
                isCurrentLocation = false;
            }

            Double Latitude = intent.getDoubleExtra("Latitude", 0);
            Double Longitude = intent.getDoubleExtra("Longitude", 0);
            mCurrentLocation = new LatLng(Latitude, Longitude);

            if (isCurrentLocation){
                mFirstLocation = mCurrentLocation;
            }

            //Clear Add a marker and move the camera
            // mMap.clear();

            LatLng position = new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude);
            mMap.addMarker(new MarkerOptions().position(position).title("current location"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            LatLngBounds bounds = new LatLngBounds.Builder().include(position).build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));

            //TODO Add marker image
            mMap.addPolyline((new PolylineOptions())
                    .add(mFirstLocation, mCurrentLocation));

            mFirstLocation = mCurrentLocation;

            //new MarkerAnimationHelper(mMap).animateMarker(marker, mCurrentLocation.latitude, mCurrentLocation.longitude);

            //Toast.makeText(MainActivity.this, "fetching data in background", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);

        IntentFilter filter = new IntentFilter(MyLocationIntentService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                filter);


    }
}