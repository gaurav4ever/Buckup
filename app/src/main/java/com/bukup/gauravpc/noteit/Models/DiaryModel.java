package com.bukup.gauravpc.noteit.Models;

/**
 * Created by Gaurav Sharma on 18-05-2017.
 */
public class DiaryModel {

    int id;
    String date;
    String title;
    String data;

    public int getId() {
        return id;
    }

    public DiaryModel(String date, String title, String data) {
        this.date = date;
        this.title = title;
        this.data = data;
    }

    public DiaryModel() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
