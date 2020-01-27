package com.app.artclass.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.app.artclass.WEEKDAY;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DatabaseConverters {

    static Gson gson = new Gson();

    public static DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public static DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");

    private static NumberFormat moneyFormat = new DecimalFormat("##'₴'");

    public static NumberFormat getMoneyFormat() {
        return moneyFormat;
    }

    @TypeConverter
    public String fromLocalDate(LocalDate date){
        return date.format(dateFormatter);
    }

    @TypeConverter
    public LocalDate toLocalDate(String date){
        return LocalDate.parse(date,dateFormatter);
    }

    @TypeConverter
    public String fromLocalTime(LocalTime time){
        return time.format(timeFormatter);
    }

    @TypeConverter
    public LocalTime toLocalTime(String time){
        return LocalTime.parse(time,timeFormatter);
    }

    @TypeConverter
    public String fromLocalDateTime(LocalDateTime dateTime){
        return dateTime.format(dateTimeFormatter);
    }

    @TypeConverter
    public LocalDateTime toLocalDateTime(String dateTime){
        return LocalDateTime.parse(dateTime,dateTimeFormatter);
    }

    // list converter
    @TypeConverter
    public List<String> stringToStrList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public String strListToString(List<String> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public String weekdayToString(WEEKDAY weekday){
        return weekday.toString();
    }

    @TypeConverter
    public WEEKDAY stringToWeekday(String weekdayStr){
        return WEEKDAY.valueOf(weekdayStr);
    }
}
