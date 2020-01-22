package com.app.artclass.database.entity;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.artclass.database.DatabaseConverters;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)

@Entity(indices = @Index(value = {"dateTime","stud_id"},unique = true))
public class Lesson {

    @PrimaryKey(autoGenerate = true)
    private long lessonId;

    @NonNull
    @TypeConverters({DatabaseConverters.class})
    private LocalDateTime dateTime;

    @NonNull
    @Embedded(prefix = "stud_")
    private Student student;

    private int hoursWorked;

    public Lesson(LocalDate fullDate, Student student, GroupType groupType) {
        this(LocalDateTime.of(fullDate,groupType.getTime()), student, 0);
    }

    @Ignore
    public Lesson(LocalDateTime dateTime, Student student) {
        this(dateTime, student, 0);
    }

    public Lesson(LocalDateTime dateTime, Student student, int hoursWorked) {
        this.dateTime = dateTime;
        this.student = student;
        this.hoursWorked = hoursWorked;
    }

    @NotNull
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Lesson lesson = null;
        try {
            lesson = (Lesson)obj;
        }catch (Exception e){
            return false;
        }

        assert lesson != null;

        return student.getId()==(lesson.getStudent().getId()) &&
                dateTime.equals(lesson.dateTime) &&
                hoursWorked==lesson.hoursWorked;
    }

    public Student getStudent() {
        return student;
    }

    public void setHoursWorked(int hours_worked) {
        this.hoursWorked = hours_worked;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }
}
