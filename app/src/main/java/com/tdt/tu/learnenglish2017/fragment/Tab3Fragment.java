package com.tdt.tu.learnenglish2017.fragment;

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
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.RequestHandler;
import com.tdt.tu.learnenglish2017.item.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham Thanh Tu on 26-Sep-17.
 */

public class Tab3Fragment extends Fragment {
    @BindView(R.id.listCategory)
    ListView listView;

    ArrayList<Category> categoryList = new ArrayList<>();
    CategoryAdapter adapter;
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

        loadCategories();
        init();
        listViewHandler();
        return view;

    }

    public void init() {
        ButterKnife.bind(this, view);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (!object.getString("message").equals(""))
                        Toast.makeText(view.getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    refreshQuestionList(object.getJSONArray("categories"));
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

    private void loadCategories() {
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_GET_CATEGORIES, null, Constants.CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshQuestionList(JSONArray questions) throws JSONException {
        categoryList.clear();

        for (int i = 0; i < questions.length(); i++) {
            JSONObject obj = questions.getJSONObject(i);

            categoryList.add(new Category(
                    imageId[i],
                    obj.getString("category_id"),
                    obj.getString("category_name")
            ));
        }
        CategoryAdapter adapter = new CategoryAdapter(view.getContext(), R.layout.category_row_layout, categoryList);
        listView.setAdapter(adapter);

    }

    private void listViewHandler() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment courseFragment = new CourseFragment();
                replaceFragment(courseFragment);

                Bundle bundle = new Bundle();
                bundle.putString("category_id", categoryList.get(position).getId());

                courseFragment.setArguments(bundle);
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment3_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


