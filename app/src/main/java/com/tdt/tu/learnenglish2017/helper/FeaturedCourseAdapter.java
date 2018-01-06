package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.Course;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 17-Dec-17.
 */

public class FeaturedCourseAdapter extends RecyclerView.Adapter<FeaturedCourseAdapter.ViewHolder> {
    Context context;
    List<Course> list;

    public FeaturedCourseAdapter(Context context, List<Course> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public FeaturedCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_course_card, parent, false);

        return new FeaturedCourseAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeaturedCourseAdapter.ViewHolder holder, int position) {
        Course course = list.get(position);
        Picasso.with(context).load(course.getImage()).into(holder.icon);
        holder.title.setText(course.getCourseName());
        SpannableString priceUnit = new SpannableString("đ" + String.valueOf(course.getPrice()));
        priceUnit.setSpan(new UnderlineSpan(), 0, 1, 0);
        if (course.getPrice() == 0)
            holder.price.setText("Free");
        else
            holder.price.setText(priceUnit);
        holder.ratingBar.setRating(course.getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView icon;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.ratingBar)
        SimpleRatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
