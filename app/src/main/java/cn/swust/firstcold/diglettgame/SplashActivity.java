package cn.swust.firstcold.diglettgame;

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
    private int progress = 0;
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
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imagePlay){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}