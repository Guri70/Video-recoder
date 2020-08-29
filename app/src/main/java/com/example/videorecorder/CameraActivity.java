package com.example.videorecorder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videorecorder.base.BaseActivity;
import com.example.videorecorder.util.CameraHelper;
import com.example.videorecorder.util.CountUpTimer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.internal.Constants;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CameraActivity extends BaseActivity implements
        CameraVideoFragment.OnFragmentInteractionListener {

    @BindView(R.id.iv_action)
    public ImageView ivActionButton;
    @BindView(R.id.iv_gallery)
    public ImageView ivGallery;
    @BindView(R.id.iv_flip)
    public ImageView ivFlipButton;

    @BindView(R.id.tv_video_timer)
    public TextView tvVideoTimer;

    private CountDownTimer cTimer = null;

    private boolean isRecordingVideo;

    private CameraVideoFragment videoFragmentInstance;

    private CountUpTimer timer;

    private String fileN = null ;

    @Override
    protected int getContentId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CameraActivityPermissionsDispatcher.needPermissionsWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
    public void needPermissions() {
        videoFragmentInstance= CameraVideoFragment.newInstance();
        switchFragment(R.id.frame_layout, videoFragmentInstance);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startVideoTimer() {
        tvVideoTimer.setVisibility(View.VISIBLE);
        timer = new CountUpTimer(300000) {
            public void onTick(int second) {
                tvVideoTimer.setText(String.valueOf(second));
            }
        };
        timer.start();
    }

    public void cancelTimer() {
        tvVideoTimer.setVisibility(View.GONE);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @OnClick(R.id.iv_flip)
    public void onClickFlip() {
        if (videoFragmentInstance == null) {
            return;
        }
        videoFragmentInstance.flipCamera();
    }

    @OnClick(R.id.iv_action)
    public void onClickAction() {
        if (videoFragmentInstance == null) {
            return;
        }
        ivFlipButton.setVisibility(View.VISIBLE);
        if (isRecordingVideo) {
            isRecordingVideo = false;
            videoFragmentInstance.stopRecordingVideo();
        } else {
            isRecordingVideo = true;
            videoFragmentInstance.startRecordingVideo();
            ivFlipButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSuccessVideoRecorded(String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            Toast.makeText(this,"Empty path",Toast.LENGTH_SHORT).show();
            return;
        }
        fileN=filepath;
        Toast.makeText(this,filepath,Toast.LENGTH_LONG).show();
        File f = new File(filepath);
        addVideo(f);
    }

    public Uri addVideo(File videoFile) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        return getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

}