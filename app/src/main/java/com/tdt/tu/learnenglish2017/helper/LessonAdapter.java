package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;
import com.tdt.tu.learnenglish2017.R;
import com.tdt.tu.learnenglish2017.activity.DialogVideoActivity;
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.item.Lesson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import es.dmoral.toasty.Toasty;


/**
 * Created by Pham Thanh Tu on 18-Oct-17.
 */

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private List<Lesson> list = new ArrayList<>();
    private Context context;
    private String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LearnEnglish2017/Download/";

    public LessonAdapter(Context context, List<Lesson> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_row_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Lesson lesson = list.get(position);
        holder.title.setText(lesson.getTitle());
        holder.duration.setText(lesson.getDuration());
        Picasso.with(context).load(lesson.getImage()).into(holder.thumbnail);

        checkFileExist(holder, fileNameHandler(holder.title.getText().toString()));
        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFileExist(holder, fileNameHandler(holder.title.getText().toString())))
                    return;
                getYoutubeDownloadUrl(holder, lesson.getLink());
            }
        });

        holder.buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DialogVideoActivity.class);
                intent.putExtra("videoPath", videoPath + getVideoName(fileNameHandler(holder.title.getText().toString())));
                context.startActivity(intent);
            }
        });
    }

    private String fileNameHandler(String videoTitle, YtFile ytFile) {
        String fileName;
        if (videoTitle.length() > 55) {
            fileName = videoTitle.substring(0, 55) + "." + ytFile.getFormat().getExt();
        } else {
            fileName = videoTitle + "." + ytFile.getFormat().getExt();
        }
        fileName = fileName.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
        return fileName;
    }

    private String fileNameHandler(String fileName) {
        fileName = fileName.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
        return fileName;
    }

    private void getYoutubeDownloadUrl(final ViewHolder holder, final String youtubeLink) {
        new YouTubeExtractor(context) {
            @Override
            protected void onPreExecute() {
                holder.buttonDownload.setVisibility(View.INVISIBLE);
                holder.waitingCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                int iTag = 0;
                if (ytFiles == null) {
                    Toasty.error(context, "Error downloading video", Toast.LENGTH_SHORT).show();
                    holder.buttonDownload.setVisibility(View.VISIBLE);
                    holder.waitingCircle.setVisibility(View.GONE);
                } else {
                    for (int i = 0; i < ytFiles.size(); i++) {
                        if (ytFiles.keyAt(ytFiles.size() - 1) > 22)
                            iTag = 22;
                        else
                            iTag = ytFiles.keyAt(ytFiles.size() - 1);
                    }
                    YtFile ytFile = ytFiles.get(iTag);
                    holder.waitingCircle.setVisibility(View.GONE);
                    holder.waitingCircle.setEnabled(false);
                    holder.progressCircle.setVisibility(View.VISIBLE);
                    DownloadFileFromURLTask task = new DownloadFileFromURLTask(holder, fileNameHandler(holder.title.getText().toString(), ytFile));
                    task.execute(ytFile.getUrl());
                }
            }
        }.extract(youtubeLink, true, false);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private boolean checkFileExist(ViewHolder holder, String fileName) {

        File folder = new File(videoPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
                if (filename[0].equalsIgnoreCase(fileName)) {
                    holder.buttonDownload.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_orange_24dp));
                    holder.buttonDownload.setEnabled(false);
                    holder.buttonOpen.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        }
        return false;
    }

    private String getVideoName(String fileName) {

        String videoName = "";
        File folder = new File(videoPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
                if (filename[0].equalsIgnoreCase(fileName)) {
                    videoName = file.getName();
                }
            }
        }
        return videoName;
    }

    private class DownloadFileFromURLTask extends AsyncTask<String, String, String> {
        ViewHolder holder;
        String fileName;

        public DownloadFileFromURLTask(ViewHolder holder, String fileName) {
            this.holder = holder;
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(String... youtubeLink) {
            int count;
            try {
                URL url = new URL(youtubeLink[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lengthOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output = new FileOutputStream(videoPath + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            holder.progressCircle.setProgress(Integer.parseInt(progress[0]));

        }

        @Override
        protected void onPostExecute(String file_url) {
            holder.progressCircle.setVisibility(View.INVISIBLE);
            holder.progressCircle.setEnabled(false);
            holder.buttonDownload.setVisibility(View.VISIBLE);
            holder.buttonDownload.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_orange_24dp));
            holder.buttonDownload.setEnabled(false);
            holder.buttonOpen.setVisibility(View.VISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;
        ImageView thumbnail, buttonOpen, buttonDownload;
        DonutProgress progressCircle;
        ProgressBar waitingCircle;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.lessonTitle);
            duration = (TextView) itemView.findViewById(R.id.lessonDuration);
            thumbnail = (ImageView) itemView.findViewById(R.id.lessonThumbnail);
            buttonDownload = (ImageView) itemView.findViewById(R.id.lessonDownload);
            progressCircle = (DonutProgress) itemView.findViewById(R.id.progressCircle);
            waitingCircle = (ProgressBar) itemView.findViewById(R.id.waitingCircle);
            buttonOpen = (ImageView) itemView.findViewById(R.id.openVideo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selectedLessonLink = list.get(getLayoutPosition()).getLink();
                    if (LessonActivity.mYoutubePlayer != null) {
                        LessonActivity.mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        LessonActivity.mYoutubePlayer.loadVideo(selectedLessonLink);
                        LessonActivity.mYoutubePlayer.play();
                    }
                }
            });

        }

    }

}
