package com.tdt.tu.learnenglish2017.helper;

import android.widget.Filter;

import com.tdt.tu.learnenglish2017.item.CourseSuggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.tdt.tu.learnenglish2017.fragment.Tab2Fragment.suggestionList;

/**
 * Created by 1stks on 27-Dec-17.
 */

public class FilterHelper {
    private static List<CourseSuggestion> courseSuggestion = suggestionList;

    public static void resetSuggestionsHistory() {
        for (CourseSuggestion suggestion : courseSuggestion) {
            suggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(String query, final int limit, final long simulatedDelay, final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                resetSuggestionsHistory();
                List<CourseSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (CourseSuggestion suggestion : courseSuggestion) {
                        if (suggestion.getBody().toUpperCase().startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<CourseSuggestion>() {
                    @Override
                    public int compare(CourseSuggestion lhs, CourseSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<CourseSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public interface OnFindSuggestionsListener {
        void onResults(List<CourseSuggestion> results);
    }

}
