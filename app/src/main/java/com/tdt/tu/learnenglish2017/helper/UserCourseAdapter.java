package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.UserCourse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCourseAdapter extends RecyclerView.Adapter<UserCourseAdapter.ViewHolder> {
    private Context context;
    private List<UserCourse> list;

    public UserCourseAdapter(Context context, List<UserCourse> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public UserCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_course_card, parent, false);

        return new UserCourseAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserCourseAdapter.ViewHolder holder, int position) {
        UserCourse course = list.get(position);

        Picasso.with(context).load(course.getImage()).into(holder.icon);
        holder.title.setText(course.getCourseName());
        holder.ratingBar.setRating(course.getRating());
        holder.progressBar.setProgress(course.getProgress());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.ratingBar)
        SimpleRatingBar ratingBar;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

