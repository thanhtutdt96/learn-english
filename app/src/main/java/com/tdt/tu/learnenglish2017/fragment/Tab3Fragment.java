package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.CategoryAdapter;
import com.tdt.tu.learnenglish2017.item.Category;
import com.tdt.tu.learnenglish2017.item.Course;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.CourseAdapter;

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

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab3Fragment extends Fragment {
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    CategoryAdapter adapter;
    ListView listView;
    View view;

    Integer[] imageId = {
            R.drawable.conversation,
            R.drawable.job,
            R.drawable.food,
            R.drawable.sport,
            R.drawable.holiday,
            R.drawable.animal,
            R.drawable.certificate,
            R.drawable.basis,
            R.drawable.travel

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment3_layout, container, false);

        listView = (ListView) view.findViewById(R.id.listCategory);
        getJSON(Constants.URL_ALL_CATEGORIES);

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
            JSONArray jsonArray = jsonObject.getJSONArray("categories");

            String[] category_names = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                category_names[i] = object.getString("category_name");
                categoryArrayList.add(new Category(imageId[i], category_names[i]));
            }
        } else {
            Toasty.warning(view.getContext(), "No categories found", Toast.LENGTH_SHORT);
        }

        adapter = new CategoryAdapter(view.getContext(), R.layout.category_row_layout, categoryArrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        fragment = new Tab2Fragment();
                        replaceFragment(fragment);
                        break;
                    case 8:
                        break;
                }
            }
        });
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment3_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


