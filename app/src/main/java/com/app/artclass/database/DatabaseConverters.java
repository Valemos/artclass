package com.app.artclass.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

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

    // Abonement converter
}
