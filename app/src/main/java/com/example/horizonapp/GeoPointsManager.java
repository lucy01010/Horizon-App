package com.example.horizonapp;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class GeoPointsManager {
    private static final String TAG = "GeoPointsManager";
    private FirebaseFirestore db;
    private CollectionReference geoPointsCollection;
    private Context context;
    private GoogleMap googleMap;
    private Map<String, Marker> markersMap;

    public GeoPointsManager(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        db = FirebaseFirestore.getInstance();
        geoPointsCollection = db.collection("geoPoints");
        markersMap = new HashMap<>();
    }

    public void addGeoPointToFirestore(LatLng location) {
        GeoPoint geoPoint = new GeoPoint(location.latitude, location.longitude);
        Map<String, Object> geoPointData = new HashMap<>();
        geoPointData.put("location", geoPoint);

        geoPointsCollection.add(geoPointData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "GeoPoint added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding GeoPoint", e));
    }

    public void addMarkersForGeoPoints() {
        geoPointsCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                GeoPoint geoPoint = document.getGeoPoint("location");
                if (geoPoint != null) {
                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    String documentId = document.getId();
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(documentId));
                    markersMap.put(documentId, marker);
                }
            }
        });
    }

    // Optionally, you can also add a method to remove markers from the map
    public void removeMarkers() {
        for (Marker marker : markersMap.values()) {
            marker.remove();
        }
        markersMap.clear();
    }
}
