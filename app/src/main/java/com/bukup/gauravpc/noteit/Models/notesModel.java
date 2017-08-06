package com.bukup.gauravpc.noteit.Models;

/**
 * Created by gaurav pc on 29-Nov-16.
 */
public class notesModel {

    int id;
    String created_on;
    String updated_on;
    String title;
    String data;
    String tag;
    String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public notesModel(String created_on,String updated_on, String title, String data,String tag,String location) {
        this.created_on=created_on;
        this.updated_on=updated_on;
        this.title = title;
        this.data = data;
        this.tag=tag;
        this.location=location;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }
}
