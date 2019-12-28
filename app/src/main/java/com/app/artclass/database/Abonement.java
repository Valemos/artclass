package com.app.artclass.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Abonement {

    @NonNull
    @PrimaryKey
    String abonementName;

    int abonementPrice;

    int hoursToWork;

    public Abonement(String abonementName, int abonementPrice, int hoursToWork) {
        this.abonementName = abonementName;
        this.abonementPrice = abonementPrice;
        this.hoursToWork = hoursToWork;
    }

    @NonNull
    @Override
    public String toString() {
        return abonementName;
    }

    public int getPrice() {
        return abonementPrice;
    }

    public int getHours() {
        return hoursToWork;
    }

    public String getName() {
        return abonementName;
    }
}
