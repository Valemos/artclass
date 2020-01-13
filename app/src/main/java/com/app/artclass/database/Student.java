package com.app.artclass.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Student implements Comparable{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private final String name;

    @TypeConverters({DatabaseConverters.class})
    private List<String> phoneList;

    @Embedded
    private Abonement abonementType;

    private int balance;

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

    public Student(@NonNull String name, List<String> phoneList, Abonement abonementType, int balance, int hoursBalance) {
        this.name = name;
        this.phoneList = phoneList;
        this.abonementType = abonementType;
        this.balance = balance;
        this.hoursBalance = hoursBalance;
    }

    @Ignore
    public Student(@NotNull String name, int balance, Abonement abonementType) {
        this.name = name;
        this.setBalance(balance);

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

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void incrementBalance(int incr){
        balance += incr;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }

    public Abonement getAbonementType() {
        return abonementType;
    }

    public void setAbonementType(Abonement abonementType) {
        this.abonementType = abonementType;
    }

    public void setPhoneList(List<String> phoneList) {
        this.phoneList = phoneList;
    }

    @Override
    public int compareTo(Object o) {
        try {
            return name.compareTo(((Student) o).name);
        }catch (ClassCastException e){
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
