package com.lzy.okhttpdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okhttpdemo.utils.ApkUtils;
import com.lzy.okhttpserver.download.DownloadInfo;
import com.lzy.okhttpserver.listener.DownloadListener;
import com.lzy.okhttpserver.download.DownloadManager;
import com.lzy.okhttpserver.download.DownloadService;
import com.lzy.okhttpdemo.Bean.ApkInfo;
import com.lzy.okhttpdemo.R;

import java.io.File;

public class DesActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView downloadSize;
    private TextView tvProgress;
    private TextView netSpeed;
    private ProgressBar pbProgress;
    private Button download;
    private Button remove;
    private Button restart;
    private MyListener listener;
    private DownloadInfo downloadInfo;
    private ApkInfo apk;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_details);
        apk = (ApkInfo) getIntent().getSerializableExtra("apk");
        downloadManager = DownloadService.getDownloadManager(this);

        ImageView icon = (ImageView) findViewById(R.id.icon);
        TextView name = (TextView) findViewById(R.id.name);
        downloadSize = (TextView) findViewById(R.id.downloadSize);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        netSpeed = (TextView) findViewById(R.id.netSpeed);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        download = (Button) findViewById(R.id.start);
        remove = (Button) findViewById(R.id.remove);
        restart = (Button) findViewById(R.id.restart);

        Glide.with(this).load(apk.getIconUrl()).error(R.mipmap.ic_launcher).into(icon);
        name.setText(apk.getName());
        download.setOnClickListener(this);
        remove.setOnClickListener(this);
        restart.setOnClickListener(this);
        listener = new MyListener();

        downloadInfo = downloadManager.getTaskByUrl(apk.getUrl());
        if (downloadInfo != null) {
            //????????????????????????????????????????????????????????????????????????
            downloadInfo.setListener(listener);
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            refreshUi(downloadInfo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (downloadInfo != null) refreshUi(downloadInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadInfo != null) downloadInfo.removeListener();
    }

    @Override
    public void onClick(View v) {
        //????????????????????????????????????????????????
        downloadInfo = downloadManager.getTaskByUrl(apk.getUrl());
        if (v.getId() == download.getId()) {
            if (downloadInfo == null) {
                downloadManager.addTask(apk.getUrl(), listener);
                return;
            }
            switch (downloadInfo.getState()) {
                case DownloadManager.PAUSE:
                case DownloadManager.NONE:
                case DownloadManager.ERROR:
                    downloadManager.addTask(downloadInfo.getUrl(), listener);
                    break;
                case DownloadManager.DOWNLOADING:
                    downloadManager.pauseTask(downloadInfo.getUrl());
                    break;
                case DownloadManager.FINISH:
                    if (ApkUtils.isAvailable(this, new File(downloadInfo.getTargetPath()))) {
                        ApkUtils.uninstall(this, ApkUtils.getPackageName(this, downloadInfo.getTargetPath()));
                    } else {
                        ApkUtils.install(this, new File(downloadInfo.getTargetPath()));
                    }
                    break;
            }
        } else if (v.getId() == remove.getId()) {
            if (downloadInfo == null) {
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadManager.removeTask(downloadInfo.getUrl());
            downloadSize.setText("--M/--M");
            netSpeed.setText("---/s");
            tvProgress.setText("--.--%");
            pbProgress.setProgress(0);
            download.setText("??????");
        } else if (v.getId() == restart.getId()) {
            if (downloadInfo == null) {
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadManager.restartTask(downloadInfo.getUrl());
        }
    }

    private class MyListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            refreshUi(downloadInfo);
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            System.out.println("onFinish");
            Toast.makeText(DesActivity.this, "????????????:" + downloadInfo.getTargetPath(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            System.out.println("onError");
            if (errorMsg != null)
                Toast.makeText(DesActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUi(DownloadInfo downloadInfo) {
        String downloadLength = Formatter.formatFileSize(DesActivity.this, downloadInfo.getDownloadLength());
        String totalLength = Formatter.formatFileSize(DesActivity.this, downloadInfo.getTotalLength());
        downloadSize.setText(downloadLength + "/" + totalLength);
        String networkSpeed = Formatter.formatFileSize(DesActivity.this, downloadInfo.getNetworkSpeed());
        netSpeed.setText(networkSpeed + "/s");
        tvProgress.setText((Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
        pbProgress.setMax((int) downloadInfo.getTotalLength());
        pbProgress.setProgress((int) downloadInfo.getDownloadLength());
        switch (downloadInfo.getState()) {
            case DownloadManager.NONE:
                download.setText("??????");
                break;
            case DownloadManager.DOWNLOADING:
                download.setText("??????");
                break;
            case DownloadManager.PAUSE:
                download.setText("??????");
                break;
            case DownloadManager.WAITING:
                download.setText("??????");
                break;
            case DownloadManager.ERROR:
                download.setText("??????");
                break;
            case DownloadManager.FINISH:
                if (ApkUtils.isAvailable(DesActivity.this, new File(downloadInfo.getTargetPath()))) {
                    download.setText("??????");
                } else {
                    download.setText("??????");
                }
                break;
        }
    }
}
