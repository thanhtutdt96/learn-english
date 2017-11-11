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
import com.tdt.tu.learnenglish2017.item.Answer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 08-Nov-17.
 */

public class AnswerAdapter extends ArrayAdapter<Answer> {
    private Context context;
    private int resId;
    private List<Answer> list;

    @BindView(R.id.txtName_rowAnswer)
    TextView name;
    @BindView(R.id.txtDate_rowAnswer)
    TextView date;
    @BindView(R.id.txtContent_rowAnswer)
    TextView content;

    public AnswerAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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

        Answer answer = list.get(position);

        name.setText(answer.getName());
        date.setText(answer.getDate());
        content.setText(answer.getContent());

        return convertView;
    }
}
