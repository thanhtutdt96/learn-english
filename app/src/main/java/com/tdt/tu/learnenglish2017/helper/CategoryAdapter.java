package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.item.Category;
import com.tdt.tu.learnenglish2017.item.Course;

import java.util.List;

import butterknife.BindView;

public class CategoryAdapter extends ArrayAdapter<Category> {
    Context context;
    int resId;
    List<Category> list;
    ImageView icon;
    TextView title;

    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
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

        Category category=list.get(position);

        icon=(ImageView)convertView.findViewById(R.id.imgCategory);
        title=(TextView)convertView.findViewById(R.id.txtCategory);

        icon.setImageResource(category.getIcon());
        title.setText(category.getTitle());
        return convertView;
    }
}
