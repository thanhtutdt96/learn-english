package com.tdt.tu.learnenglish2017.item;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class CourseSuggestion implements SearchSuggestion {

    public static final Creator<CourseSuggestion> CREATOR = new Creator<CourseSuggestion>() {
        @Override
        public CourseSuggestion createFromParcel(Parcel in) {
            return new CourseSuggestion(in);
        }

        @Override
        public CourseSuggestion[] newArray(int size) {
            return new CourseSuggestion[size];
        }
    };
    private String courseName;
    private boolean isHistory = false;

    public CourseSuggestion(String suggestion) {
        this.courseName = suggestion.toLowerCase();
    }

    public CourseSuggestion(Parcel source) {
        this.courseName = source.readString();
        this.isHistory = source.readInt() != 0;
    }

    public boolean getIsHistory() {
        return this.isHistory;
    }

    public void setIsHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    @Override
    public String getBody() {
        return courseName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeInt(isHistory ? 1 : 0);
    }
}