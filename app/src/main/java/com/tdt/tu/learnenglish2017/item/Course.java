package com.tdt.tu.learnenglish2017.item;

/**
 * Created by Pham Thanh Tu on 11-Oct-17.
 */

public class Course {
    int id;
    String title;
    int price;

    public Course(int id, String title, int price) {
        this.id = id;
        this.title = title;
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
