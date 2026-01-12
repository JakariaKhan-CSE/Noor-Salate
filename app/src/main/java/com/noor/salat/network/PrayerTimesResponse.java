package com.noor.salat.network;

import com.google.gson.annotations.SerializedName;
import com.noor.salat.model.PrayerTimesData;

public class PrayerTimesResponse {
    @SerializedName("code")
    public int code;
    @SerializedName("status")
    public String status;
    @SerializedName("data")
    public PrayerTimesData data;
}
