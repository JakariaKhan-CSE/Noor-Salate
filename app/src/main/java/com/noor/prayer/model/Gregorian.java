package com.noor.prayer.model;

import com.google.gson.annotations.SerializedName;

public class Gregorian {
    @SerializedName("date")
    public String date;
    @SerializedName("month")
    public Month month;
    @SerializedName("year")
    public String year;
    @SerializedName("weekday")
    public Weekday weekday;

    public static class Month {
        @SerializedName("number")
        public int number;
        @SerializedName("en")
        public String en;
    }
    
    public static class Weekday {
        @SerializedName("en")
        public String en;
    }
}
