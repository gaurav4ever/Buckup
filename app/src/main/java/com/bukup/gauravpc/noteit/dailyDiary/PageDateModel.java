package com.bukup.gauravpc.noteit.dailyDiary;

/**
 * Created by Gaurav Sharma on 19-05-2017.
 */
public class PageDateModel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String day;
    private String day_ordinal;
    private String month;
    private String year;
    private String week_of_month;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay_ordinal() {
        return day_ordinal;
    }

    public void setDay_ordinal(String day_ordinal) {
        this.day_ordinal = day_ordinal;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWeek_of_month() {
        return week_of_month;
    }

    public void setWeek_of_month(String week_of_month) {
        this.week_of_month = week_of_month;
    }
}
