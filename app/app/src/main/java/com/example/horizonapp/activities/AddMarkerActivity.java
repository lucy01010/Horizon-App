package com.example.horizonapp.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;  // Import for logging
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.horizonapp.GeoPointsManager;
import com.example.horizonapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "AddMarkerActivity";
    private GoogleMap map;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;

        this.map.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();  // Remove the previous marker
            }

            selectedMarker = this.map.addMarker(new MarkerOptions().position(latLng));

            // Retrieve the address information
            new Thread(() -> {
                GeoPointsManager geoPointsManager = new GeoPointsManager(getApplicationContext(), map);
                geoPointsManager.addGeoPointToFirestore(latLng);

                String addressDetails = getAddressFromLocation(latLng.latitude, latLng.longitude);
                if (addressDetails != null) {
                    Log.d(TAG, "Selected location's address: " + addressDetails);

                    // Use a handler to switch back to the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        // Set the result with the address details
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("LOCATION_ADDRESS", addressDetails);
                        setResult(RESULT_OK, resultIntent);  // Set result code and return data
                        finish();  // End activity to return to AddFragment
                    });
                } else {
                    Log.d(TAG, "Could not retrieve address for the selected location.");
                }
            }).start();
        });
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String addressLine = address.getAddressLine(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();
                String postalCode = address.getPostalCode();

                return addressLine + ", " + city + ", " + country;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while retrieving address: " + e.getMessage());
        }
        return null;
    }
}
