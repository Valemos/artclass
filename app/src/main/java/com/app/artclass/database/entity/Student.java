package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.app.artclass.UserSettings;

import org.jetbrains.annotations.NotNull;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Student implements Comparable{

    @PrimaryKey(autoGenerate = true)
    private long studentId;

    @NonNull
    private final String name;

    @NonNull
    private String notes = "";

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

    public Student(@NonNull String name, String notes, int moneyBalance, int hoursBalance) {
        this.name = name;
        this.notes = notes;
        this.moneyBalance = moneyBalance;
        this.hoursBalance = hoursBalance;
    }

    @Ignore
    public Student(@NotNull String name, int moneyBalance) {
        this.name = name;
        this.setMoneyBalance(moneyBalance);
        hoursBalance = UserSettings.getInstance().getHoursForMoney(moneyBalance);
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
        this.hoursBalance = UserSettings.getInstance().getHoursForMoney(moneyBalance);
    }

    public int getMoneyBalance() {
        return moneyBalance;
    }

    public void incrementMoneyBalance(int incr){
        moneyBalance += incr;
        hoursBalance = UserSettings.getInstance().getHoursForMoney(moneyBalance);
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    @NotNull
    public String getNotes() {
        return notes;
    }

    public void setNotes(@NotNull String notes) {
        this.notes = notes;
    }

    @Override
    public int compareTo(Object o) {
        try {
            return name.compareTo(((Student) o).name);
        }catch (ClassCastException e){
            return 0;
        }
    }
}
