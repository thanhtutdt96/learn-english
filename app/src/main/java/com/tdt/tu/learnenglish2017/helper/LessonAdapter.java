package com.tdt.tu.learnenglish2017.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
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
import com.tdt.tu.learnenglish2017.activity.LessonActivity;
import com.tdt.tu.learnenglish2017.item.Lesson;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static android.content.Context.DOWNLOAD_SERVICE;

//import com.commit451.youtubeextractor.YouTubeExtractionResult;
//import com.commit451.youtubeextractor.YouTubeExtractor;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

/**
 * Created by Pham Thanh Tu on 18-Oct-17.
 */

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {
    //private final YouTubeExtractor extractor = YouTubeExtractor.create();
    List<Lesson> list;
    Context context;
    String temp = "https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg";
    boolean isFinished = false;
    String videoDownloadLink;

    public LessonAdapter(Context context, List<Lesson> list) {
        this.context = context;
        this.list = list;
    }
//
//        private void startDownload() {
//            new DownloadFileAsync().execute(temp);
//        }

//        class DownloadFileAsync extends AsyncTask<String, String, String> {
//            ProgressDialog dialog;
//
//            @Override
//            protected String doInBackground(String... strings) {
//                int count;
//                try {
//
//
//                    URL url = new URL(strings[0]);
//                    URLConnection connection = url.openConnection();
//                    connection.connect();
//
//                    int fileSize = connection.getContentLength();
//
//                    InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
//                    OutputStream outputStream = new FileOutputStream("sdcard/Download/idm.exe");
//
//                    byte data[] = new byte[1024];
//                    long total = 0;
//                    while ((count = inputStream.read(data)) != -1) {
//                        total += count;
//                        publishProgress("" + (int) (total * 100 / fileSize));
//                        outputStream.write(data, 0, count);
//                    }
//
//                    outputStream.flush();
//                    outputStream.close();
//                    inputStream.close();
//                } catch (Exception e) {
//                    Log.e("Error: ", e.getMessage());
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                dialog = new ProgressDialog(context);
//                dialog.setMessage("Downloading file. Please wait...");
//                dialog.setIndeterminate(false);
//                dialog.setMax(100);
//                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                dialog.setCancelable(true);
//                dialog.show();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                dialog.dismiss();
//                Toast.makeText(context, "Download Completed!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            protected void onProgressUpdate(String... values) {
//                Log.d("progress", values[0]);
//                dialog.setProgress(Integer.parseInt(values[0]));
//            }
//        }

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
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  getYoutubeDownloadUrl(holder, "https://www.youtube.com/watch?v=" + lesson.getLink());
                getYoutubeDownloadUrl(holder, lesson.getLink());

//                getLinkVideo(holder, lesson.getLink());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        downloadFromUrl(holder, holder.linkDownload.getText().toString(), lesson.getTitle(), fileNameHandlder(lesson.getTitle()));
//                    }
//                }, 2000);
            }
        });
    }


    private String fileNameHandlder(String videoTitle, YtFile ytFile) {
        String fileName;
        if (videoTitle.length() > 55) {
            fileName = videoTitle.substring(0, 55) + "." + ytFile.getFormat().getExt();
        } else {
            fileName = videoTitle + "." + ytFile.getFormat().getExt();
        }
        fileName = fileName.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
        return fileName;
    }

    private void getYoutubeDownloadUrl(final ViewHolder holder, final String youtubeLink) {
        new YouTubeExtractor(context) {
            @Override
            protected void onPreExecute() {
                holder.btnDownload.setVisibility(View.INVISIBLE);
                holder.waitingCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                int iTag = 0;
                if (ytFiles == null) {
                    Toast.makeText(context, "Error downloading video", Toast.LENGTH_SHORT).show();
                    holder.btnDownload.setVisibility(View.VISIBLE);
                    holder.waitingCircle.setVisibility(View.GONE);
                } else {
                    holder.waitingCircle.setVisibility(View.GONE);
                    holder.progressCircle.setVisibility(View.VISIBLE);
                    for (int i = 0; i < ytFiles.size(); i++) {
                        if (ytFiles.keyAt(ytFiles.size() - 1) > 22)
                            iTag = 22;
                        else
                            iTag = ytFiles.keyAt(ytFiles.size() - 1);
                    }
                    YtFile ytFile = ytFiles.get(iTag);
                    downloadVideoFromUrl(holder, ytFile.getUrl(), holder.title.getText().toString(), fileNameHandlder(vMeta.getTitle(), ytFile));
                }
            }
        }.extract(youtubeLink, true, false);

    }

    private void downloadVideoFromUrl(final ViewHolder holder, String youtubeUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeUrl);
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Lesson \"" + downloadTitle + "\" is being downloaded....");
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir("/LearnEnglish2017/Download", fileName);

        final long downloadId = downloadManager.enqueue(request);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);
                cursor.moveToFirst();
                int sizeDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int sizeTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                cursor.close();
                final int progressNumber = (sizeDownloaded * 100 / sizeTotal);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        holder.progressCircle.setProgress(progressNumber);
                    }
                });

            }

        }, 0, 10);
        if (holder.progressCircle.getProgress() == 100) {
            Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show();
            holder.btnDownload.setBackground(context.getResources().getDrawable(R.drawable.ic_check_green_24dp));
            holder.btnDownload.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, linkDownload;
        ImageView thumbnail, btnDownload;
        DonutProgress progressCircle;
        ProgressBar waitingCircle;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.lesson_title);
            duration = (TextView) itemView.findViewById(R.id.lesson_duration);
            thumbnail = (ImageView) itemView.findViewById(R.id.lesson_thumbnail);
            btnDownload = (ImageView) itemView.findViewById(R.id.lesson_download);
            progressCircle = (DonutProgress) itemView.findViewById(R.id.progressCircle);
            waitingCircle = (ProgressBar) itemView.findViewById(R.id.waitingCircle);
            linkDownload = (TextView) itemView.findViewById(R.id.linkDownload);

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
