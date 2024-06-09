package com.example.apptrail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RegisterTrailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker usuario;
    private Location loc;
    private static final int REQUEST_LOCATION_UPDATES = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_trail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        startLocationUpdates();
        this.googleMap = googleMap;
        MarkerOptions user_markerOptions=new MarkerOptions();
        if(loc!=null) {
            user_markerOptions.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
        } else {
            user_markerOptions.position(new LatLng(0, 0));
        }
        user_markerOptions.title("Usuário");
        user_markerOptions.snippet("Estou aqui!");
        usuario=googleMap.addMarker(user_markerOptions);
    }

    public void startLocationUpdates() {
        if  (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationRequest = new LocationRequest.Builder(5*1000).build();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    if(location!=null) {
                        loc = location;
                        Log.d("Latitude", String.valueOf(loc.getLatitude()));
                        Log.d("Longitude", String.valueOf(loc.getLongitude()));

                        usuario.setPosition(new LatLng(loc.getLatitude(),loc.getLongitude()));
                        // centraliza a camera na posição do marcador mudando o zoom
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                                (new LatLng(loc.getLatitude(), loc.getLongitude()),15));
                    }
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,null);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_UPDATES);
        }
    }
}