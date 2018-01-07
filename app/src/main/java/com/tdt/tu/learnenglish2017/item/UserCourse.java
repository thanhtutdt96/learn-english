package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 06-Jan-18.
 */

public class UserCourse {
    private String image;
    private String courseId;
    private String courseName;
    private String description;
    private String link;
    private float rating;
    private int progress;

    public UserCourse(String image, String courseId, String courseName, String description, String link, float rating, int progress) {
        this.image = image;
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.link = link;
        this.rating = rating;
        this.progress = progress;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
