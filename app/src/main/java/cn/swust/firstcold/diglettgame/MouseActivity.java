package cn.swust.firstcold.diglettgame;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class MouseActivity extends AppCompatActivity implements View.OnClickListener {
    //锤子和地鼠控件
    private ImageView imageViewMouse,imageViewChui;
    //设置游戏音乐关闭、开始或者暂停游戏、排行榜、结束游戏
    private ImageButton imageBtnMusic,imageBtnPlay,imageBtnList,imageBtnEnd;
    private TextView tv_count;
    //标志连击
    private int lcount = 0;
    //计时线程
    private CountTimeThread countTimeThread;
    //刷新地鼠位置线程
    private ReNewDiglettThread reNewDiglett;
    //当前关卡数
    private int level = 1;
    //当前账户
    private String account;
    private ProgressBar progressBarTime;
    private float[][] position;
    private boolean isGameStart = false;
    private boolean isMusicStart = false;
    private int[] isContinue = {0,0,0,0,0};
    //记录连击数量
    private int COMBO = 0;
    private Intent intentSound ;
    //private final Intent intentMusic = new Intent(MouseActivity.this, BcmusicService.class);
    private final int MAX_CLICK = 5;
    //传递地鼠位置消息类型
    private final int MOUSE_POZITION = 1;
    //传递计时器消息类型
    private final int TIME = 2;

    private final int PROGRESS = 3;
     //简单模式时间限制
    private int time_limit = 120;
    //简单模式刷新时间
    private int time_renew = 900;
    //记录用户目前游戏得分
    private int grade = 0;
    private static final int ACTION_PLAY_CHUI = 1;
    private static final int ACTION_PLAY_SHU = 2;
    //操作UI界面
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MOUSE_POZITION){
                imageViewMouse.setVisibility(View.VISIBLE);
                imageViewMouse.setX(position[msg.arg1][0]);
                imageViewMouse.setY(position[msg.arg1][1]);
                playSound(ACTION_PLAY_SHU);
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
        initLevel();

        //  Toast.makeText(this, "高dp："+heightDp+"\n"+"宽DP"+widthDp, Toast.LENGTH_SHORT).show();

    }

    private void initLevel() {
        CurrentNum = Integer.valueOf(getIntent().getStringExtra(LevelSelectionActivity.LEVEL))+1;
        account = getIntent().getStringExtra(MainActivity.ACCOUNT);
        Toast.makeText(this, "关卡："+CurrentNum+"账户："+account, Toast.LENGTH_SHORT).show();
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
        intentSound = new Intent(this,BcsoundService.class);
        progressBarTime = findViewById(R.id.pgb_time);
        //绑定图片按钮控件并设计监听事件
        progressBarTime.setMax(time_limit);

        imageViewMouse = findViewById(R.id.iv_mouse);
        imageViewChui = findViewById(R.id.iv_chuizi);

        imageBtnMusic = findViewById(R.id.ib_music);
        imageBtnMusic.setOnClickListener(this);
        imageBtnPlay = findViewById(R.id.ib_play);
        imageBtnPlay.setOnClickListener(this);
        //排行榜
        imageBtnList = findViewById(R.id.ib_list);
        imageBtnList.setOnClickListener(this);
        imageBtnEnd = findViewById(R.id.ib_end);
        imageBtnEnd.setOnClickListener(this);
        tv_count = findViewById(R.id.tv_count);
        AssetManager assetManager = this.getAssets();
        tv_count.setTypeface(Typeface.createFromAsset(assetManager,"fonts/FZYTK.TTF"));
        //设置用户点击老鼠后的响应事件
        imageViewMouse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    grade++;
                    isContinue[lcount] = 1;
                    if(lcount==0){
                        if(isContinue[MAX_CLICK-1]==1){
                            COMBO++;
                        }else{
                            grade+=COMBO*2;
                            COMBO = 0;
                        }
                    }else{
                        if(isContinue[(lcount-1)%5]==1){
                            COMBO++;
                        }else{
                            grade+=COMBO*2;
                            COMBO = 0;
                        }
                    }
                    imageViewMouse.setVisibility(View.INVISIBLE);
                    imageViewChui.setX(event.getRawX()-imageViewChui.getWidth()/2);
                    imageViewChui.setY(event.getRawY()-imageViewChui.getHeight()-70);
                    imageViewChui.setVisibility(View.VISIBLE);
                    playSound(ACTION_PLAY_CHUI);
                    tv_count.setText("分数: "+grade);
                    //tv_count.setText("COMB x" + COMBO);
                }
                //用户抬起手后，将锤子设置为不可见
                else if(event.getAction()==MotionEvent.ACTION_UP) {
                    imageViewChui.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    /**
     *
     * @param action 播放音效类型
     */
    protected void playSound(int action){
        intentSound.putExtra("打地鼠界面",action);
        startService(intentSound);
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
                imageViewChui.setY(event.getRawY()-imageViewChui.getHeight()/3);
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
                    countTimeThread = new CountTimeThread(time_limit);
                    //创建更新地鼠位置线程对象 默认模式为简单
                    reNewDiglett = new ReNewDiglettThread(time_renew);
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