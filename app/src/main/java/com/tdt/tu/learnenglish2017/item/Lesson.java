package com.tdt.tu.learnenglish2017.item;

/**
 * Created by Pham Thanh Tu on 18-Oct-17.
 */

public class Lesson {
    String image;
    String title;
    String duration;
    String link;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Lesson(String image, String title, String duration, String link) {
        this.image = image;
        this.title = title;
        this.duration = duration;
        this.link = link;

    }

}
