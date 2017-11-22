package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 31-Oct-17.
 */

public class Question {
    String questionId;
    String name;
    String date;
    String lesson;
    String title;
    String content;
    int commentCount;

    public Question(String questionId, String name, String date, String lesson, String title, String content, int commentCount) {
        this.questionId = questionId;
        this.name = name;
        this.date = date;
        this.lesson = lesson;
        this.title = title;
        this.content = content;
        this.commentCount = commentCount;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
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

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
