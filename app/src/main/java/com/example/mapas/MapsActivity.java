package com.example.mapas;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener{

    private GoogleMap mMap;
    private Location location;
    private SupportMapFragment mapFragment;
    private LatLng latLng;
    private Geocoder geocoder;
    private final String TAG = getClass().getSimpleName();
    private Address address;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //this.checkGPSEnabled();
        getPermission();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //this.checkGPSEnabled();
    }

    private void checkGPSEnabled() {
        try{
            int gpsSignal = Settings.Secure.getInt(this.getContentResolver(),Settings.Secure.LOCATION_MODE);
            if(gpsSignal == 0){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getPermission();
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

            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPS = isGpsEnabled(this);
            boolean isNetwork = isNetworkEnabled(this);
            /*if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){




                if(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!=null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }*/
            if (location != null)
            {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
            //Request update as location manager can return null otherwise
            else
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            }
            setLatLng(lat,lon);
        }
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public static boolean isNetworkEnabled(Context context) {
        LocationManager lm = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
