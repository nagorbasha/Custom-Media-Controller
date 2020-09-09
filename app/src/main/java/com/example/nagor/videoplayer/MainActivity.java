package com.example.nagor.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SurfaceView surfaceView;

    MediaPlayer mediaPlayer;

    TextView startTime, endTime;

    private ProgressBar progressBar;

    ImageView playPause, next, previous, repeat;

    private boolean isHandlerEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        surfaceView = findViewById(R.id.surfaceView);

        next = findViewById(R.id.next);

        playPause = findViewById(R.id.play_pause);

        previous = findViewById(R.id.rewind);

        repeat = findViewById(R.id.repeat);

        next.setOnClickListener(this);

        playPause.setOnClickListener(this);

        previous.setOnClickListener(this);

        repeat.setOnClickListener(this);


        mediaPlayer = new MediaPlayer();

        surfaceView.getHolder().addCallback(callback);

        startTime = findViewById(R.id.start_time);

        endTime = findViewById(R.id.end_time);

        progressBar = findViewById(R.id.progress_bar);

    }

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            playPause.setImageResource(R.drawable.pause);
            endTime.setText(AppUtils.formatTime(mediaPlayer.getDuration()));
            progressBar.setMax(mediaPlayer.getDuration());
            startPlayBackHandler();
        }
    };
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            playPause.setImageResource(R.drawable.play);
            stopPlayBackHandler();
        }
    };

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                startTime.setText(AppUtils.formatTime(mediaPlayer.getCurrentPosition()));
                progressBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 1000);
        }
    };

    private void startPlayBackHandler() {
        if (!isHandlerEnabled) {
            handler.post(runnable);
            isHandlerEnabled = true;
        }
    }


    private void stopPlayBackHandler() {
        if (isHandlerEnabled) {
            handler.removeCallbacks(runnable);
            isHandlerEnabled = false;
        }
    }


    MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            int progress = (mediaPlayer.getDuration() * i) / 100;
            Log.e("Buffer List", " buff " + i + " perc "+progress);
            progressBar.setSecondaryProgress(progress);
        }
    };


    MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
            Log.e("Media OnInfo", " type " + i);
            return false;
        }
    };

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.sample);

            try {
                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assetFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setSurface(surfaceHolder.getSurface());


            mediaPlayer.setOnPreparedListener(preparedListener);

            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);

            mediaPlayer.setOnCompletionListener(onCompletionListener);

            mediaPlayer.setOnInfoListener(onInfoListener);

            mediaPlayer.prepareAsync();
        }


        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                startForward();
                break;

            case R.id.play_pause:
                playPause();

                break;

            case R.id.rewind:
                startRewind();
                break;

            case R.id.repeat:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isLooping()) {
                        mediaPlayer.setLooping(false);
                        repeat.setImageResource(R.drawable.repeat);
                    } else {
                        mediaPlayer.setLooping(true);
                        repeat.setImageResource(R.drawable.repeat_enabled);
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        if(mediaPlayer !=null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPause.setImageResource(R.drawable.play);
        }
        super.onStop();
    }

    private void playPause() {
        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPause.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                playPause.setImageResource(R.drawable.pause);
            }
    }

    private void startRewind() {
        if (mediaPlayer != null) {
            int curDur = mediaPlayer.getCurrentPosition();
            int seekPostion = curDur - 10000;
            if (seekPostion <= 0) {
                mediaPlayer.seekTo(0);
            } else {
                mediaPlayer.seekTo(seekPostion);
            }
        }
    }

    private void startForward() {
        if (mediaPlayer != null) {
            int curDur = mediaPlayer.getCurrentPosition();
            int seekPostion = curDur + 10000;
            if (seekPostion > mediaPlayer.getDuration()) {
                if (mediaPlayer.isLooping()) {
                    mediaPlayer.seekTo(0);
                } else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            } else {
                mediaPlayer.seekTo(seekPostion);
            }
        }
    }
}
