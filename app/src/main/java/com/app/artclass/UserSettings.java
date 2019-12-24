package com.app.artclass;

import android.content.Context;
import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.RequiresApi;

import com.app.artclass.database.Abonement;
import com.app.artclass.database.GroupType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserSettings {

    private List<GroupType> allGroupTypes;
    private List<Abonement> allAbonements;
    private List<String> allWeekdaysStr = new ArrayList<>(
            Arrays.asList("Воскресенье",
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота"));

    private SparseArray<String> balanceButtonIncrements;

    private static UserSettings classInstance;
    private Context context;
    public static UserSettings getInstance(Context context){
        if(classInstance==null){
            classInstance = new UserSettings(context);
        }
        return classInstance;
    }
    public static UserSettings getInstance(){return classInstance;}
    public static UserSettings refreshInst(Context context){
        classInstance = new UserSettings(context);
        return classInstance;
    }

    private UserSettings(Context context) {
        allGroupTypes = new ArrayList<>();
        allAbonements = new ArrayList<>();
        balanceButtonIncrements = initDefaultBtnIncrements();
    }

    private SparseArray<String> initDefaultBtnIncrements() {
        SparseArray<String> out = new SparseArray<>();

        out.put(-100,"-100");
        out.put(100,"+100");
        out.put(500,"+500");

        return out;
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

        allGroupTypes.forEach((e)->{
            allGroupsStr.add(e.getGroupName());
        });

        return allGroupsStr;
    }

    public List<String> getAbonementLabels() {
        final List<String> allAbonementsStr = new ArrayList<>();
        allAbonements.forEach((e)->{
            allAbonementsStr.add(e.getName());
        });
        return allAbonementsStr;
    }

    public LocalTime getGroupTime(String timeString) {
        return LocalTime.of(11,0);
    }

    public Abonement getAbonement(String name) {
        return new Abonement(name,1000, 12);
    }

    public List<GroupType> getGroupTypes() {
        allGroupTypes=new ArrayList<>();
        allGroupTypes.add(new GroupType(LocalTime.of(11,0),"Loshara"));
        return allGroupTypes;
    }

    public int getWeekdayIndex(String weekday) {
        return allWeekdaysStr.indexOf(weekday);
    }

    public SparseArray<String> getBalanceIncrements() {
        return balanceButtonIncrements;
    }

    public GroupType getGroupType(String name) {
        for (GroupType groupType : allGroupTypes) {
            if (groupType.getGroupName() == name) {
                return groupType;
            }
        }
        return null;
    }
}
