package com.tdt.tu.learnenglish2017.item;

public class Course {
    private int imageId;
    private String courseId;
    private String courseName;
    private int price;
    private String description;

    public Course(int imageId, String courseId, String courseName, int price, String description) {
        this.imageId = imageId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.price = price;
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
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
}
