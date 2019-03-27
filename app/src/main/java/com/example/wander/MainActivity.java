package com.example.wander;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void updateLocationInfo(Location location) {
        Log.i("Location info: ", location.toString());

        TextView latitudeTextView = findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = findViewById(R.id.longitudeTextView);
        TextView accTextView = findViewById(R.id.accTextView);
        TextView altTextView = findViewById(R.id.altTextView);
        TextView addTextView = findViewById(R.id.addTextView);

        latitudeTextView.setText(getString(R.string.latitude, location.getLatitude()));
        longitudeTextView.setText(getString(R.string.longitude, location.getLongitude()));
        accTextView.setText(getString(R.string.accuracy, location.getAccuracy()));
        altTextView.setText(getString(R.string.altitude, location.getAltitude()));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addTextView.setText(getString(R.string.address, "Could Not Find Address."));
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList != null && addressList.size() > 0) {
                Log.i("Place Info: ", addressList.get(0).toString());

                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }

                if(address.getSubThoroughfare() != null){
                    sb.append(address.getSubThoroughfare()).append("\n");
                }
                if(address.getThoroughfare() != null) {
                    sb.append(address.getThoroughfare()).append("\n");
                }
                if(address.getLocality() != null) {
                    sb.append(address.getLocality()).append("\n");
                }
                if(address.getPostalCode() != null) {
                    sb.append(address.getPostalCode()).append("\n");
                }
                if(address.getCountryCode() != null) {
                    sb.append(address.getCountryName());
                }
                addTextView.setText(getString(R.string.address, sb));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
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
        };

        //if(Build.VERSION.SDK_INT < 23){
        //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //}
        //else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    updateLocationInfo(location);
                }
            }
        //}
    }
}
