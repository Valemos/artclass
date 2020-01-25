package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Abonement {

    @NonNull
    @PrimaryKey
    public
    String name;

    private int price;

    public int hours;

    public Abonement(String name, int price, int hours) {
        this.name = name;
        this.price = price;
        this.hours = hours;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public static Abonement getNoAbonement(){
        return new Abonement("без абонемента",0,0);
    }

    public int getHours() {
        return hours;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int abonementPrice) {
        this.price = abonementPrice;
    }

    public void setHours(int hoursToWork) {
        this.hours = hoursToWork;
    }
}
