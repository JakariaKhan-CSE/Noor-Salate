package com.noor.prayer.model;

import com.google.gson.annotations.SerializedName;

public class Hijri {
    @SerializedName("date")
    public String date;
    @SerializedName("day")
    public String day; // Added day
    @SerializedName("month")
    public Month month;
    @SerializedName("year")
    public String year;

    public static class Month {
        @SerializedName("number")
        public int number;
        @SerializedName("en")
        public String en;
        @SerializedName("ar")
        public String ar;
    }
}
