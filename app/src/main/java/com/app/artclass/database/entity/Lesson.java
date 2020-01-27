package com.app.artclass.database.entity;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.app.artclass.database.DatabaseConverters;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

import static androidx.room.ForeignKey.CASCADE;

@RequiresApi(api = Build.VERSION_CODES.O)

@Entity(foreignKeys = @ForeignKey(entity = Student.class, parentColumns = "studentId", childColumns = "stud_studentId", onDelete = CASCADE),
        indices = @Index(value = {"date","group_time","stud_studentId"},unique = true))
public class Lesson {

    @PrimaryKey(autoGenerate = true)
    private long lessonId;

    @NonNull
    @TypeConverters({DatabaseConverters.class})
    private LocalDate date;

    @NonNull
    @Embedded(prefix = "stud_")
    private Student student;

    @NonNull
    @Embedded(prefix = "group_")
    private GroupType groupType;

    private int hoursWorked;

    @Ignore
    private static Comparator<Lesson> dateComparator = (o1, o2) -> o1.getDate().compareTo(o2.getDate());

    public Lesson(@NotNull LocalDate date, @NotNull GroupType groupType, @NotNull Student student) {
        this.date = date;
        this.student = student;
        this.groupType = groupType;
    }

    @Ignore
    public Lesson(LocalDate date, Student student) {
        this(date, GroupType.getNoGroup(), student);
    }

    @Ignore
    public Lesson(LocalDate date, Student student, GroupType groupType) {
        this(date, groupType, student);
    }

    public LocalDate getDate() {
        return date;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Lesson lesson;
        try {
            lesson = (Lesson)obj;
        }catch (Exception e){
            return false;
        }

        return lesson != null && lessonId == lesson.getLessonId();
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

    @NonNull
    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(@NonNull GroupType groupType) {
        this.groupType = groupType;
    }

    public static Comparator<Lesson> getDateComparator() {
        return dateComparator;
    }
}
