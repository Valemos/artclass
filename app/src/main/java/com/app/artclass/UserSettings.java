package com.app.artclass;

import android.content.Context;
import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.app.artclass.database.entity.GroupType;
import com.app.artclass.database.StudentsRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserSettings {

    public static int signInRequestCode = 0;
    private static String googleAppToken = "113249870759-4cq8cpt5mt209tb0v5n7pbetbh2lgntq.apps.googleusercontent.com";

    private List<GroupType> allGroupTypes = new ArrayList<>();
    private static GroupType noGroup;
    private GoogleSignInAccount main_account;

    public List<WEEKDAY> getWeekdays() {
        return WEEKDAY.getValues();
    }

    public List<WEEKDAY> getWeekdaysWithDefault(Context context) {
        List<WEEKDAY> weekdays = new ArrayList<>(WEEKDAY.getValues());
        weekdays.add(0, WEEKDAY.getNoWeekday(context));
        return weekdays;
    }

    public int getHoursForMoney(int moneyBalance) {
        return moneyBalance / getMoneyPerHour();
    }

    public int getMoneyForHours(int hours) {
        return hours * getMoneyPerHour();
    }

    private int getMoneyPerHour() {
        return 75;
    }

    private SparseArray<String> balanceButtonIncrements;

    private static UserSettings classInstance;

    public static UserSettings getInstance(){
        if(classInstance==null){
            classInstance = new UserSettings();
        }
        return classInstance;
    }

    private UserSettings() {
        balanceButtonIncrements = initDefaultBtnIncrements();
        noGroup = new GroupType(LocalTime.of(0,0), WEEKDAY.NO_WEEKDAY, "не выбрано");
    }

    public void initDefaultGroupTypes() {
        GroupType g0 = new GroupType(LocalTime.of(11,0), WEEKDAY.TUESDAY, "дети 11:00");
        GroupType g1 = new GroupType(LocalTime.of(11,0), WEEKDAY.SATURDAY, "взрослые 11:00");
        GroupType g2 = new GroupType(LocalTime.of(15,0), WEEKDAY.SATURDAY, "дети 15:00");
        GroupType g3 = new GroupType(LocalTime.of(17,0), WEEKDAY.SATURDAY, "взрослые 17:00");
        GroupType g4 = new GroupType(LocalTime.of(17,0), WEEKDAY.SUNDAY, "взрослые 17:00");


        StudentsRepository.getInstance().addGroupType(g0);
        StudentsRepository.getInstance().addGroupType(g1);
        StudentsRepository.getInstance().addGroupType(g2);
        StudentsRepository.getInstance().addGroupType(g3);
        StudentsRepository.getInstance().addGroupType(g4);

        allGroupTypes = new ArrayList<>(Arrays.asList(g0, g1, g2, g3, g4));
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
        return 7;
    }

    public int getWeekdayIndex(WEEKDAY toCheck) {
        for (int pos = 0; pos < WEEKDAY.getValues().size(); pos++) {
            if(toCheck==WEEKDAY.get(pos)){
                // +1 required for LocalDate Weekdays
                return pos;
            }
        }
        return 0;
    }

    public List<GroupType> getAllGroupTypes() {
        return allGroupTypes;
    }

    public List<GroupType> getAllGroupTypesWithDefault(Context context) {
        List<GroupType> groupTypes = new ArrayList<>(getAllGroupTypes());
        groupTypes.add(0, GroupType.getNoGroup(context));
        return groupTypes;
    }

    public SparseArray<String> getBalanceIncrements() {
        return balanceButtonIncrements;
    }

    public void writeSettingsToRepository(StudentsRepository repository) {
        repository.addGroupType(noGroup);
        repository.insertGroupTypes(allGroupTypes);
    }

    public void addGroupType(GroupType groupType) {
        allGroupTypes.add(groupType);
    }

    public void removeGroupType(GroupType groupType){
        allGroupTypes.remove(groupType);
    }

    public void getSettingsFromRepository(StudentsRepository repository, MainActivity mainActivity) {

    }

    public String getWeekday(LocalDate date) {
        return WEEKDAY.get(date.getDayOfWeek().getValue()-1).getName();
    }

    public GroupType getNoGroup() {
        return noGroup;
    }

    public GroupType getNoGroup(Context context) {
        noGroup.setName(context.getString(R.string.no_group_label));
        noGroup.setWeekday(WEEKDAY.getNoWeekday(context));
        return noGroup;
    }

    public String getHoursTextFormat() {
        return "%d ч";
    }

    public void initGoogleAccount(GoogleSignInAccount account) {
        main_account = account;
    }

    public GoogleSignInAccount getUserAccount() {
        return main_account;
    }

    public static String getGoogleAppToken() {
        return googleAppToken;
    }

    public void setAllGroupTypes(List<GroupType> groupTypes) {
        allGroupTypes.clear();
        allGroupTypes.addAll(groupTypes);
    }
}
