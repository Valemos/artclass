package com.app.artclass;

import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.RequiresApi;

import com.app.artclass.database.entity.Abonement;
import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.StudentsRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserSettings {

    private List<GroupType> allGroupTypes;
    private List<Abonement> allAbonements;

    private List<String> allWeekdaysStr = new ArrayList<>(
            Arrays.asList(
                    "Суббота",
                    "Воскресенье",
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница"
                    ));

    private SparseArray<String> balanceButtonIncrements;

    private static UserSettings classInstance;

    public static UserSettings getInstance(){
        if(classInstance==null){
            classInstance = new UserSettings();
        }
        return classInstance;
    }
    private UserSettings() {
        allGroupTypes = initDefaultGroupTypes();
        allAbonements = initDefaultAbonements();
        balanceButtonIncrements = initDefaultBtnIncrements();
    }

    private List<GroupType> initDefaultGroupTypes() {
        return Arrays.asList(
                new GroupType(LocalTime.of(11,0),"дети 11:00"),
                new GroupType(LocalTime.of(15,0),"взрослые 15:00"),
                new GroupType(LocalTime.of(17,0),"взрослые 17:00"));
    }

    private List<Abonement> initDefaultAbonements() {
        return Arrays.asList(
                new Abonement("обычный", 1200, 12),
                new Abonement("половина", 600, 6));
    }

    private SparseArray<String> initDefaultBtnIncrements() {
        SparseArray<String> out = new SparseArray<>();

        out.put(-100,"-100");
        out.put(100,"+100");
        out.put(500,"+500");

        return out;
    }

    public List<Abonement> getAllAbonements() {
        if(allAbonements.size()==0)
            allAbonements = initDefaultAbonements();
        return allAbonements;
    }

    public static int getDefaultLessonHours() {
        return 2;
    }

    public static long getGroupDaysInitAmount() {
        return 10;
    }

    public List<String> getWeekdayLabels() {
        return allWeekdaysStr;
    }

    public List<String> getGroupLabels() {
        List<String> allGroupsStr = new ArrayList<>();
        allGroupTypes.forEach((e)-> allGroupsStr.add(e.getGroupName()));
        return allGroupsStr;
    }

    public LocalTime getGroupTime(String groupName) {
        return Objects.requireNonNull(allGroupTypes.stream().filter(groupType -> groupType.getGroupName().equals(groupName))
                .findFirst().orElse(null)).getTime();
    }

    public Abonement getAbonement(String name) {
        return allAbonements.stream()
                .filter(abonement -> abonement.getName().equals(name))
                .findFirst().orElse(null);
    }

    public List<GroupType> getAllGroupTypes() {
        if(allGroupTypes.size()==0)
            allGroupTypes=initDefaultGroupTypes();
        return allGroupTypes;
    }

    public int getWeekdayIndex(String weekday) {
        return allWeekdaysStr.indexOf(weekday);
    }

    public SparseArray<String> getBalanceIncrements() {
        return balanceButtonIncrements;
    }

    public GroupType getGroupType(String name) {
        return allGroupTypes.stream().filter(groupType -> groupType.getGroupName().equals(name))
                .findFirst().orElse(null);
    }

    public void writeSettingsToRepository(StudentsRepository repository) {
        repository.insertAbonements(allAbonements);
        repository.insertGroupTypes(allGroupTypes);
    }
}
