package com.noor.prayer.model;

import com.google.gson.annotations.SerializedName;

public class PrayerTimesData {
    @SerializedName("timings")
    public Timings timings;
    @SerializedName("date")
    public Date date;
}
