package com.noor.salat.model;

import com.google.gson.annotations.SerializedName;

public class Timings {
    @SerializedName("Fajr")
    public String fajr;
    @SerializedName("Sunrise")
    public String sunrise;
    @SerializedName("Dhuhr")
    public String dhuhr;
    @SerializedName("Asr")
    public String asr;
    @SerializedName("Sunset")
    public String sunset;
    @SerializedName("Maghrib")
    public String maghrib;
    @SerializedName("Isha")
    public String isha;
    @SerializedName("Imsak")
    public String imsak;
    @SerializedName("Midnight")
    public String midnight;
}
