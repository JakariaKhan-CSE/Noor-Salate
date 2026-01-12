package com.noor.salat.ui.qibla;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.noor.salat.R;

public class QiblaFragment extends Fragment implements SensorEventListener {

    private ImageView compassDial;
    private ImageView qiblaNeedle;
    private TextView bearingText;
    private TextView qiblaStatus;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private float currentDegree = 0f;
    private float qiblaBearing = 0f;
    
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qibla, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compassDial = view.findViewById(R.id.compass_dial);
        qiblaNeedle = view.findViewById(R.id.qibla_needle);
        bearingText = view.findViewById(R.id.bearing_text);
        qiblaStatus = view.findViewById(R.id.qibla_status);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermission();
    }
    
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLocation();
                } else {
                    qiblaStatus.setText("Location required for Qibla");
                }
            });

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
    
    @SuppressLint("MissingPermission")
    private void getLocation() {
         fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        qiblaBearing = calculateQibla(location.getLatitude(), location.getLongitude());
                        qiblaStatus.setText("Location Found. Qibla: " + (int)qiblaBearing + "°");
                    } else {
                        qiblaStatus.setText("Location not found. Using Default.");
                        qiblaBearing = calculateQibla(23.8103, 90.4125); // Default
                    }
                });
    }

    private float calculateQibla(double lat, double lon) {
        double lat1 = Math.toRadians(lat);
        double lon1 = Math.toRadians(lon);
        double lat2 = Math.toRadians(21.422487); // Mecca Lat
        double lon2 = Math.toRadians(39.826206); // Mecca Lon

        double dLon = lon2 - lon1;
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.atan2(y, x);

        return (float) ((Math.toDegrees(bearing) + 360) % 360);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getContext(), "Sensors not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            float[] r = new float[9];
            float[] i = new float[9];
            if (SensorManager.getRotationMatrix(r, i, lastAccelerometer, lastMagnetometer)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(r, orientation);
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                updateCompass(azimuthInDegrees);
            }
        }
    }

    private void updateCompass(float azimuth) {
        // Rotate the DIAL so North is always pointing North relative to device
        // Example: If device faces East (90 deg), Dial should rotate -90 deg so 'N' is at West.
        
        RotateAnimation ra = new RotateAnimation(
                currentDegree, 
                -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, 
                Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(250);
        ra.setFillAfter(true);

        compassDial.startAnimation(ra);
        
        // Rotate the QIBLA NEEDLE
        // Needle should point to Qibla Bearing relative to North.
        // If device faces North (0), Needle point to `qiblaBearing`.
        // If device faces East (90), Needle should point to `qiblaBearing - 90`.
        // `compassDial` rotates by -azimuth.
        // If we attach `qiblaNeedle` to `compassDial` (visually or layout wise), it rotates with it.
        // But here `qiblaNeedle` is separate sibling.
        // So we rotate `qiblaNeedle` by `-azimuth + qiblaBearing`.
        
        RotateAnimation raQibla = new RotateAnimation(
                currentDegree + qiblaBearing, // This logic assumes 'currentDegree' tracks -azimuth mostly... wait.
                -azimuth + qiblaBearing,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(250);
        raQibla.setFillAfter(true);
        qiblaNeedle.startAnimation(raQibla);

        currentDegree = -azimuth;
        bearingText.setText((int)azimuth + "°");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
