package com.tdt.tu.learnenglish2017.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.fragment.LessonsFragment;
import com.tdt.tu.learnenglish2017.fragment.MoreFragment;
import com.tdt.tu.learnenglish2017.fragment.Tab3Fragment;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.SectionsPagerAdapter;
import com.tdt.tu.learnenglish2017.item.Lesson;

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

public class LessonActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private YouTubePlayerFragment youTubePlayerFragment;

    String[] links;
    public static ArrayList<Lesson> listLesson;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs_Lesson)
    TabLayout tabLayout;
    @BindView(R.id.course_title_2)
    TextView courseTitle;

    public static YouTubePlayer mYoutubePlayer;
    int REQUEST_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        // Initialize the views
        ButterKnife.bind(this);

        String name = getIntent().getStringExtra("courseName");
        courseTitle.setText(name);

        listLesson = new ArrayList<>();

        //Retrieve all lessons data
        getJSON(Constants.URL_ALL_LESSONS);

        // Initialize the YoutubePlayerFragment
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
        youTubePlayerFragment.initialize(Constants.API_KEY, this);

        // Create the TabLayout
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Lessons");
        tabLayout.getTabAt(1).setText("More");

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LessonsFragment());
        adapter.addFragment(new MoreFragment());
        viewPager.setAdapter(adapter);
    }

    private void transferDataToFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("listLesson",listLesson);
        LessonsFragment lessonsFragment = new LessonsFragment();
        lessonsFragment.setArguments(bundle);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            mYoutubePlayer = youTubePlayer;
            youTubePlayer.loadVideo(links[0]);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(LessonActivity.this, REQUEST_VIDEO);

        } else {
            Toast.makeText(this, "Error loading video!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO)
            getYouTubePlayerProvider().initialize(Constants.API_KEY, this);
    }


    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubePlayerFragment);
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
                    loadLessonsIntoListView(s);
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

    private void loadLessonsIntoListView(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.getInt("success") == 1) {
            JSONArray jsonArray = jsonObject.getJSONArray("lessons");

            String[] lesson_names = new String[jsonArray.length()];
            String[] durations = new String[jsonArray.length()];
            links = new String[jsonArray.length()];
            String[] thumbnail = new String[jsonArray.length()];
            int[] prices = new int[jsonArray.length()];

            //looping through all the elements in json array
            for (int i = 0; i < jsonArray.length(); i++) {

                //getting json object from the json array
                JSONObject object = jsonArray.getJSONObject(i);

                //getting the name from the json object and putting it inside string array
                lesson_names[i] = object.getString("lesson_name");
                durations[i] = object.getString("duration");
                links[i] = object.getString("link");
                thumbnail[i] = "http://img.youtube.com/vi/" + links[i] + "/default.jpg";
                listLesson.add(new Lesson(thumbnail[i], lesson_names[i], durations[i], links[i]));
            }
        } else
            Toasty.warning(this, "No lessons found", Toast.LENGTH_SHORT);
    }

}
