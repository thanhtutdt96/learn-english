package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.activity.QAActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.CourseAdapter;
import com.tdt.tu.learnenglish2017.item.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab2Fragment extends Fragment {
    ArrayList<Course> listCourse;
    CourseAdapter adapter;
    ListView listView;
    View view;

    Integer[] imageId = {
            R.drawable.google_drive,
            R.drawable.firefox,
            R.drawable.adobe_acrobat,
            R.drawable.spotify,
            R.drawable.photoshop,
            R.drawable.dropbox,
            R.drawable.chrome,
            R.drawable.spotify,
            R.drawable.google_drive

    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment2_layout, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        listCourse = new ArrayList<>();

        getJSON(Constants.URL_ALL_COURSES);
        return view;
    }

    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    loadCoursesIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadCoursesIntoListView(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.getInt("success") == 1) {
            JSONArray jsonArray = jsonObject.getJSONArray("courses");

            final String[] course_names = new String[jsonArray.length()];
            final String[] course_ids = new String[jsonArray.length()];
            int[] prices = new int[jsonArray.length()];
            listCourse = new ArrayList<>();

            //looping through all the elements in json array
            for (int i = 0; i < jsonArray.length(); i++) {

                //getting json object from the json array
                JSONObject object = jsonArray.getJSONObject(i);

                course_ids[i] = object.getString("course_id");
                course_names[i] = object.getString("course_name");
                prices[i] = object.getInt("price");
                listCourse.add(new Course(imageId[i], course_ids[i], course_names[i], prices[i]));
            }
        } else {
            Toasty.warning(view.getContext(), "No courses found", Toast.LENGTH_SHORT);
        }

        adapter = new CourseAdapter(view.getContext(), R.layout.course_row_layout, listCourse);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences prefs = view.getContext().getSharedPreferences("my_prefs",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("courseId", listCourse.get(position).getCourseId());
                editor.commit();

                Intent i1 = new Intent(view.getContext(), LessonActivity.class);
                i1.putExtra("courseName", listCourse.get(position).getCourseName());
                startActivity(i1);

            }
        });
    }
}
