package com.example.motox;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import com.example.motox.databinding.ActivityConductorMapBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.motox.databinding.ActivityPasajeroMapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PasajeroMapAct extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location nLastLocation;
    LocationRequest nLocationRequest;

    private FusedLocationProviderClient nFusedLocationClient;
    private Button nLogout, nSolicitar, nConfiguracion, nEstadoViaje, nHistorial;
    private LatLng pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasajero_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        nFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        nLogout = findViewById(R.id.logout);
        nLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PasajeroMapAct.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        });

        nSolicitar = findViewById(R.id.solicitar);
        nSolicitar.setOnClickListener(v ->{
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("solicitudPasajero");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userId, new GeoLocation(nLastLocation.getLatitude(), nLastLocation.getLongitude()));

            pickupLocation = new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Recoger Aquí"));

            nSolicitar.setText("Buscando a tu conductor...");
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        nLocationRequest = LocationRequest.create();
        nLocationRequest.setInterval(1000);
        nLocationRequest.setFastestInterval(1000);
        nLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Dar Permiso").setMessage("Mensaje solicitud de permiso")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(PasajeroMapAct.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(PasajeroMapAct.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

}