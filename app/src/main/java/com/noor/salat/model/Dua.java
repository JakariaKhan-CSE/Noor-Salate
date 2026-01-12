package com.noor.salat.model;

public class Dua {
    public String title;
    public String arabic;
    public String translation;
    public String reference;
    public boolean expanded;

    public Dua(String title, String arabic, String translation, String reference) {
        this.title = title;
        this.arabic = arabic;
        this.translation = translation;
        this.reference = reference;
        this.expanded = false;
    }
}
