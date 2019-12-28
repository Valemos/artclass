package com.app.artclass.database;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static androidx.room.ForeignKey.CASCADE;

@RequiresApi(api = Build.VERSION_CODES.O)

@Entity(
        foreignKeys = @ForeignKey(
        entity = Student.class,
        parentColumns = "name",
        childColumns = "studentName",
        onDelete = CASCADE),

        primaryKeys = {"dateTime"},
        indices = @Index(value = {"dateTime","studentName"},unique = true)
)
public class Lesson {

    @NonNull
    @TypeConverters({DatabaseConverters.class})
    private LocalDateTime dateTime;

    private String studentName;

    private int hoursWorked;

    public Lesson(@NotNull LocalDateTime dateTime, String studentName, int hoursWorked) {
        this.dateTime = dateTime;
        this.studentName = studentName;
        this.hoursWorked = hoursWorked;
    }

    public Lesson(LocalDate date, String studentName, GroupType groupType){
        this(LocalDateTime.of(date,groupType.getTime()), studentName, 0);
    }

    public Lesson(LocalDate fullDate, LocalTime time, Student student) {
        this(LocalDateTime.of(fullDate,time), student.getName(), 0);
    }

    public Lesson(LocalDateTime dateTime, Student student) {
        this(dateTime, student.getName(), 0);
    }

    public Lesson(LocalDateTime dateTime, Student student, int hoursWorked) {
        this(dateTime, student.getName(), hoursWorked);
    }

    @NotNull
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hours_worked) {
        this.hoursWorked = hours_worked;
    }

    public String getStudentName() {
        return studentName;
    }
}
