package com.app.artclass.database;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static androidx.room.ForeignKey.CASCADE;

@RequiresApi(api = Build.VERSION_CODES.O)

@Entity(
        foreignKeys = @ForeignKey(
        entity = Student.class,
        parentColumns = "idStudent",
        childColumns = "idStudent",
        onDelete = CASCADE),

        primaryKeys = {"dateTime","idStudent"},

        indices = @Index(value = {"idStudent"})
)
public class Lesson {

    @NonNull
    @TypeConverters({DatabaseConverters.class})
    private LocalDateTime dateTime;

    @ColumnInfo(name = "idStudent")
    private int studentId;

    private int hoursWorked;

    public Lesson(LocalDateTime dateTime, int studentId, int hoursWorked) {
        this.dateTime = dateTime;
        this.studentId = studentId;
        this.hoursWorked = hoursWorked;
    }

    public Lesson(LocalDate fullDate, LocalTime time, Student student) {
        this(LocalDateTime.of(fullDate,time), student.getIdStudent(), 0);
    }

    public Lesson(LocalDateTime dateTime, Student student) {
        this(dateTime, student.getIdStudent(), 0);
    }

    public Lesson(LocalDateTime dateTime, Student student, int hoursWorked) {
        this(dateTime, student.getIdStudent(), hoursWorked);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int student_id) {
        this.studentId = student_id;
    }

    public String getName(){
        return DatabaseManager.getInstance().getStudent(studentId).getName();
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hours_worked) {
        this.hoursWorked = hours_worked;
    }
}
