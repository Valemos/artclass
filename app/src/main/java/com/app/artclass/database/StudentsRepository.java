package com.app.artclass.database;

import android.app.Application;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsRepository {

    private static StudentsRepository instance;

    private final Application mainApplication;

    private StudentDao mStudentDao;
    private LessonDao mLessonDao;
    private AbonementDao mAbonementDao;
    private GroupTypeDao mGroupTypeDao;

    public static StudentsRepository getInstance(Application application){
        if (instance == null){
            instance = new StudentsRepository(application);
        }
        return instance;
    }

    private StudentsRepository(Application application) {
        this.mainApplication =application;
        DatabaseStudents db = DatabaseStudents.getDatabase(application);
        this.mStudentDao = db.studentDao();
        this.mLessonDao = db.lessonDao();
        this.mAbonementDao = db.abonementDao();
        this.mGroupTypeDao = db.groupTypeDao();
    }

    @Nullable
    public static StudentsRepository getInstance() {return instance;}

    public Application getMainApplication() {
        return mainApplication;
    }

    public void addStudent(Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            mStudentDao.insert(student);
        });
    }

    public void addLesson(Lesson lesson) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            mLessonDao.insert(lesson);
        });
    }

    public LiveData<Student> getStudent(String studentName) {
        return mStudentDao.get(studentName);
    }

    public boolean isGroupExists(LocalDate date, LocalTime time) {
        return mLessonDao.getForDateTime(LocalDateTime.of(date,time)).getValue()!=null;
    }

    public void deleteLessons(LocalDateTime dateTime) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            mLessonDao.delete(dateTime);
        });
    }

    public LiveData<List<Lesson>> getLessonList(LocalDateTime dateTime) {
        return mLessonDao.getForDateTime(dateTime);
    }

    public LiveData<List<Lesson>> getLessonList(LocalDate date, LocalTime time){
        return getLessonList(LocalDateTime.of(date,time));
    }

    public LiveData<List<Lesson>> getLessonList(Student student){
        return mLessonDao.getForStudent(student.getName());
    }

    public LiveData<List<Student>> getAllStudents() {
        return mStudentDao.getAll();
    }

    public void addGroup(LocalDateTime groupDate, List<Student> students) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> students.forEach(student ->
                mLessonDao.insert(new Lesson(groupDate,student)
                )));
    }

    public void update(Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mStudentDao.update(student));
    }

    public void update(Lesson lesson) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.update(lesson));
    }

    public void delete(Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mStudentDao.delete(student));
    }

    public void deleteLessonsForStudentsList(LocalDateTime dateTime, List<Student> studentList) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> studentList.forEach(student -> mLessonDao.delete(dateTime, student.getName())));
    }

    public void deleteStudents(List<Student> studentList) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> studentList.forEach(student -> mStudentDao.delete(student)));
    }

    public void resetDatabase() {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
        });
    }

    public void initDefaultSettings() {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
        });
    }

    public LiveData<List<Student>> getStudentsForNames(List<String> names) {
        return mStudentDao.getAllByNames(names);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void initDefaultSettings(){
//        addSetting(StudentsRepository.SETTING_GROUP_DAYS_INIT_AMOUNT, "severalDays",10);
//        addSetting(StudentsRepository.SETTING_DATE_SEPARATOR_DIGIT, "separator", "/");
//        addSetting(StudentsRepository.SETTING_LESSON_DURATION,"normalDuration", 2);
//        addSetting(StudentsRepository.SETTING_MAXIMUM_GROUP_AMOUNT, "maximumGroup", 11);
//
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Воскресенье",1);
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Понедельник",2);
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Вторник",3);
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Среда",4);
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Четверг",5);
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Пятница",6);
//        addSetting(StudentsRepository.SETTING_WEEKDAY_LABEL,"Суббота",7);
//
//        addSetting(StudentsRepository.SETTING_ABONEMENT,"разово","300", 2);
//        addSetting(StudentsRepository.SETTING_ABONEMENT,"600 1/2","600", 8);
//        addSetting(StudentsRepository.SETTING_ABONEMENT,"1200 полн.","1200", 16);
//
//        addSetting(StudentsRepository.SETTING_GROUP_TYPE, "11:00",1100);
//        addSetting(StudentsRepository.SETTING_GROUP_TYPE, "15:00",1500);
//        addSetting(StudentsRepository.SETTING_GROUP_TYPE, "17:00",1700);
//
//        addSetting(StudentsRepository.SETTING_BUTTON_INCREMENT, "+100",100);
//        addSetting(StudentsRepository.SETTING_BUTTON_INCREMENT, "-100",-100);
//        addSetting(StudentsRepository.SETTING_BUTTON_INCREMENT, "+500",500);
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