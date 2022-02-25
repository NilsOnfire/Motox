package com.example.motox;

import androidx.annotation.NonNull;
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
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConductorMapAct extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location nLastLocation;
    LocationRequest nLocationRequest;

    private FusedLocationProviderClient nFusedLocationClient;
    private Button nLogout, nConfiguracion, nEstadoViaje, nHistorial;
    private int status = 0;
    private String pasajeroId = "", destino;
    private LatLng destinoLatLng, pickupLatLng;
    private float rideDistance;
    private Boolean isLoggingOut = false;
    private SupportMapFragment mapFragment;
    private LinearLayout nInfoPasajero;
    private ImageView nImagenPerfilPasajero;
    private TextView nNombrePasajero, nTelefonoPasajero, nDestinoPasajero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductor_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        nFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nLogout = findViewById(R.id.logout);
        nLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ConductorMapAct.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
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

    LocationCallback nLocationCallback = new LocationCallback(){
      public void onLocationResult(LocationResult locationResult){
          for(Location location : locationResult.getLocations()){
              if(getApplicationContext() != null){
                  if (!pasajeroId.equals("") && nLastLocation != null && location != null){
                      rideDistance += nLastLocation.distanceTo(location)/1000;
                  }

                  LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                  mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                  mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                  DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("conductorDisponible");
                  DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("conductorEnServicio");
                  GeoFire geoFireAvailable = new GeoFire(refAvailable);
                  GeoFire geoFireWorking = new GeoFire(refWorking);

                  switch (pasajeroId){
                      case "":
                          geoFireWorking.removeLocation(userId);
                          geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                          break;

                      default:
                          geoFireAvailable.removeLocation(userId);
                          geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                          break;
                  }
              }
          }
      }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this).setTitle("Dar Permiso").setMessage("Mensaje solicitud de permiso")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(ConductorMapAct.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        })
                    .create()
                    .show();
            }
            else{
                ActivityCompat.requestPermissions(ConductorMapAct.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        nFusedLocationClient.requestLocationUpdates(nLocationRequest, nLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Por favor proporciona el permiso ", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void conectarConductor(){
        checkLocationPermission();
        nFusedLocationClient.requestLocationUpdates(nLocationRequest, nLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    protected void desconectarConductor() {
        if(nFusedLocationClient != null){
            nFusedLocationClient.removeLocationUpdates(nLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

}