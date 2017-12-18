package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 01-Nov-17.
 */

public class Category {
    String icon;
    String id;
    String title;

    public Category(String icon, String id, String title) {
        this.icon = icon;
        this.id = id;
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
