package com.app.artclass.database;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.app.artclass.UserSettings;
import com.app.artclass.database.dao.GroupStudentRefDao;
import com.app.artclass.database.dao.GroupTypeDao;
import com.app.artclass.database.dao.LessonDao;
import com.app.artclass.database.dao.StudentDao;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.entity.GroupTypeStudentsRef;
import com.app.artclass.database.entity.GroupTypeWithStudents;
import com.app.artclass.database.entity.Lesson;
import com.app.artclass.database.entity.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StudentsRepository {

    private static StudentsRepository instance;

    private final Application mainApplication;

    private StudentDao mStudentDao;
    private LessonDao mLessonDao;
    private GroupTypeDao mGroupTypeDao;
    private GroupStudentRefDao mGroupStudentRefDao;
    private LiveData<List<Student>> allStudentsData;
    private LiveData<List<GroupType>> allGroupTypes;

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
        this.mGroupTypeDao = db.groupTypeDao();
        this.mGroupStudentRefDao = db.groupStudentRefDao();

        this.allStudentsData =  mStudentDao.getAll();
        this.allGroupTypes = mGroupTypeDao.getAll();
    }

    @NonNull
    public static StudentsRepository getInstance() {return instance;}

    public Application getMainApplication() {
        return mainApplication;
    }

    public void initDatabaseTest() {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {

            Student t0 = new Student("ИменноеИмя0", 0);t0.setStudentId(mStudentDao.insert(t0));
            Student t1 = new Student("ИменноеИмя1", 100);t1.setStudentId(mStudentDao.insert(t1));
            Student t2 = new Student("ИменноеИмя2", 200);t2.setStudentId(mStudentDao.insert(t2));
            Student t3 = new Student("ИменноеИмя3", 300);t3.setStudentId(mStudentDao.insert(t3));
            Student t4 = new Student("ИменноеИмя4", 400);t4.setStudentId(mStudentDao.insert(t4));
            Student t5 = new Student("ИменноеИмя5", 500);t5.setStudentId(mStudentDao.insert(t5));
            Student t6 = new Student("ИменноеИмя6", 600);t6.setStudentId(mStudentDao.insert(t6));
            Student t7 = new Student("ИменноеИмя7", 700);t7.setStudentId(mStudentDao.insert(t7));
            Student t8 = new Student("ИменноеИмя8", 800);t8.setStudentId(mStudentDao.insert(t8));
            Student t9 = new Student("ИменноеИмя9", 900);t9.setStudentId(mStudentDao.insert(t9));

            UserSettings.getInstance().getAllGroupTypes().forEach(groupType -> {
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t0.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t1.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t2.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t3.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t4.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t5.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t6.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t7.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t8.getStudentId()));
                mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(), t9.getStudentId()));
            });

            mLessonDao.insert(new Lesson(UserSettings.getInstance().getAllGroupTypes().get(0), t0, 0));
            mLessonDao.insert(new Lesson(UserSettings.getInstance().getAllGroupTypes().get(1), t0, 0));
            mLessonDao.insert(new Lesson(UserSettings.getInstance().getAllGroupTypes().get(2), t0, 0));
            mLessonDao.insert(new Lesson(UserSettings.getInstance().getAllGroupTypes().get(3), t0, 0));
        });
    }

    public void addStudent(@NonNull Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            student.setStudentId(mStudentDao.insert(student));
        });
    }

    public void addLesson(@NonNull Lesson lesson) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            mStudentDao.update(lesson.getStudent());
            lesson.setLessonId(mLessonDao.insert(lesson));
        });
    }

    public LiveData<Student> getStudent(String studentName) {
        return mStudentDao.get(studentName);
    }

    public void deleteLessons(LocalDate date, GroupType groupType) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.delete(date,groupType.getName()));
    }

    public void deleteLessons(List<Lesson> lessons){
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.delete(lessons));
    }

    public LiveData<List<Lesson>> getLessonList(LocalDate date, GroupType groupType)  {
        return mLessonDao.getForGroup(date, groupType.getName());
    }

    public LiveData<List<Lesson>> getLessonList(@NonNull Student student){
        return mLessonDao.getForStudent(student.getStudentId());
    }

    public LiveData<List<Student>> getAllStudents() {
        return allStudentsData;
    }

    public void addGroupLessons(LocalDate date, GroupType groupType, List<Student> students){
        DatabaseStudents.databaseWriteExecutor.execute(() ->
                students.forEach(student ->
                        mLessonDao.insert(new Lesson(date, groupType, student))));
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
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            mStudentDao.delete(student);
            mGroupStudentRefDao.deleteForStudent(student.getStudentId());
            mLessonDao.deleteForStudent(student.getStudentId());
        });
    }

    public void deleteLessonsForStudentsList(LocalDate date, GroupType groupType, List<Student> studentList) {
        List<Long> idList = new ArrayList<>();
        studentList.forEach(student -> idList.add(student.getStudentId()));
        DatabaseStudents.databaseWriteExecutor.execute(() -> mLessonDao.delete(date, groupType.getName(), idList));
    }

    public void deleteStudents(List<Student> studentList) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> studentList.forEach(student -> mStudentDao.delete(student)));
    }

    public void resetDatabase(Context context) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> DatabaseStudents.getDatabase(context).clearAllTables());
        try {
            DatabaseStudents.databaseWriteExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void insertGroupTypes(List<GroupType> allGroupTypes) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> allGroupTypes.forEach(groupType -> groupType.setGroupTypeId(mGroupTypeDao.insert(groupType))));
    }

    public void addLessons(List<Lesson> lessonsNew) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> lessonsNew.forEach(lesson -> mLessonDao.insert(lesson)));
    }

    public LiveData<List<Student>> getStudentsCursorByQuery(String query) {
        return mStudentDao.getByQuery("%"+query+"%");
    }

    public LiveData<GroupTypeWithStudents> getStudentsList(GroupType groupType) {
        return mGroupTypeDao.getGroupTypeWithStudents(groupType.getGroupTypeId());
    }

    public void addGroupType(GroupType groupType) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> groupType.setGroupTypeId(mGroupTypeDao.insert(groupType)));
    }

    public void deleteStudentsFromGroup(GroupType groupType, List<Student> studentList) {
        DatabaseStudents.databaseWriteExecutor.execute(() ->
                studentList.forEach(student ->
                        mGroupStudentRefDao.delete(new GroupTypeStudentsRef(groupType.getGroupTypeId(),student.getStudentId()))));
    }

    public void addStudentToGroup(GroupType groupType, Student student) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> mGroupStudentRefDao.insert(new GroupTypeStudentsRef(groupType.getGroupTypeId(),student.getStudentId())));
    }

    public LiveData<GroupTypeWithStudents> getStudentsForGroup(GroupType groupType) {
        return mGroupTypeDao.getGroupTypeWithStudents(groupType.getGroupTypeId());
    }

    public void addStudentsToGroup(GroupType groupType, List<Student> students) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> students.forEach(student ->
                mGroupStudentRefDao.insert(
                        new GroupTypeStudentsRef(groupType.getGroupTypeId(),student.getStudentId())
                )));
    }

    public void delete(GroupType groupType) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            mGroupStudentRefDao.deleteForGroup(groupType.getGroupTypeId());
            mGroupTypeDao.delete(groupType);
        });
    }

    public void addGroupTypeWithStudents(GroupType groupType, List<Student> students) {
        DatabaseStudents.databaseWriteExecutor.execute(() -> {
            groupType.setGroupTypeId(mGroupTypeDao.insert(groupType));
            students.forEach(student ->
                    mGroupStudentRefDao.insert(
                            new GroupTypeStudentsRef(groupType.getGroupTypeId(), student.getStudentId())
                    ));}
        );
    }

    public LiveData<List<GroupType>> getAllGroupTypes() {
        return allGroupTypes;
    }
}