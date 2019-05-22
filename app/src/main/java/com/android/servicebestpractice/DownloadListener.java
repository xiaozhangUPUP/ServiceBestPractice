package com.android.servicebestpractice;

/**
 * Created by zhangqi on 2019/5/22
 */
public interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onCanceled();

    void onPaused();
}
