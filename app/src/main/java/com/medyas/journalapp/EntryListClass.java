package com.medyas.journalapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class EntryListClass {

    private String title, content, date, priority;

    public EntryListClass() {
        title = "";
        content = "";
        date = "";
        priority = "";
    }

    public EntryListClass(String title, String content, String date, String priority) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Drawable getPriorityImage(Resources res) {
        if(this.priority.equals("")) return res.getDrawable(android.R.drawable.btn_star_big_off);
        else return res.getDrawable(android.R.drawable.btn_star_big_on);
    }
}
