package com.noor.salat.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanService {
    @GET("v1/timings")
    Call<PrayerTimesResponse> getTimings(
        @Query("latitude") double latitude,
        @Query("longitude") double longitude,
        @Query("method") int method,
        @Query("school") int school
    );
    
    @GET("v1/timingsByCity")
    Call<PrayerTimesResponse> getTimingsByCity(
        @Query("city") String city,
        @Query("country") String country,
        @Query("method") int method
    );
}
