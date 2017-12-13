package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
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
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import es.dmoral.toasty.Toasty;


/**
 * Created by Pham Thanh Tu on 18-Oct-17.
 */

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private List<Lesson> list;
    private Context context;
    private String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LearnEnglish2017/Download/";

    private ThinDownloadManager downloadManager;
    private int downloadId = 0;
    private int downloadCount;

    public LessonAdapter(Context context, List<Lesson> list) {
        this.context = context;
        this.list = list;
        downloadManager = new ThinDownloadManager();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_row_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Lesson lesson = list.get(position);
        holder.title.setText(lesson.getTitle());
        holder.duration.setText(lesson.getDuration());
        Picasso.with(context).load(lesson.getImage()).into(holder.thumbnail);

        checkFileExist(holder, fileNameHandler(holder.title.getText().toString()));
        holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadCount++;
                if (downloadCount > 2) {
                    Toasty.warning(context, "You are allowed to download only 2 lesson at a time!", Toast.LENGTH_SHORT).show();
                    return;
                }
                holder.progressCircle.setVisibility(View.VISIBLE);
                holder.buttonDownload.setVisibility(View.INVISIBLE);
                getYoutubeDownloadUrl(holder, lesson.getLink());
            }
        });

        holder.buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DialogVideoActivity.class);
                Log.d("videoName", videoPath + getVideoName(fileNameHandler(holder.title.getText().toString())));
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
                    downloadVideoFromUrl(holder, ytFile.getUrl(), fileNameHandler(holder.title.getText().toString(), ytFile));
                }
            }
        }.extract(youtubeLink, true, false);

    }

    private void downloadVideoFromUrl(final ViewHolder holder, String youtubeUrl, String fileName) {

        Uri destinationUri = Uri.parse(videoPath + fileName);
        Uri downloadUri = Uri.parse(youtubeUrl);
        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest request) {
                        int id = request.getDownloadId();
                        downloadCount = 0;
                        if (id == downloadId) {
                            holder.progressCircle.setVisibility(View.INVISIBLE);
                            holder.buttonDownload.setVisibility(View.VISIBLE);
                            holder.buttonDownload.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_orange_24dp));
                            holder.buttonOpen.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
                        int id = request.getDownloadId();
                        if (id == downloadId) {
                            Toast.makeText(context, "Download id: " + id + " Failed: ErrorCode " + errorCode + ", " + errorMessage, Toast.LENGTH_LONG).show();
                            holder.progressCircle.setVisibility(View.INVISIBLE);
                            holder.buttonDownload.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
                        holder.waitingCircle.setVisibility(View.GONE);
                        holder.progressCircle.setVisibility(View.VISIBLE);
                        int id = request.getDownloadId();

                        if (id == downloadId) {
                            holder.progressCircle.setProgress(progress);
                        }
                    }
                });
        if (downloadManager.query(downloadId) == DownloadManager.STATUS_NOT_FOUND) {
            downloadId = downloadManager.add(downloadRequest);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    downloadId = downloadManager.add(downloadRequest);
                }
            }, 2000);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void checkFileExist(ViewHolder holder, String fileName) {

        File folder = new File(videoPath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
                if (filename[0].equalsIgnoreCase(fileName)) {
                    holder.buttonDownload.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_orange_24dp));
                    holder.buttonDownload.setEnabled(false);
                    holder.buttonOpen.setVisibility(View.VISIBLE);
                }
            }
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;
        ImageView thumbnail, buttonDownload, buttonOpen;
        DonutProgress progressCircle;
        ProgressBar waitingCircle;

        public ViewHolder(View itemView) {
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
