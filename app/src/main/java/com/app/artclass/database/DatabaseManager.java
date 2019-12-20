package com.app.artclass.database;

import android.content.Context;
import android.os.Build;
import android.util.LruCache;

import androidx.annotation.RequiresApi;
import androidx.room.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DatabaseManager{

    public static final String DATABASE_NAME = "studentsData";

    private DatabaseStudents databaseStudents;


    // singleton instance
    private static DatabaseManager classInstance;

    public static DatabaseManager getInstance(){
        return classInstance;
    }
    public DatabaseStudents getDatabase(){ return databaseStudents; }

    public static DatabaseManager getInstance(Context context){
        if(classInstance==null) {
            classInstance = new DatabaseManager(context);
        }
        return classInstance;
    }

    private DatabaseManager(Context context) {
        databaseStudents = Room.databaseBuilder(context,DatabaseStudents.class, DATABASE_NAME)
                .build();
    }

    public Student getStudentByName(String name){
        return databaseStudents.studentDao().get(name);
    }

    public void addStudent(Student studentNew) {
        databaseStudents.studentDao().insert(studentNew);
    }

    public void addLesson(Lesson lesson) {
        databaseStudents.lessonDao().insert(lesson);
    }

    public Student getStudent(String studentName) {
        return databaseStudents.studentDao().get(studentName);
    }

    public boolean isGroupExists(LocalDate date, LocalTime time) {
        List<Lesson> lsn = databaseStudents.lessonDao().getForDateTime(LocalDateTime.of(date,time));

        return (lsn != null || lsn.size()>0);
    }

    public void deleteLessons(LocalDateTime dateTime) {
        databaseStudents.lessonDao().delete(dateTime);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void initDefaultSettings(){
//        addSetting(DatabaseManager.SETTING_GROUP_DAYS_INIT_AMOUNT, "severalDays",10);
//        addSetting(DatabaseManager.SETTING_DATE_SEPARATOR_DIGIT, "separator", "/");
//        addSetting(DatabaseManager.SETTING_LESSON_DURATION,"normalDuration", 2);
//        addSetting(DatabaseManager.SETTING_MAXIMUM_GROUP_AMOUNT, "maximumGroup", 11);
//
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Воскресенье",1);
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Понедельник",2);
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Вторник",3);
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Среда",4);
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Четверг",5);
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Пятница",6);
//        addSetting(DatabaseManager.SETTING_WEEKDAY_LABEL,"Суббота",7);
//
//        addSetting(DatabaseManager.SETTING_ABONEMENT,"разово","300", 2);
//        addSetting(DatabaseManager.SETTING_ABONEMENT,"600 1/2","600", 8);
//        addSetting(DatabaseManager.SETTING_ABONEMENT,"1200 полн.","1200", 16);
//
//        addSetting(DatabaseManager.SETTING_GROUP_TYPE, "11:00",1100);
//        addSetting(DatabaseManager.SETTING_GROUP_TYPE, "15:00",1500);
//        addSetting(DatabaseManager.SETTING_GROUP_TYPE, "17:00",1700);
//
//        addSetting(DatabaseManager.SETTING_BUTTON_INCREMENT, "+100",100);
//        addSetting(DatabaseManager.SETTING_BUTTON_INCREMENT, "-100",-100);
//        addSetting(DatabaseManager.SETTING_BUTTON_INCREMENT, "+500",500);
//
//
//        initTestSamples();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void initTestSamples() {
//        addStudent(new Student("Bntoxa", 125, "1200 полный", 5));
//        addStudent(new Student("Antoxich", 500, "600 пол", 8));
//        addStudent(new Student("Antoxion", 315, "1200 полный", 16));
//        addStudent(new Student("1", 315, "1200 полный", 16));
//        addStudent(new Student("2", 315, "1200 полный", 16));
//
//        addLessonForStudent(new Lesson(Converters.getDateInt(LocalDate.now()), "15:00", "Antoxion", 0));
//        addLessonForStudent(new Lesson(20191030, "11:00", "Bntoxa", 0));
//        addLessonForStudent(new Lesson(20191030, "17:00", "Antoxich", 0));
//    }
}