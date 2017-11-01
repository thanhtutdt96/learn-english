package com.tdt.tu.learnenglish2017.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.Question;

import java.util.List;

/**
 * Created by 1stks on 01-Nov-17.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder> {
    private List<Question> questionList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.username.setText(question.getName());
        holder.date.setText(question.getDate());
        holder.lesson.setText(question.getLesson());
        holder.questionTitle.setText(question.getTitle());
        holder.questionContent.setText(question.getContent());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, date, lesson, questionTitle, questionContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.txtName_Question);
            date = (TextView) itemView.findViewById(R.id.txtDate_Question);
            lesson = (TextView) itemView.findViewById(R.id.txtlessonNumber_Question);
            questionTitle = (TextView) itemView.findViewById(R.id.txtTitle_Question);
            questionContent = (TextView) itemView.findViewById(R.id.txtContent_Question);
        }
    }

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }
}
