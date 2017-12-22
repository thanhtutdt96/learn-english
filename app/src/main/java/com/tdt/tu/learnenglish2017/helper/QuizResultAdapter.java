package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.QuizResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 22-Dec-17.
 */

public class QuizResultAdapter extends ArrayAdapter<QuizResult> {
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtScore)
    TextView txtScore;
    @BindView(R.id.txtRank)
    TextView txtRank;

    private Context context;
    private int resId;
    private List<QuizResult> list;


    public QuizResultAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<QuizResult> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resId, parent, false);
        }
        ButterKnife.bind(this, convertView);

        QuizResult result = list.get(position);

        txtDate.setText(result.getDate());
        txtScore.setText(String.valueOf(result.getScore()));
        txtRank.setText(result.getRank());

        return convertView;
    }
}
