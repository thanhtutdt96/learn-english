package com.tdt.tu.learnenglish2017.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.DialogVideoActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 1stks on 03-Jan-18.
 */

public class DownloadedContentAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public DownloadedContentAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {

        String headerTitle = (String) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.downloaded_group, null);
        }
        TextView txtGroupHeader = (TextView) view.findViewById(R.id.txtGroupHeader);
        txtGroupHeader.setText(headerTitle);

        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, final ViewGroup viewGroup) {
        final String childText = (String) getChild(groupPosition, childPosition);
        final String headerTitle = (String) getGroup(groupPosition);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.downloaded_item, null);
        }

        TextView txtGroupItem = (TextView) view.findViewById(R.id.txtGroupItem);
        Button btnPlay = (Button) view.findViewById(R.id.btnPlay);
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);

        txtGroupItem.setText(childText.split("\\.(?=[^\\.]+$)")[0]);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DialogVideoActivity.class);
                intent.putExtra("videoPath", Constants.DOWNLOAD_PATH + headerTitle + "/" + childText);
                context.startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this lesson ?");
                builder.setTitle("Delete lesson");
                builder.setIcon(R.drawable.ic_warning_orange_24dp);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(Constants.DOWNLOAD_PATH + headerTitle + "/" + childText);
                        file.delete();
                        listDataChild.get(listDataHeader.get(groupPosition)).remove(childPosition);

                        if (getChildrenCount(groupPosition) == 0) {
                            listDataHeader.remove(groupPosition);
                            File folder = new File(Constants.DOWNLOAD_PATH + headerTitle);
                            folder.delete();
                        }

                        notifyDataSetChanged();
                    }
                });
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
