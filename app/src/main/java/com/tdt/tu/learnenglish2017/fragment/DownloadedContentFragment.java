package com.tdt.tu.learnenglish2017.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.helper.Constants;
import com.tdt.tu.learnenglish2017.helper.DownloadedContentAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 1stks on 03-Jan-18.
 */

public class DownloadedContentFragment extends Fragment {

    @BindView(R.id.expandableList)
    ExpandableListView expandableListView;
    @BindView(R.id.btnBack)
    Button btnBack;
    DownloadedContentAdapter adapter;
    List<String> listGroupHeader = new ArrayList<>();
    HashMap<String, List<String>> listGroupItem = new HashMap<>();
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_downloaded_content, container, false);
        init();
        prepareListData();
        return view;
    }

    private void prepareListData() {
        File appFolder = new File(Constants.DOWNLOAD_PATH);
        File[] listCourseFolder = appFolder.listFiles();
        for (File folder : listCourseFolder) {
            if (folder.isDirectory())
                listGroupHeader.add(folder.getName());
        }

        for (int i = 0; i < listGroupHeader.size(); i++) {
            File courseFolder = new File(Constants.DOWNLOAD_PATH + listGroupHeader.get(i));
            File[] listFiles = courseFolder.listFiles();
            List<String> listItem = new ArrayList<>();
            for (File file : listFiles) {
                if (file.isFile()) {
                    listItem.add(file.getName());
                }
            }
            listGroupItem.put(listGroupHeader.get(i), listItem);
        }

        adapter = new DownloadedContentAdapter(view.getContext(), listGroupHeader, listGroupItem);
        expandableListView.setAdapter(adapter);
    }

    private void init() {
        ButterKnife.bind(this, view);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }
}
