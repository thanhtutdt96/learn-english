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
import com.tdt.tu.learnenglish2017.item.More;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pham Thanh Tu on 30-Oct-17.
 */

public class MoreAdapter extends ArrayAdapter<More> {
    private Context context;
    private int resId;
    private List<More> list;

    @BindView(R.id.more_Row_Icon)
    ImageView icon;
    @BindView(R.id.more_Row_TextView)
    TextView textView;

    public MoreAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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

        More more = list.get(position);
        icon.setImageResource(more.getId());
        textView.setText(more.getName());

        return convertView;
    }
}
