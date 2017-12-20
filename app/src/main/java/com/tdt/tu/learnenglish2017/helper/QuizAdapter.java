package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.Quiz;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 21-Dec-17.
 */

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    Context context;
    List<Quiz> list;

    public QuizAdapter(Context context, List<Quiz> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public QuizAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_card_layout, parent, false);

        return new QuizAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuizAdapter.ViewHolder holder, int position) {
        Quiz quiz = list.get(position);
        holder.title.setText(quiz.getLessonNumber());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}