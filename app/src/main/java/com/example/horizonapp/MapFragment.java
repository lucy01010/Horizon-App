package com.example.horizonapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton addBtn = view.findViewById(R.id.fab_add_marker);
        mapSearchView = view.findViewById(R.id.mapSearch);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLastLocation();

        addBtn.setOnClickListener(v->{
            Intent intent = new Intent(requireActivity(), AddActivity.class);
            startActivity(intent);
        });
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } else {
                        Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return view;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        } else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;

                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(MapFragment.this);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng myloc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        googleMap.addMarker(new MarkerOptions().position(myloc).title("My Location").icon(BitmapDescriptorFactory.defaultMarker()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 12));



        BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.church_marker_icon);
        BitmapDescriptor customMarkerIcon2 = BitmapDescriptorFactory.fromResource(R.drawable.museum_icon);
        BitmapDescriptor customMarkerIcon3 = BitmapDescriptorFactory.fromResource(R.drawable.castle_icon);

        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.church_marker_icon);
        Bitmap markerBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.museum_icon);
        Bitmap markerBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.castle_icon);


        float scaledWidth = markerBitmap.getWidth() * 0.0625f;
        float scaledHeight = markerBitmap.getHeight() * 0.0625f;

        customMarkerIcon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(markerBitmap, (int) scaledWidth, (int) scaledHeight, false));
        customMarkerIcon2 = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(markerBitmap2, (int) scaledWidth, (int) scaledHeight, false));
        customMarkerIcon3 = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(markerBitmap3, (int) scaledWidth, (int) scaledHeight, false));

        //churches
        LatLng haghartsinMonastery = new LatLng(40.77614, 44.96847);
        LatLng saintHovhannesChurch  = new LatLng(40.18532,44.50372);
        LatLng goshavank = new LatLng(40.729878,44.997725);
        LatLng makaravank = new LatLng(40.973819,45.127233);

        //museums
        LatLng historyMuseum = new LatLng(40.1743,44.5035);
        LatLng hovhannesTumanyanMuseum = new LatLng(40.1878, 44.5099);
        LatLng matenadaran = new LatLng(40.19207, 44.52113);

        //Fortress
        LatLng amberd = new LatLng(40.3887,44.2260);
        LatLng smbataBerd = new LatLng(	39.871736, 45.338113);
        LatLng bagaberd = new LatLng(40.19207, 44.52113);


        googleMap.addMarker(new MarkerOptions().position(haghartsinMonastery).title("Haghartsin Monastery").icon(customMarkerIcon));
        googleMap.addMarker(new MarkerOptions().position( saintHovhannesChurch ).title(" Saint Hovhannes Church ").icon(customMarkerIcon));
        googleMap.addMarker(new MarkerOptions().position( goshavank).title(" Goshavank Monastery ").icon(customMarkerIcon));
        googleMap.addMarker(new MarkerOptions().position( makaravank).title(" Makaravank Monastery Complex ").icon(customMarkerIcon));

        googleMap.addMarker(new MarkerOptions().position( historyMuseum).title("History Museum").icon(customMarkerIcon2));
        googleMap.addMarker(new MarkerOptions().position( hovhannesTumanyanMuseum).title("Hovhannes Tumanyan Museum").icon(customMarkerIcon2));
        googleMap.addMarker(new MarkerOptions().position( matenadaran).title("Mesrop Mashtots Matenadara ").icon(customMarkerIcon2));

        googleMap.addMarker(new MarkerOptions().position(amberd).title("Amberd fortress ").icon(customMarkerIcon3));
        googleMap.addMarker(new MarkerOptions().position(smbataBerd).title("Smbataberd Fortress ").icon(customMarkerIcon3));


        GeoPointsManager geoPointsManager = new GeoPointsManager(requireContext(), googleMap);
        geoPointsManager.addMarkersForGeoPoints();
    }

    }



