package com.app.artclass.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Comparator;
import java.util.List;

@Entity(indices = {@Index(value = {"name","idStudent"}, unique = true)})
public class Student {

    @PrimaryKey(autoGenerate = true)
    private int idStudent;

    @NonNull
    private String name;

    public Student(@NonNull String name, List<String> phoneList, Abonement abonementType, int balance, int hoursBalance) {
        this.name = name;
        this.phoneList = phoneList;
        this.abonementType = abonementType;
        this.balance = balance;
        this.hoursBalance = hoursBalance;
    }

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

    public static Comparator<Student> getStudentComparator() {
        return studentComparator;
    }

    private static Comparator<Student> studentComparator = new Comparator<Student>() {
        @Override
        public int compare(Student o1, Student o2) {
            return o1.name.compareTo(o2.name);
        }
    };

    public Student(String fullName, int balance, Abonement abonementType) {
        this.name = fullName;
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

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
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
}
