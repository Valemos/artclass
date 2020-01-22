package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Abonement {

    @NonNull
    @PrimaryKey
    public
    String abonementName;

    int abonementPrice;

    public int hoursToWork;

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

    public static Abonement getNoAbonement(){
        return new Abonement("без абонемента",0,0);
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

    public void setName(@NonNull String abonementName) {
        this.abonementName = abonementName;
    }

    public int getAbonementPrice() {
        return abonementPrice;
    }

    public void setPrice(int abonementPrice) {
        this.abonementPrice = abonementPrice;
    }

    public void setHours(int hoursToWork) {
        this.hoursToWork = hoursToWork;
    }
}
