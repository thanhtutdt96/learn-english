package com.tdt.tu.learnenglish2017.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.CourseInfoActivity;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.CourseAdapter;
import com.tdt.tu.learnenglish2017.helper.FilterHelper;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Course;
import com.tdt.tu.learnenglish2017.item.CourseSuggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab2Fragment extends Fragment {
    public static List<CourseSuggestion> suggestionList = new ArrayList<>();
    @BindView(R.id.recyclerSearchResult)
    RecyclerView recyclerSearchResult;
    @BindView(R.id.listTopSearch)
    ListView listViewTopSearch;
    @BindView(R.id.searchBar)
    FloatingSearchView searchBar;
    @BindView(R.id.txtTopSearches)
    TextView txtTopSearches;
    private View view;
    private List<Course> courseList = new ArrayList<>();
    private List<String> listCourseId = new ArrayList<>();
    private List<String> listTopSearch = new ArrayList<>();
    private ArrayAdapter searchAdapter;
    private CourseAdapter courseAdapter;

    private ArrayList<CourseSuggestion> historyList = new ArrayList<>();
    private ArrayList<String> duplicateSuggestions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2, container, false);

        init();
        loadSearchSuggestions();
        listViewHandler();

        return view;
    }

    private void init() {
        ButterKnife.bind(this, view);
        courseAdapter = new CourseAdapter(view.getContext(), courseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerSearchResult.setLayoutManager(linearLayoutManager);
        recyclerSearchResult.setHasFixedSize(true);
        recyclerSearchResult.setItemAnimator(new DefaultItemAnimator());
        recyclerSearchResult.setAdapter(courseAdapter);

        searchAdapter = new ArrayAdapter<>(view.getContext(), R.layout.top_searches_row, listTopSearch);
        listTopSearch.clear();
        listTopSearch.add("free");
        listTopSearch.add("conversation");
        listTopSearch.add("english");
        listTopSearch.add("vocabulary");
        listTopSearch.add("toeic");
        listTopSearch.add("ielts");
        listTopSearch.add("job");
        listTopSearch.add("food");
        listTopSearch.add("advanced");
        listTopSearch.add("sport");
        listViewTopSearch.setAdapter(searchAdapter);

        setupSearchBar();

        listViewTopSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String searchText = listTopSearch.get(i);
                searchBar.setSearchText(searchText);
                searchHandler(searchText);
                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            }
        });

    }

    private void setupSearchBar() {
        searchBar.bringToFront();
        searchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchBar.clearSuggestions();
                } else {
                    searchBar.showProgress();

                    FilterHelper.findSuggestions(newQuery, 3,
                            250, new FilterHelper.OnFindSuggestionsListener() {
                                @Override
                                public void onResults(List<CourseSuggestion> results) {
                                    searchBar.swapSuggestions(results);
                                    searchBar.hideProgress();
                                }
                            });
                }
            }
        });

        searchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                CourseSuggestion suggestion = (CourseSuggestion) searchSuggestion;
                saveHistory(suggestion.getBody());
                searchHandler(suggestion.getBody());

            }

            @Override
            public void onSearchAction(String currentQuery) {
                saveHistory(currentQuery);
                searchHandler(currentQuery);
            }
        });

        searchBar.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                searchBar.swapSuggestions(historyList);
            }

            @Override
            public void onFocusCleared() {

            }
        });

        searchBar.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_home) {
                    listViewTopSearch.setVisibility(View.VISIBLE);
                    txtTopSearches.setVisibility(View.VISIBLE);
                    recyclerSearchResult.setVisibility(View.GONE);
                    searchBar.clearQuery();
                    searchBar.clearSuggestions();
                }
            }
        });
    }

    private void saveHistory(String searchQuery) {
        CourseSuggestion suggestion = new CourseSuggestion(searchQuery);
        suggestion.setIsHistory(true);

        for (CourseSuggestion tmp : historyList) {
            if (tmp.getBody().equalsIgnoreCase(searchQuery)) {
                return;
            }
        }

        if (historyList.size() >= 3) {
            historyList.remove(historyList.size() - 1);
            historyList.add(suggestion);
        } else {
            historyList.add(suggestion);
        }
    }

    private void searchHandler(String query) {
        listViewTopSearch.setVisibility(View.GONE);
        txtTopSearches.setVisibility(View.GONE);
        recyclerSearchResult.setVisibility(View.VISIBLE);
        if (query.equalsIgnoreCase("Free")) {
            loadSearchResults("" + 0);
        } else
            loadSearchResults(query);
    }

    private void listViewHandler() {
        recyclerSearchResult.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = recyclerSearchResult.findChildViewUnder(e.getX(), e.getY());
                int recyclerViewItemPosition;
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    recyclerViewItemPosition = recyclerSearchResult.getChildAdapterPosition(childView);

                    String email = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).getString("email", "");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);

                    LoadUserCourseIds loadUserCourseIds = new LoadUserCourseIds(Constants.URL_GET_USER_COURSE_IDS_BY_EMAIL, params, Constants.CODE_POST_REQUEST, recyclerViewItemPosition);
                    loadUserCourseIds.execute();
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void loadSearchResults(String searchQuery) {
        HashMap<String, String> params = new HashMap<>();
        params.put("search_query", searchQuery);

        LoadSearchResults loadSearchResults = new LoadSearchResults(Constants.URL_GET_SEARCH_RESULTS, params, Constants.CODE_POST_REQUEST);
        loadSearchResults.execute();
    }

    private void refreshQuestionList(JSONArray results) throws JSONException {
        courseList.clear();
        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);

            courseList.add(new Course(
                    obj.getString("icon"),
                    obj.getString("course_id"),
                    obj.getString("course_name"),
                    obj.getInt("price"),
                    obj.getString("description"),
                    obj.getString("link"),
                    Float.parseFloat(obj.getString("rating"))
            ));
        }

        courseAdapter.notifyDataSetChanged();
    }

    private void refreshCourseIdList(JSONArray questions) throws JSONException {
        listCourseId.clear();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);
            listCourseId.add(obj.getString("course_id"));
        }
    }

    private void switchActivity(int position) {
        if (checkEnrolledCourse(position)) {
            SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE).edit();
            editor.putString("course_id", courseList.get(position).getCourseId());
            editor.putString("description", courseList.get(position).getDescription());
            editor.commit();

            Intent intent = new Intent(view.getContext(), LessonActivity.class);
            intent.putExtra("course_name", courseList.get(position).getCourseName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(view.getContext(), CourseInfoActivity.class);
            intent.putExtra("course_id", courseList.get(position).getCourseId());
            intent.putExtra("course_name", courseList.get(position).getCourseName());
            intent.putExtra("price", courseList.get(position).getPrice());
            intent.putExtra("description", courseList.get(position).getDescription());
            intent.putExtra("link", courseList.get(position).getLink());

            startActivity(intent);
        }
    }

    private boolean checkEnrolledCourse(int position) {
        for (String courseId : listCourseId) {
            if (courseId.equals(courseList.get(position).getCourseId())) {
                return true;
            }
        }
        return false;
    }

    private void loadSearchSuggestions() {
        LoadSearchSuggestions loadSearchSuggestions = new LoadSearchSuggestions(Constants.URL_GET_SEARCH_SUGGESTIONS, null, Constants.CODE_GET_REQUEST);
        loadSearchSuggestions.execute();
    }

    private void refreshSuggestions(JSONArray suggestions) throws JSONException {
        suggestionList.clear();

        for (int i = 0; i < suggestions.length(); i++) {
            JSONObject obj = suggestions.getJSONObject(i);

            String[] courseNames = obj.getString("course_name").toLowerCase().split("\\s+");
            String[] tags = obj.getString("tag").split(",");
            String[] descriptions = obj.getString("description").toLowerCase().split("\\s*,\\s*|\\s+|\\s*\\.\\s*");

            for (int j = 0; j < courseNames.length; j++) {
                duplicateSuggestions.add(courseNames[j]);
            }

            for (int k = 0; k < tags.length; k++) {
                duplicateSuggestions.add(tags[k]);
            }

            for (int m = 0; m < descriptions.length; m++) {
                duplicateSuggestions.add(descriptions[m]);
            }
        }
    }

    private class LoadSearchResults extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog dialog;

        LoadSearchResults(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(view.getContext());
            dialog.setMessage("Searching...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshQuestionList(object.getJSONArray("results"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    public class LoadUserCourseIds extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;
        int position;

        LoadUserCourseIds(String url, HashMap<String, String> params, int requestCode, int position) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.position = position;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshCourseIdList(object.getJSONArray("course_ids"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            switchActivity(position);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    private class LoadSearchSuggestions extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        LoadSearchSuggestions(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toasty.info(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshSuggestions(object.getJSONArray("suggestions"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            linkedHashSet.addAll(duplicateSuggestions);
            duplicateSuggestions.clear();
            duplicateSuggestions.addAll(linkedHashSet);

            for (String suggestion : duplicateSuggestions) {
                suggestionList.add(new CourseSuggestion(suggestion));
            }

        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == Constants.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestCode == Constants.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

}
