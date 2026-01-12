package com.noor.salat.api;

import com.noor.salat.models.PrayerTimesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanService {
    @GET("timings")
    Call<PrayerTimesResponse> getPrayerTimes(
        @Query("latitude") double latitude,
        @Query("longitude") double longitude,
        @Query("method") int method
    );
}
