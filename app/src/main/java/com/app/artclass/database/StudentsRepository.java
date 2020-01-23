package com.app.artclass.database;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.app.artclass.UserSettings;
import com.app.artclass.database.dao.AbonementDao;
import com.app.artclass.database.dao.GroupTypeDao;
import com.app.artclass.database.dao.LessonDao;
import com.app.artclass.database.dao.StudentDao;
import com.app.artclass.database.entity.Abonement;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsRepository {

    private static StudentsRepository instance;

    private final Application mainApplication;

    private StudentDao mStudentDao;
    private LessonDao mLessonDao;
    private AbonementDao mAbonementDao;
    private GroupTypeDao mGroupTypeDao;
    private LiveData<List<Student>> allStudentsData;

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


        this.allStudentsData =  mStudentDao.getAll();
    }

    @Nullable
    public static StudentsRepository getInstance() {return instance;}

    public Application getMainApplication() {
        return mainApplication;
    }

    public void initDefaultSettings() {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            DatabaseStudents.getDatabase(mainApplication.getApplicationContext()).clearAllTables();

            Student t1 = new Student("Borya", 500, UserSettings.getInstance().getAllAbonements().get(0));
            Student t2 = new Student("Gavril", 15000, UserSettings.getInstance().getAllAbonements().get(0));
            Student t3 = new Student("Alexey", 500, UserSettings.getInstance().getAllAbonements().get(1));
            Student t4 = new Student("Бомжара 007", 0, UserSettings.getInstance().getAllAbonements().get(1));
            Student t5 = new Student("Мистер Твистер", 228, Abonement.getNoAbonement());

            t1.setId(mStudentDao.insert(t1));
            t2.setId(mStudentDao.insert(t2));
            t3.setId(mStudentDao.insert(t3));
            t4.setId(mStudentDao.insert(t4));
            t5.setId(mStudentDao.insert(t5));

            mLessonDao.insert(new Lesson(LocalDate.now().plusDays(1),t1,UserSettings.getInstance().getAllGroupTypes().get(1)));
            mLessonDao.insert(new Lesson(LocalDate.now().plusDays(2),t2,UserSettings.getInstance().getAllGroupTypes().get(1)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t2,UserSettings.getInstance().getAllGroupTypes().get(0)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t3,UserSettings.getInstance().getAllGroupTypes().get(0)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t4,UserSettings.getInstance().getAllGroupTypes().get(0)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t4,UserSettings.getInstance().getAllGroupTypes().get(2)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t3,UserSettings.getInstance().getAllGroupTypes().get(2)));
        });
    }

    public void addStudent(@NonNull Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            student.setId(mStudentDao.insert(student));
        });
    }

    public void addLesson(@NonNull Lesson lesson) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            lesson.setLessonId(mLessonDao.insert(lesson));
        });
    }

    public LiveData<Student> getStudent(String studentName) {
        return mStudentDao.get(studentName);
    }

    public void deleteLessons(LocalDateTime dateTime) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.delete(dateTime));
    }

    public LiveData<List<Lesson>> getLessonList(LocalDateTime dateTime)  {
        return mLessonDao.getForDateTime(dateTime);
    }

    public LiveData<List<Lesson>> getLessonList(LocalDate date, GroupType groupType){
        return getLessonList(LocalDateTime.of(date,groupType.getTime()));
    }

    public LiveData<List<Lesson>> getLessonList(@NonNull Student student){
        return mLessonDao.getForStudent(student.getName());
    }

    public LiveData<List<Student>> getAllStudents() {
        return allStudentsData;
    }

    public void addGroup(LocalDateTime groupDate,@NonNull List<Student> students) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> students.forEach(student ->
                mLessonDao.insert(new Lesson(groupDate,student)
                )));
    }

    public void update(@NonNull Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mStudentDao.update(student));
    }

    public void update(@NonNull Lesson lesson) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.update(lesson));
    }

    public void update(List<Lesson> lessonsList) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> lessonsList.forEach(lesson -> {
            mStudentDao.update(lesson.getStudent());
            mLessonDao.update(lesson);
        }));
    }

    public void delete(@NonNull Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mStudentDao.delete(student));
    }

    public void deleteLessonsForStudentsList(LocalDateTime dateTime, List<Student> studentList) {
        List<String> names = new ArrayList<>();
        studentList.forEach(student -> names.add(student.getName()));
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.delete(dateTime, names));
    }

    public void deleteStudents(List<Student> studentList) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> studentList.forEach(student -> mStudentDao.delete(student)));
    }

    public void resetDatabase(Context context) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> DatabaseStudents.getDatabase(context).clearAllTables());
    }

    public void insertAbonements(List<Abonement> allAbonements) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> allAbonements.forEach(abonement -> mAbonementDao.insert(abonement)));
    }

    public void insertGroupTypes(List<GroupType> allGroupTypes) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> allGroupTypes.forEach(groupType -> mGroupTypeDao.insert(groupType)));
    }

    public LiveData<Student> getStudentForName(String name) {
        return mStudentDao.get(name);
    }

    public LiveData<List<Student>> getStudentsForNames(List<String> selectedStudentNames) {

        return mStudentDao.getAllByNames(selectedStudentNames);
    }
}