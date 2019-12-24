package com.app.artclass.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = @Index(value = "abonementName",unique = true))
public class Abonement {

    @PrimaryKey(autoGenerate = true)
    int idAbonement;

    String abonementName;

    int abonementPrice;

    int hoursToWork;

    public Abonement(String abonementName, int abonementPrice, int hoursToWork) {
        this.abonementName = abonementName;
        this.abonementPrice = abonementPrice;
        this.hoursToWork = hoursToWork;
    }

    public int getIdAbonement() {
        return idAbonement;
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
