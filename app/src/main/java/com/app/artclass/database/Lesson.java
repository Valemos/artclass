package com.app.artclass.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = Student.class,
        parentColumns = "id",
        childColumns = "student_id",
        onDelete = CASCADE))
public class Lesson {

    @TypeConverters({DatabaseConverters.class})
    public LocalDateTime date_time;

    @ColumnInfo(name = "student_id")
    public int student_id;

    public int hours_worked;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Lesson(LocalDate fullDate, LocalTime time, String studentName) {
        this(fullDate, time, DatabaseManager.getInstance().getStudentByName(studentName), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Lesson(LocalDate fullDate, LocalTime time, Student student, int hoursWorked) {
        this.date_time = LocalDateTime.of(fullDate,time);


    }
}
