package com.app.artclass.database;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.app.artclass.UserSettings;

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
    private List<Student> allStudentsList;

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

    public void deleteLessons(LocalDateTime dateTime) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.delete(dateTime));
    }

    public LiveData<List<Lesson>> getLessonList(LocalDateTime dateTime) {
        return mLessonDao.getForDateTime(dateTime);
    }

    public LiveData<List<Lesson>> getLessonList(LocalDate date, GroupType groupType){
        return getLessonList(LocalDateTime.of(date,groupType.getTime()));
    }

    public LiveData<List<Lesson>> getLessonList(Student student){
        return mLessonDao.getForStudent(student.getName());
    }

    public LiveData<List<Student>> getAllStudents() {
        System.out.println(allStudentsData.getValue());
        return allStudentsData;
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

    public void initDefaultSettings() {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            Student t1 = new Student("Borya", 500, UserSettings.getInstance().getAllAbonements().get(0));
            Student t2 = new Student("Gavril", 15000, UserSettings.getInstance().getAllAbonements().get(0));
            Student t3 = new Student("Alexey", 500, UserSettings.getInstance().getAllAbonements().get(1));
            Student t4 =new Student("Бомжара 007", 0, UserSettings.getInstance().getAllAbonements().get(1));
            mStudentDao.insert(t1);
            mStudentDao.insert(t2);
            mStudentDao.insert(t3);
            mStudentDao.insert(t4);

            mLessonDao.insert(new Lesson(LocalDate.now().plusDays(1),t1,UserSettings.getInstance().getAllGroupTypes().get(1)));
            mLessonDao.insert(new Lesson(LocalDate.now().plusDays(2),t2,UserSettings.getInstance().getAllGroupTypes().get(1)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t2,UserSettings.getInstance().getAllGroupTypes().get(0)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t3,UserSettings.getInstance().getAllGroupTypes().get(0)));
            mLessonDao.insert(new Lesson(LocalDate.now(),t4,UserSettings.getInstance().getAllGroupTypes().get(0)));
        });
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