package cn.swust.firstcold.diglettgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class MouseActivity extends AppCompatActivity implements View.OnClickListener {
    //锤子和地鼠控件
    private ImageView imageViewMouse,imageViewChui;
    //设置游戏音乐关闭、开始或者暂停游戏、排行榜、结束游戏、返回
    private ImageButton imageBtnMusic,imageBtnPlay,imageBtnList,imageBtnEnd,imageButtonBack;
    private TextView tv_count,tv_curtime;
    //标志连击
    private int lcount = 0;

    static String recordCount = "0"; // 用于存储得分

    public static final String CONFIG_NAME = "user_config";
    public static final int CONFIG_MODE = Context.MODE_PRIVATE;

    //为每个关卡设置一个变量，用于分别用SharedPreferences方法存储各个关卡的最高分
    public static final String SCORE1 = "user_Score1";
    public static final String SCORE2 = "user_Score2";
    public static final String SCORE3 = "user_Score3";
    public static final String SCORE4 = "user_Score4";
    public static final String SCORE5 = "user_Score5";
    public static final String SCORE6 = "user_Score6";
    public static final String SCORE7 = "user_Score7";
    public static final String SCORE8 = "user_Score8";
    public static final String SCORE9 = "user_Score9";
    public static final String SCORE10 = "user_Score10";

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
                  time_limit = msg.arg1;
                  progressBarTime.setProgress(msg.arg1);
                  tv_curtime.setText(""+msg.arg1+"秒");
                  if (msg.arg1==0){
                      grade +=COMBO*2;
                      saveConfig(level);
                  }
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
        level = Integer.valueOf(getIntent().getStringExtra(LevelSelectionActivity.LEVEL))+1;
        account = getIntent().getStringExtra(MainActivity.ACCOUNT);
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
            while (!Thread.currentThread().isInterrupted()&&isGameStart&&this.time_limit>=0){
                   this.time_limit--;
                   Message msg = new Message();
                   msg.what = TIME;
                   msg.arg1 = this.time_limit;
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
        tv_curtime = findViewById(R.id.tv_curtime);
        intentSound = new Intent(this,BcsoundService.class);
        progressBarTime = findViewById(R.id.pgb_time);
        //绑定图片按钮控件并设计监听事件
        progressBarTime.setMax(time_limit);

        imageViewMouse = findViewById(R.id.iv_mouse);
        imageViewChui = findViewById(R.id.iv_chuizi);
        imageButtonBack = findViewById(R.id.ib_return);
        imageButtonBack.setOnClickListener(this);
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
        //设置字体
        AssetManager assetManager = this.getAssets();

        tv_count.setTypeface(Typeface.createFromAsset(assetManager,"fonts/FZYTK.TTF"));
        //设置用户点击老鼠后的响应事件
        imageViewMouse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN&&isGameStart){
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
                    tv_count.setText(""+grade+"分");
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

    /**
     * 播放音乐
     */
    private void startPlayBacMusic() {
        Intent intent = new Intent(MouseActivity.this,BcmusicService.class);
        startService(intent);
    }
    /**
     * 停止播放音乐
     */
    private void stopPlayBacMusic() {
        Intent intent = new Intent(MouseActivity.this,BcmusicService.class);
        stopService(intent);
    }

    //ImageButton的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_play:
                if(isGameStart){
                    imageBtnPlay.setImageResource(R.mipmap.btn_start);
                    isGameStart = false;
                    stopService(intentSound);
                }else{
                    imageBtnPlay.setImageResource(R.mipmap.btn_pause);
                    isGameStart = true;
                    progressBarTime.setVisibility(View.VISIBLE);
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
                    imageBtnMusic.setImageResource(R.mipmap.music_off);
                    isMusicStart = false;
                    stopPlayBacMusic();
                }else{
                    imageBtnMusic.setImageResource(R.mipmap.music_on);
                    isMusicStart  =true;
                    startPlayBacMusic();
                }
                break;
            case R.id.ib_end:
                isGameStart = false;
                imageBtnPlay.setImageResource(R.mipmap.btn_start);
                loadConfig(level);//先读取一次排行榜中已经存储的内容，即使不进行游戏也可以点击排行榜观看已存储的数据
                saveConfig(level);  //若没进行游戏，则显示历史保存的记录。若进行了游戏，则保存最新游戏的记录
                COMBO = 0;
                grade = 0;
                stopService(intentSound);
                break;
            case R.id.ib_list:
                loadConfig(level);//先读取一次排行榜中已经存储的内容
                saveConfig(level);  //若没进行游戏，则显示历史保存的记录。若进行了游戏，则保存最新游戏的记录
                startActivity(new Intent(MouseActivity.this, rankingListActivity.class));
                break;
            case R.id.ib_return:
                finish();
                break;
        }
    }

    protected void saveConfig(int i) //存储第i关的游戏最高分数
    {
        int maxCount; //用于存储最新得分和已存储的得分较大的一个
        SharedPreferences sp = getSharedPreferences(CONFIG_NAME,CONFIG_MODE);
        SharedPreferences.Editor editor = sp.edit();
        switch (i)
        {
            case 1: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE1,String.valueOf(maxCount));
                editor.apply();
                break;
            case 2: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE2,String.valueOf(maxCount));
                editor.apply();
                break;
            case 3: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE3,String.valueOf(maxCount));
                editor.apply();
                break;
            case 4: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE4,String.valueOf(maxCount));
                editor.apply();
                break;
            case 5: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE5,String.valueOf(maxCount));
                editor.apply();
                break;
            case 6: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE6,String.valueOf(maxCount));
                editor.apply();
                break;
            case 7: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE7,String.valueOf(maxCount));
                editor.apply();
                break;
            case 8: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE8,String.valueOf(maxCount));
                editor.apply();
                break;
            case 9: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE9,String.valueOf(maxCount));
                editor.apply();
                break;
            case 10: maxCount = Math.max(grade,Integer.parseInt(recordCount));
                editor.putString(SCORE10,String.valueOf(maxCount));
                editor.apply();
                break;

        }
    }
    protected void loadConfig(int i) //用于显示第i关的最高分数
    {
        SharedPreferences sp = getSharedPreferences(CONFIG_NAME,CONFIG_MODE);
        switch (i)
        {
            case 1: recordCount = sp.getString(SCORE1,"0");
                break;
            case 2: recordCount = sp.getString(SCORE2,"0");
                break;
            case 3: recordCount = sp.getString(SCORE3,"0");
                break;
            case 4: recordCount = sp.getString(SCORE4,"0");
                break;
            case 5: recordCount = sp.getString(SCORE5,"0");
                break;
            case 6: recordCount = sp.getString(SCORE6,"0");
                break;
            case 7: recordCount = sp.getString(SCORE7,"0");
                break;
            case 8: recordCount = sp.getString(SCORE8,"0");
                break;
            case 9: recordCount = sp.getString(SCORE9,"0");
                break;
            case 10: recordCount = sp.getString(SCORE10,"0");
                break;
        }
    }
}