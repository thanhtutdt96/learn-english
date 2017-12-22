package com.tdt.tu.learnenglish2017.item;

/**
 * Created by 1stks on 21-Dec-17.
 */

public class QuizResult {
    private String date;
    private int score;
    private String rank;

    public QuizResult(String date, int score, String rank) {
        this.date = date;
        this.score = score;
        this.rank = rank;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
