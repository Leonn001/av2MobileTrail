package com.example.apptrail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;

public class RegisterTrailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker usuario;
    private Location loc;
    private static final int REQUEST_LOCATION_UPDATES = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    TrilhasDB trilhadb;
    private Button startbtn;
    private Button stopbtn;
    private TextView textView;
    private TextView timeTextView;
    private TextView speedTextView;
    int waypoint_counter;
    boolean isRecording = false;
    private Location lastLocation;
    private double totalDistance = 0.0;
    private long startTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_trail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);

        startbtn = findViewById(R.id.startbtn);
        stopbtn = findViewById(R.id.stopbtn);
        textView = findViewById(R.id.textView);
        timeTextView = findViewById(R.id.textView4);
        speedTextView = findViewById(R.id.textView6);

        // Inicializar os botões
        startbtn.setEnabled(true);
        stopbtn.setEnabled(false);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Setar listeners nos botões
        startbtn.setOnClickListener(v -> {
            trilhadb = new TrilhasDB(RegisterTrailActivity.this);
            trilhadb.apagarTrilha();
            waypoint_counter = 0;
            totalDistance = 0.0;
            lastLocation = null;
            textView.setText("0.0 m");
            timeTextView.setText("0 s");
            speedTextView.setText("0.0 m/s");
            startTime = System.currentTimeMillis();
            isRecording = true;
            startbtn.setEnabled(false); // Desabilitar botão start ao iniciar
            stopbtn.setEnabled(true); // Habilitar botão stop
            handler.postDelayed(timerRunnable, 1000);
        });

        stopbtn.setOnClickListener(v -> {
            isRecording = false;
            stopLocationUpdates();
            handler.removeCallbacks(timerRunnable);

            if (trilhadb != null) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // em segundos
                double averageSpeed = totalDistance / elapsedTime; // velocidade média em m/s

                // Solicitar título ao usuário
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save trail");
                builder.setMessage("Enter the track title:");

                final EditText input = new EditText(this);
                builder.setView(input);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String title = input.getText().toString();

                    trilhadb.addTrail(title, totalDistance, elapsedTime, averageSpeed);
                    trilhadb.close();

                    Toast.makeText(RegisterTrailActivity.this, "Trail saved successfully!", Toast.LENGTH_SHORT).show();

                    startbtn.setEnabled(true); // Habilitar botão start ao parar
                    stopbtn.setEnabled(false); // Desabilitar botão stop
                });

                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                builder.show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        MarkerOptions user_markerOptions = new MarkerOptions();
        user_markerOptions.position(new LatLng(0, 0));
        user_markerOptions.title("User");
        user_markerOptions.snippet("I am here!");
        usuario = googleMap.addMarker(user_markerOptions);
        startLocationUpdates();
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = LocationRequest.create()
                    .setInterval(5000)
                    .setFastestInterval(2000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        Log.d("LocationResult", "Location result is null");
                        return;
                    }
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        loc = location;
                        Log.d("Latitude", String.valueOf(loc.getLatitude()));
                        Log.d("Longitude", String.valueOf(loc.getLongitude()));
                        usuario.setPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(loc.getLatitude(), loc.getLongitude()), 15));
                        if (isRecording && trilhadb != null) {
                            addWayPoint(loc);
                            updateDistanceAndSpeed(loc);
                        }
                    } else {
                        Log.d("Location", "Location is null");
                    }
                }
            };

            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_UPDATES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Log.d("Permission", "Location permission denied");
            }
        }
    }

    public void stopLocationUpdates() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void addWayPoint(Location location) {
        if (location != null && trilhadb != null) {
            Waypoint waypoint = new Waypoint(location);
            trilhadb.registrarWaypoint(waypoint);
            waypoint_counter++;
        } else {
            Log.d("addWayPoint", "Location or trilhadb is null, cannot add waypoint");
        }
    }

    private void updateDistanceAndSpeed(Location newLocation) {
        if (lastLocation != null) {
            double distance = DistanceCalculator.calculateDistance(lastLocation, newLocation);
            totalDistance += distance;
            textView.setText(String.format("%.2f m", totalDistance));

            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // em segundos
            timeTextView.setText(String.format("%d s", elapsedTime));

            double speed = distance / 5; // distância em metros por intervalo (5 segundos)
            speedTextView.setText(String.format("%.2f m/s", speed));
        }
        lastLocation = newLocation;
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // em segundos
            timeTextView.setText(String.format("%d s", elapsedTime));
            handler.postDelayed(this, 1000);
        }
    };
}
