package com.app.artclass.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DatabaseConverters {

    public static DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public static DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd/hh:mm");

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
}
