package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 01-Nov-17.
 */

public class Category {
    int icon;
    String title;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }
}
