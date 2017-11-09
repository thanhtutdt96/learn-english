package com.tdt.tu.learnenglish2017.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubePlayer;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.helper.LessonAdapter;

/**
 * Created by Pham Thanh Tu on 30-Oct-17.
 */

public class LessonsFragment extends Fragment {
    View view;
    LessonAdapter adapter;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lesson, container, false);


        listView = (ListView) view.findViewById(R.id.lessons_ListView);

        adapter = new LessonAdapter(view.getContext(), R.layout.lesson_row_layout, LessonActivity.listLesson);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLessonLink = LessonActivity.listLesson.get(position).getLink();
                if (LessonActivity.mYoutubePlayer != null) {
                    LessonActivity.mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    LessonActivity.mYoutubePlayer.loadVideo(selectedLessonLink);
                    LessonActivity.mYoutubePlayer.play();
                }
            }
        });

        return view;
    }
}

