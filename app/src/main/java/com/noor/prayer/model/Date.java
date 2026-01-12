package com.noor.prayer.model;

import com.google.gson.annotations.SerializedName;

public class Date {
    @SerializedName("readable")
    public String readable;
    @SerializedName("hijri")
    public Hijri hijri;
    @SerializedName("gregorian")
    public Gregorian gregorian;
}
