package cn.swust.firstcold.diglettgame;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageplay;
    private ProgressBar progressBarRun;
    private final int RUNMESSAGE = 1;
    private ImageView imgMusic;
    private int progress = 0;
    public static boolean isPlay = true;

    private Handler runHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            progressBarRun.setProgress(msg.arg1);
            if (msg.arg1==90){
                imageplay.setVisibility(View.VISIBLE);
                progressBarRun.setVisibility(View.INVISIBLE);

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        InitView();
        runProgressBar();
        startService(new Intent(SplashActivity.this,BcmusicService.class));
    }
    private void runProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()&&progress<100){
                    progress+=30;
                    Message msg = new Message();
                    msg.what = RUNMESSAGE;
                    msg.arg1 = progress;
                    runHandler.sendMessage(msg);
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void InitView() {
        imageplay = findViewById(R.id.imagePlay);
        imageplay.setOnClickListener(this);
        progressBarRun = findViewById(R.id.pgb_run);
        imgMusic = findViewById(R.id.img_sp_music);
        imgMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay){
                    imgMusic.setImageResource(R.mipmap.music_off);
                    isPlay = false;
                    stopPlayBacMusic();
                }else{
                    imgMusic.setImageResource(R.mipmap.music_on);
                    isPlay = true;
                    startPlayBacMusic();
                }
            }
        });
        setObjAnimation(imgMusic,"rotation",0f,365f,-1);
        setObjAnimation(imageplay,"scaleX",0.2f,1f,0);

    }

    private void setObjAnimation(Object obj,String name,float one,float two,int repeat) {
        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(obj,name,one,two);
        objectAnimator.setDuration(2500);/*动画时间*/
        objectAnimator.setRepeatCount(repeat);
        objectAnimator.start();
    }

    /**
     * 播放音乐
     */
    private void startPlayBacMusic() {
        Intent intent = new Intent(SplashActivity.this,BcmusicService.class);
        startService(intent);
    }
    /**
     * 停止播放音乐
     */
    private void stopPlayBacMusic() {
        Intent intent = new Intent(SplashActivity.this,BcmusicService.class);
        stopService(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imagePlay){
            Intent intentSound = new Intent(SplashActivity.this,BcsoundService.class);
            intentSound.putExtra("打地鼠界面",5);
            startService(intentSound);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}