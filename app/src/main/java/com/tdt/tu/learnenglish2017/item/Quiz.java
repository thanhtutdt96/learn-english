package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 21-Dec-17.
 */

public class Quiz {
    private String lessonId;
    private String lessonNumber;

    public Quiz(String lessonId, String lessonNumber) {
        this.lessonId = lessonId;
        this.lessonNumber = lessonNumber;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(String lessonNumber) {
        this.lessonNumber = lessonNumber;
    }
}
