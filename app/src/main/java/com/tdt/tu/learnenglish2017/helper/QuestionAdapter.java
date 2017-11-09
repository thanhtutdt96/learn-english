package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.Question;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 01-Nov-17.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {
    private Context context;
    private int resId;
    private List<Question> list;

    @BindView(R.id.txtName_Question)
    TextView name;
    @BindView(R.id.txtDate_Question)
    TextView date;
    @BindView(R.id.txtlessonNumber_Question)
    TextView lessonNumber;
    @BindView(R.id.txtTitle_Question)
    TextView title;
    @BindView(R.id.txtContent_Question)
    TextView content;

    public QuestionAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
        ButterKnife.bind(this, convertView);

        Question question = list.get(position);

        name.setText(question.getName());
        date.setText(question.getDate());
        lessonNumber.setText(question.getLesson());
        title.setText(question.getTitle());
        content.setText(question.getContent());

        return convertView;
    }
}
