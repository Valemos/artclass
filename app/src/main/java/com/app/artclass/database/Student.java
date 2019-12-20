package com.app.artclass.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.List;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Student {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    public List<String> phone_numbers;

    public int balance;

    public String abonement_type;

    public int hours_balance;

    @Override
    public boolean equals(@Nullable Object obj) {
        try{
            assert obj != null;
            return name.equals(((Student)obj).name);
        }catch (ClassCastException e){
            return false;
        }
    }

    public static Comparator<Student> getStudentComparator() {
        return studentComparator;
    }

    private static Comparator<Student> studentComparator = new Comparator<Student>() {
        @Override
        public int compare(Student o1, Student o2) {
            return o1.name.compareTo(o2.name);
        }
    };

    public Student(String fullName, int balance, String abonementType, int hoursToWork) {
        this.name = fullName;
        this.balance = balance;

        this.abonement_type = abonementType;
        this.hours_balance = hoursToWork;
    }

    public int getHoursBalance() {
        return hours_balance;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }
}
