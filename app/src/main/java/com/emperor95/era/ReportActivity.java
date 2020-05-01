package com.emperor95.era;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.emperor95.era.pojo.Reports;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private SupportMapFragment mapFragment;

    private Button btnReport;
    private TextInputEditText edtEmergency;
    
    private CollectionReference collectionReference;
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_report);

        collectionReference = FirebaseFirestore.getInstance().collection("REPORTS");
        
        btnReport = findViewById(R.id.button3);
        edtEmergency = findViewById(R.id.edtEmergency);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(ReportActivity.this);
        location();

        btnReport.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edtEmergency.getText().toString().trim())) {
                Reports reports = new Reports(
                        edtEmergency.getText().toString().trim(),
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                );
                collectionReference.document().set(reports)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ReportActivity.this, "Report sent We will respond to you soon", Toast.LENGTH_LONG).show();
                                    edtEmergency.setText("");
                                    finish();
                                } else {
                                    Toast.makeText(ReportActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Tell us about your emergency ...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrieveLoc();
//                    createLocationRequest();
                } else {
                    Toast.makeText(this, "Location permission is required.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(myLocation)
                .title("Your Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        // Move the camera instantly  with a zoom of 15.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);

    }

    private void setupMap(){
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void retrieveLoc(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(ReportActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentLocation = location;
                            setupMap();
                        }
                    }
                });
    }

    void location(){
        String[] permissions = new String[1];
        permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;

        int permission = ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        //request runtime permission on android 6+
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1000);
            }
        } else {
            retrieveLoc();
        }
    }

}
