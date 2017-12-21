package com.tdt.tu.learnenglish2017.item;

public class Course {
    private String image;
    private String courseId;
    private String courseName;
    private int price;
    private String description;
    private String link;

    public Course(String image, String courseId, String courseName, int price, String description, String link) {
        this.image = image;
        this.courseId = courseId;
        this.courseName = courseName;
        this.price = price;
        this.description = description;
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
