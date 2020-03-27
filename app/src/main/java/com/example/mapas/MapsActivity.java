package com.example.mapas;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location location;
    private SupportMapFragment mapFragment;
    private LatLng latLng;
    private Geocoder geocoder;
    private final String TAG = getClass().getSimpleName();
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getPermission();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(latLng));
/*
        CameraPosition camera = new CameraPosition.Builder()
                .target(sevilla)
                .zoom(15)
                .bearing(90)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));

        geocoder = new Geocoder(this, Locale.getDefault());
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "Marker " + marker.getId() + " Drag@" + marker.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.d(TAG,"Marker " + marker.getId() + " DragEnd" + " Drag@" + marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                double latitude = marker.getPosition().latitude;
                double longitude = marker.getPosition().longitude;

                try{
                    address = (Address) geocoder.getFromLocation(latitude,longitude,1);
                }catch (IOException e){

                }

            }
        });

    }

    private void getPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    125);

            return;
        }else{

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                if(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!=null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            setLatLng(location.getLatitude(),location.getLongitude());
            mapFragment.getMapAsync(this);
        }
    }

    private LatLng setLatLng(double latitude, double longitude){
        latLng = new LatLng(latitude,longitude);
        return latLng;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 125: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Aceptado", Toast.LENGTH_LONG).show();
                    getPermission();
                } else {
                    Toast.makeText(this,"Permiso a la ", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
