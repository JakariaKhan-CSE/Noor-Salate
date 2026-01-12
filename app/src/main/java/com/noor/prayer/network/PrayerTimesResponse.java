package com.noor.prayer.network;

import com.google.gson.annotations.SerializedName;
import com.noor.prayer.model.PrayerTimesData;

public class PrayerTimesResponse {
    @SerializedName("code")
    public int code;
    @SerializedName("status")
    public String status;
    @SerializedName("data")
    public PrayerTimesData data;
}
