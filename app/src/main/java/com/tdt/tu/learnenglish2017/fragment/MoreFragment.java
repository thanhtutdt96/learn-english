package com.tdt.tu.learnenglish2017.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.QAActivity;
import com.tdt.tu.learnenglish2017.helper.MoreAdapter;
import com.tdt.tu.learnenglish2017.item.More;

import java.util.ArrayList;

/**
 * Created by Pham Thanh Tu on 30-Oct-17.
 */

public class MoreFragment extends Fragment {
    View view;
    ListView listView;
    MoreAdapter adapter;
    ArrayList<More> arrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_more, container, false);
        listView = (ListView) view.findViewById(R.id.more_ListView);
        arrayList = new ArrayList<>();
        arrayList.add(new More(R.drawable.download, "Download this course"));
        arrayList.add(new More(R.drawable.info, "About this course"));
        arrayList.add(new More(R.drawable.qa, "Q&A"));
        adapter = new MoreAdapter(view.getContext(), R.layout.more_row_layout, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent intent=new Intent(view.getContext(), QAActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        return view;
    }
}
