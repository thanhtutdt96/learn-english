package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by Pham Thanh Tu on 11-Oct-17.
 */

public class CourseAdapter extends ArrayAdapter<Course> {
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.ratingBar)
    SimpleRatingBar ratingBar;
    private Context context;
    private int resId;
    private List<Course> list;


    public CourseAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Course> objects) {
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
        Course course = list.get(position);

        ButterKnife.bind(this, convertView);

        Picasso.with(context).load(course.getImage()).into(icon);
        title.setText(course.getCourseName());
        SpannableString priceUnit = new SpannableString("đ" + String.valueOf(course.getPrice()));
        priceUnit.setSpan(new UnderlineSpan(), 0, 1, 0);
        if (course.getPrice() == 0)
            price.setText("Free");
        else
            price.setText(priceUnit);
        ratingBar.setRating(course.getRating());

        return convertView;
    }

}

