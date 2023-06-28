package cn.swust.firstcold.diglettgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import cn.swust.firstcold.diglettgame.R;


public class MouseActivity extends AppCompatActivity implements View.OnClickListener {
    //锤子和地鼠控件
    private ImageView imageViewMouse,imageViewChui;
    //记录分数
    private TextView count;
    //设置游戏音乐关闭、开始或者暂停游戏、结束游戏
    private ImageButton imageBtnMusic,imageBtnPlay,imageBtnList,imageBtnEnd;
    //private boolean flag = false;
    //记录连击
    private int lcount = 0;
    //计时线程
    private CountTimeThread countTimeThread;
    //刷新地鼠位置线程
    private ReNewDiglettThread reNewDiglett;

    private ProgressBar progressBarTime;
    private float[][] position;
    private boolean isGameStart = false;
    private boolean isMusicStart = false;
    private int[] isContinue = {0,0,0,0,0};
    private int COMBO = 0;
    private final int MAX_CLICK = 5;
    //传递地鼠位置消息类型
    private final int MOUSE_POZITION = 1;
    //传递计时器消息类型
    private final int TIME = 2;

    private final int PROGRESS = 3;
     //简单模式时间限制
    private int time_limit_simple = 120;
    //中等模式时间限制
    private int time_limit_middle = 100;
    //困难模式时间限制
    private int time_limit_hard = 80;
    //简单模式刷新时间
    private int mode_simple = 900;
    //中等模式刷新时间
    private int mode_middle = 700;
    //困难模式刷新时间
    private int mode_hard = 500;
    //记录用户目前游戏得分
    private int grade = 0;
    //操作UI界面
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MOUSE_POZITION){
                imageViewMouse.setVisibility(View.VISIBLE);
                imageViewMouse.setX(position[msg.arg1][0]);
                imageViewMouse.setY(position[msg.arg1][1]);
                //count.setText("连击"+msg.arg2+"次");
            }else if (msg.what==TIME){
                  progressBarTime.setProgress(msg.arg1);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        initView();

        //  Toast.makeText(this, "高dp："+heightDp+"\n"+"宽DP"+widthDp, Toast.LENGTH_SHORT).show();

    }

    /**
     * 定义一个内部计时线程类,
     */
    private class CountTimeThread extends Thread{
            int time_limit;
        /**
         *
         * @param time_limit 根据用户选择的游戏模式设置对应时间限制
         */
        public CountTimeThread(int time_limit) {
            this.time_limit = time_limit;
        }
        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()&&isGameStart){
                   this.time_limit--;
                   Message msg = new Message();
                   msg.what = TIME;
                   msg.arg1 = time_limit;
                   handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建一个用于更新地鼠位置的线程类
     */
    private class ReNewDiglettThread extends Thread{
            int mode;
        public ReNewDiglettThread(int mode) {
            this.mode = mode;
        }

        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()&&isGameStart){
                Message msg = new Message();
                msg.what = MOUSE_POZITION;
                msg.arg1 = new Random().nextInt(position.length);
                lcount = (lcount+1)%5;
                isContinue[lcount] = 0;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(mode);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *绑定控件
     */
    private void initView() {
        initPosition();
        progressBarTime = findViewById(R.id.pgb_time);

        //绑定图片按钮控件并设计监听事件
        progressBarTime.setMax(time_limit_simple);
        imageViewMouse = findViewById(R.id.iv_mouse);
        imageViewChui = findViewById(R.id.iv_chuizi);

        imageBtnMusic = findViewById(R.id.ib_music);
        imageBtnMusic.setOnClickListener(this);
        imageBtnPlay = findViewById(R.id.ib_play);
        imageBtnPlay.setOnClickListener(this);
        imageBtnList = findViewById(R.id.ib_list);
        imageBtnList.setOnClickListener(this);
        imageBtnEnd = findViewById(R.id.ib_end);
        imageBtnEnd.setOnClickListener(this);
        count = findViewById(R.id.tv_count);
        AssetManager assetManager = this.getAssets();
        count.setTypeface(Typeface.createFromAsset(assetManager,"fonts/FZYTK.TTF"));
        //设置用户点击老鼠后的响应事件
        imageViewMouse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //点击地鼠将对应位置刷新地鼠
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    isContinue[lcount] = 1;
                    if(lcount==0){
                        if(isContinue[MAX_CLICK-1]==1){
                            COMBO++;
                        }else{
                            COMBO = 0;
                        }
                    }else{
                        if(isContinue[(lcount-1)%5]==1){
                            COMBO++;
                        }else{
                            COMBO = 0;
                        }
                    }
                    imageViewMouse.setVisibility(View.INVISIBLE);

                    imageViewChui.setX(event.getRawX()-imageViewChui.getWidth()/2);
                    imageViewChui.setY(event.getRawY()-imageViewChui.getHeight()-70);
                    imageViewChui.setVisibility(View.VISIBLE);
                    count.setText("COMB x" + COMBO);
                }
                //用户抬起手后，将锤子设置为不可见
                else if(event.getAction()==MotionEvent.ACTION_UP) {
                    imageViewChui.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 初始化每个洞口的位置坐标
     */
    private void initPosition() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightPx = displayMetrics.heightPixels;
        int widthPx = displayMetrics.widthPixels;
        float density = displayMetrics.density;
        //高827  宽392
        float heightDp = heightPx / density;
        float widthDp = widthPx / density;
        int STATUS_BAR_HEIGHT = getStatusBarHeight(this);
        position = new float[][]{{60 * widthDp / 392, (float) (630 * heightDp / 830+STATUS_BAR_HEIGHT)}, {425 * widthDp / 392, (float) (630 * heightDp / 830+STATUS_BAR_HEIGHT)}, {780*widthDp/392, (float) (625 * heightDp / 830+STATUS_BAR_HEIGHT)},
                {45 * widthDp / 392, (float) (1015 * heightDp / 830+STATUS_BAR_HEIGHT*1.5)}, {425 * widthDp / 392, (float) (1015 * heightDp / 830+STATUS_BAR_HEIGHT*1.5)}, {780*widthDp/392, (float) (1015 * heightDp / 830+STATUS_BAR_HEIGHT*1.5)},
                {45 * widthDp / 392, (float) (1435 * heightDp / 830+STATUS_BAR_HEIGHT*1.5)}, {415 * widthDp / 392, (float) (1435 * heightDp / 830+STATUS_BAR_HEIGHT*1.5)}, {780*widthDp/392, (float) (1435 * heightDp / 830+STATUS_BAR_HEIGHT*1.5)}};
    }

    /*
       设计监听屏幕触摸事件，用于显示锤子
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                imageViewChui.setX(event.getRawX()-imageViewChui.getWidth()/2);
                imageViewChui.setY(event.getRawY()-imageViewChui.getHeight());
                imageViewChui.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_UP:
                imageViewChui.setVisibility(View.INVISIBLE);
                break;
        }
        return super.onTouchEvent(event);
    }

    //ImageButton的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_play:
                if(isGameStart){
                    imageBtnPlay.setImageResource(R.mipmap.btn_start);
                    isGameStart = false;
                }else{
                    imageBtnPlay.setImageResource(R.mipmap.btn_pause);
                    isGameStart = true;
                    // 开启一个线程用于游戏倒计时线程对象  默认为简单模式
                    countTimeThread = new CountTimeThread(time_limit_simple);
                    //创建更新地鼠位置线程对象 默认模式为简单
                    reNewDiglett = new ReNewDiglettThread(mode_simple);
                    reNewDiglett.start();
                    countTimeThread.start();
                    
                }
                break;
            case R.id.ib_music:
                if(isMusicStart){
                    imageBtnMusic.setImageResource(R.mipmap.music_on);
                    isMusicStart = false;
                }else{
                    imageBtnMusic.setImageResource(R.mipmap.music_off);
                    isMusicStart  =true;
                }
                break;
            case R.id.ib_end:
                isGameStart = false;
                imageBtnPlay.setImageResource(R.mipmap.btn_start);

        }
    }
}