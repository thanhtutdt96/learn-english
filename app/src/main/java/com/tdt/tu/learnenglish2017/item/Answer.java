package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 08-Nov-17.
 */

public class Answer {
    private String name;
    private String date;
    private String content;

    public Answer(String name, String date, String content) {
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
