package com.app.artclass.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = @Index(value = "abonement_name",unique = true))
public class Abonement {

    @PrimaryKey(autoGenerate = true)
    int id;

    String abonement_name;

    public int getAbonementAmount() {
        return abonement_amount;
    }

    int abonement_amount;

    public Abonement(String abonement_name, int abonement_amount) {
        this.abonement_name = abonement_name;
        this.abonement_amount = abonement_amount;
    }

    public String getAbonementName() {
        return abonement_name;
    }
}
