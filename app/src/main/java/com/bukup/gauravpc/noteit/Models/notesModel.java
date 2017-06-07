package com.bukup.gauravpc.noteit.Models;

/**
 * Created by gaurav pc on 29-Nov-16.
 */
public class notesModel {

    int id;
    String date;
    String title;
    String data;
    String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public notesModel() {
    }

    public notesModel(String date, String title, String data,String tag) {
        this.date = date;
        this.title = title;
        this.data = data;
        this.tag=tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
