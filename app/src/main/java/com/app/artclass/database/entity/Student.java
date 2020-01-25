package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Student implements Comparable{

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private final String name;

    private String notes;

    @Embedded(prefix = "abon_")
    private Abonement abonementType;

    private int moneyBalance;

    private int hoursBalance;

    @Override
    public boolean equals(@Nullable Object obj) {
        try{
            assert obj != null;
            return name.equals(((Student)obj).name);
        }catch (ClassCastException e){
            return false;
        }
    }

    public Student(@NonNull String name, String notes, Abonement abonementType, int moneyBalance, int hoursBalance) {
        this.name = name;
        this.notes = notes;
        this.abonementType = abonementType;
        this.moneyBalance = moneyBalance;
        this.hoursBalance = hoursBalance;
    }

    @Ignore
    public Student(@NotNull String name, int moneyBalance, Abonement abonementType) {
        this.name = name;
        this.setMoneyBalance(moneyBalance);

        this.setAbonementType(abonementType);
        this.hoursBalance = abonementType.getHours();
    }

    public void setHoursBalance(int hoursBalance) {
        this.hoursBalance = hoursBalance;
    }

    public int getHoursBalance() {
        return hoursBalance;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setMoneyBalance(int moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public int getMoneyBalance() {
        return moneyBalance;
    }

    public void incrementMoneyBalance(int incr){
        moneyBalance += incr;
    }

    public Abonement getAbonementType() {
        return abonementType;
    }

    public void setAbonementType(Abonement abonementType) {
        this.abonementType = abonementType;
    }

    @Override
    public int compareTo(Object o) {
        try {
            return name.compareTo(((Student) o).name);
        }catch (ClassCastException e){
            return 0;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
