package com.example.wilder.winstateapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation = null;
    Location mLocationUser;
    FusedLocationProviderClient mFusedLocationClient;
    boolean mIsWaitingForGoogleMap = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        askLocationPermission();

    }

    /**
     * Permissions
     **/

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // personne a déjà refusé

            } else {

                // on ne lui a pas encore posé la question
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        6996);

                // demande les droits à l'utilisateur
            }
        } else {
            // on a déjà le droit !
            getLocation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 6996: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // il a accepté, charger la position de la personne

                    getLocation();

                } else {

                    Toast.makeText(this, "Vous avez refusé la geolocalisation", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void moveCameraOnUser(Location location) {

        if (mMap == null) {
            mIsWaitingForGoogleMap = true;
            mLastLocation = location;
        } else if (location != null) {

            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(userLocation, 17);


            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
            mMap.animateCamera(yourLocation);
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.6014536, 1.4421452000000272), 10));
        }
    }

    private void getLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // mettre à jour la position de l'utilisateur
                moveCameraOnUser(location);
                mLocationUser = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Si l'utilisateur à permis l'utilisation de la localisation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            moveCameraOnUser(location);
                        }
                    });

            // Si l'utilisateur n'a pas désactivé la localisation du téléphone
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // demande la position de l'utilisateur
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5, locationListener);
            } else {
                Toast.makeText(this, "Geoloc Off", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mIsWaitingForGoogleMap) {
            moveCameraOnUser(mLastLocation);
        }


        Resources ressource = getApplicationContext().getResources();
        int widthDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, ressource.getDisplayMetrics());
        int heightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, ressource.getDisplayMetrics());

        BitmapDescriptor iconWinNews = BitmapDescriptorFactory.fromResource(R.drawable.winnews);
        BitmapDescriptor iconWinNewsJaune = BitmapDescriptorFactory.fromResource(R.drawable.winnews_jaune);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.741895, -73.989308))
                .title("Cancer")
                .icon(iconWinNews));
    }

}

