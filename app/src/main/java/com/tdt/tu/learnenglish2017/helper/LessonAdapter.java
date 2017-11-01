package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.Lesson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham Thanh Tu on 18-Oct-17.
 */

public class LessonAdapter extends ArrayAdapter<Lesson> {
    private Context context;
    private int resId;
    private List<Lesson> list;
    @BindView(R.id.lesson_thumbnail)
    ImageView thumbnail;
    @BindView(R.id.lesson_title)
    TextView title;
    @BindView(R.id.lesson_duration)
    TextView duration;
    @BindView(R.id.lesson_download)
    ImageView download;


    public LessonAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Lesson> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resId, parent, false);
        }
        Lesson lesson = list.get(position);

        ButterKnife.bind(this, convertView);
        Picasso.with(context).load(lesson.getImage()).into(thumbnail);
        title.setText(lesson.getTitle());
        duration.setText(lesson.getDuration());


        return convertView;
    }
}
