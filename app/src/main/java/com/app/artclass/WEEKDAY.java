package com.app.artclass;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

public enum WEEKDAY {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье"),
    NO_WEEKDAY("не выбрано");

    String name;

    WEEKDAY(String weekName) {
        name = weekName;
    }

    public static WEEKDAY getNoWeekday(Context context) {
        return WEEKDAY.NO_WEEKDAY.setName(context.getString(R.string.no_weekday_label));
    }

    public static List<WEEKDAY> getValues() {
        return Arrays.asList(Arrays.copyOfRange(WEEKDAY.values(),0, 6));
    }

    public String getName() {
        return name;
    }

    private WEEKDAY setName(String name){
        this.name = name;
        return this;
    }

    public static WEEKDAY get(int pos) {
        return WEEKDAY.values()[pos];
    }
}
