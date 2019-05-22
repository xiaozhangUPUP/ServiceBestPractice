package com.android.servicebestpractice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();
    private DownloadTask downloadTask;
    private String downloadUrl;
    private DownloadBinder binder = new DownloadBinder();

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            showToast("Download Success");
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            showToast("Download Failed");
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            showToast("Canceled");
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            showToast("Paused");
        }
    };


    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    class DownloadBinder extends Binder {
        public void StartDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(downloadListener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("Downloading...", 0));
                showToast("Downloading...");
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pausedDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String dectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(dectory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    showToast("Canceled");
                }
            }
        }
    }


    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    private void showToast(String msg) {
        Toast.makeText(DownloadService.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "-----------------onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "-----------------onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "-----------------onDestroy: ");
    }
}
