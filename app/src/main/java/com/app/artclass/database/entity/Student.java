package com.app.artclass.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.artclass.UserSettings;
import com.app.artclass.database.DatabaseConverters;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Student implements Comparable{

    @PrimaryKey(autoGenerate = true)
    private long studentId;

    @TypeConverters({DatabaseConverters.class})
    private LocalDate dateCreated;

    @NonNull
    private final String name;

    @NonNull
    private String notes = "";

    private int moneyBalance;

    private int hoursBalance;

    public Student(@NonNull String name, String notes, int moneyBalance, int hoursBalance, LocalDate dateCreated) {
        this.name = name;
        this.notes = notes;
        this.moneyBalance = moneyBalance;
        this.hoursBalance = hoursBalance;
        this.dateCreated = dateCreated;
    }

    @Ignore
    public Student(@NotNull String name, int moneyBalance) {
        this.name = name;
        this.setMoneyBalance(moneyBalance);
        dateCreated = LocalDate.now();
    }

    @Ignore
    public Student(@NotNull String name, String notes, int moneyBalance) {
        this.name = name;
        this.notes = notes;
        this.setMoneyBalance(moneyBalance);
        dateCreated = LocalDate.now();
    }

    public void setHoursBalance(int hoursBalance) {
        int diff = hoursBalance - this.hoursBalance;
        this.moneyBalance += UserSettings.getInstance().getMoneyForHours(diff);
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

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public int compareTo(Object o) {
        try {
            return name.compareTo(((Student) o).name);
        }catch (ClassCastException e){
            return 0;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try{
            assert obj != null;
            return name.equals(((Student)obj).name);
        }catch (ClassCastException e){
            return false;
        }
    }
}
