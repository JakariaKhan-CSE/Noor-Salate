package com.noor.salat.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.noor.salat.model.PrayerTimesData;
import com.noor.salat.network.PrayerTimesResponse;
import com.noor.salat.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TextView locationText;
    private TextView dateText;
    private TextView countdownTimer;
    private TextView nextPrayerName;
    private TextView nextPrayerTime;
    private LinearLayout prayersContainer;
    private FusedLocationProviderClient fusedLocationClient;
    private CountDownTimer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationText = view.findViewById(R.id.location_text);
        dateText = view.findViewById(R.id.date_text);
        countdownTimer = view.findViewById(R.id.countdown_timer);
        nextPrayerName = view.findViewById(R.id.next_prayer_name);
        nextPrayerTime = view.findViewById(R.id.next_prayer_time);
        prayersContainer = view.findViewById(R.id.prayers_container);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        checkLocationPermission();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getLocationAndFetchTimes();
                } else {
                    locationText.setText("Default: Dhaka");
                    fetchPrayerTimes(23.8103, 90.4125); // Fallback to Dhaka
                }
            });

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocationAndFetchTimes();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndFetchTimes() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        locationText.setText("Lat: " + String.format("%.2f", location.getLatitude()) + ", Lon: " + String.format("%.2f", location.getLongitude()));
                        fetchPrayerTimes(location.getLatitude(), location.getLongitude());
                    } else {
                        locationText.setText("Location not found, using Default");
                        fetchPrayerTimes(23.8103, 90.4125);
                    }
                });
    }

    private void fetchPrayerTimes(double latitude, double longitude) {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("NoorSalatPrefs", android.content.Context.MODE_PRIVATE);
        int method = prefs.getInt("calc_method", 2);
        int school = prefs.getInt("madhab", 0); // 0 or 1
        
        RetrofitClient.getService().getTimings(latitude, longitude, method, school)
                .enqueue(new Callback<PrayerTimesResponse>() {
                    @Override
                    public void onResponse(Call<PrayerTimesResponse> call, Response<PrayerTimesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            updateUI(response.body().data);
                        } else {
                            Toast.makeText(getContext(), "Failed to load times", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PrayerTimesResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(PrayerTimesData data) {
        dateText.setText(data.date.readable + " | " + data.date.hijri.day + " " + data.date.hijri.month.en + " " + data.date.hijri.year);

        prayersContainer.removeAllViews();
        Map<String, String> timings = new TreeMap<>(); // Sorted manually later
        timings.put("Fajr", data.timings.fajr);
        timings.put("Sunrise", data.timings.sunrise);
        timings.put("Dhuhr", data.timings.dhuhr);
        timings.put("Asr", data.timings.asr);
        timings.put("Maghrib", data.timings.maghrib);
        timings.put("Isha", data.timings.isha);

        String[] order = {"Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"};

        long nextPrayerMillis = -1;
        String nextPrayer = "";
        String nextPrayerTimeStr = "";
        long now = System.currentTimeMillis();

        for (String name : order) {
            String time = timings.get(name);
            addPrayerItem(name, time);

            long prayerMillis = getPrayerTimeMillis(time);
            if (prayerMillis > now && nextPrayerMillis == -1) {
                nextPrayerMillis = prayerMillis;
                nextPrayer = name;
                nextPrayerTimeStr = time;
            }
        }
        
        // Handle case where next prayer is tomorrow Fajr (simplified: just show Fajr for now)
        if (nextPrayerMillis == -1) {
             nextPrayer = "Fajr";
             nextPrayerTimeStr = timings.get("Fajr");
             // Add 24 hours to prayerMillis for tomorrow
             nextPrayerMillis = getPrayerTimeMillis(nextPrayerTimeStr) + 86400000; 
        }

        nextPrayerName.setText(getNextPrayerLabel(nextPrayer)); 
        nextPrayerTime.setText(convertTo12Hour(nextPrayerTimeStr));
        startCountdown(nextPrayerMillis);
    }
    
    private String getNextPrayerLabel(String prayer) {
        return "Next: " + prayer;
    }

    private void addPrayerItem(String name, String time) {
        View view = getLayoutInflater().inflate(R.layout.item_prayer_time, prayersContainer, false);
        TextView nameView = view.findViewById(R.id.prayer_name);
        TextView timeView = view.findViewById(R.id.prayer_time);

        nameView.setText(name);
        timeView.setText(convertTo12Hour(time));

        prayersContainer.addView(view);
    }

    private void startCountdown(long targetMillis) {
        if (timer != null) timer.cancel();
        
        timer = new CountDownTimer(targetMillis - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / 3600000;
                long minutes = (millisUntilFinished % 3600000) / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                countdownTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                countdownTimer.setText("00:00:00");
                getLocationAndFetchTimes(); // Refresh
            }
        }.start();
    }

    private long getPrayerTimeMillis(String timeStr) {
        try {
            // Remove timezone part usually like "05:00 (EST)" -> "05:00"
            String cleanTime = timeStr.split(" ")[0];
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = format.parse(cleanTime);
            
            // Set to today
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            java.util.Calendar timeCal = java.util.Calendar.getInstance();
            timeCal.setTime(date);
            
            calendar.set(java.util.Calendar.HOUR_OF_DAY, timeCal.get(java.util.Calendar.HOUR_OF_DAY));
            calendar.set(java.util.Calendar.MINUTE, timeCal.get(java.util.Calendar.MINUTE));
            calendar.set(java.util.Calendar.SECOND, 0);
            
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private String convertTo12Hour(String timeStr) {
         try {
            String cleanTime = timeStr.split(" ")[0];
            SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat outFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return outFormat.format(inFormat.parse(cleanTime));
        } catch (Exception e) {
            return timeStr;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) timer.cancel();
    }
}
