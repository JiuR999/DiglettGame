package cn.swust.firstcold.diglettgame;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class MouseActivity extends AppCompatActivity implements View.OnClickListener {
    //锤子和地鼠控件
    private ImageView imageViewMouse,imageViewChui,imageViewTime,imageBtnMusic;
    //设置游戏音乐关闭、开始或者暂停游戏、排行榜、结束游戏、返回
    private ImageView imageBtnPlay,imageBtnList,imageBtnEnd,imageButtonBack;
    //显示得分、当前剩余时间、关卡数，目标分
    private TextView tv_count,tv_curtime,tv_level,tv_target;
    //标志连击
    private int lcount = 0;

    static String recordCount = "0"; // 用于存储得分

    private ObjectAnimator objectAnimator;


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
    private int[] isContinue = {0,0,0,0,0};
    //记录连击数量
    private int COMBO = 0;
    private Intent intentSound ;
    private final int MAX_CLICK = 5;
    //传递地鼠位置消息类型
    private final int MOUSE_POZITION = 1;
    //传递计时器消息类型
    private final int TIME = 2;
    private final int PROGRESS = 3;
    //简单模式时间限制
    private int TIME_LIMIT;
    //简单模式刷新时间
    private int TIME_RENEW = 900;
    //通关状态，默认为false
    private boolean levelPass;
    private boolean isAnimStart = false;
    //记录用户目前游戏得分
    private int grade = 0;
    private int TARGETGRADE = 10;
    private static final int ACTION_PLAY_CHUI = 1;
    private static final int ACTION_PLAY_SHU = 2;
    private static final int ACTION_PLAY_VICTORY = 3;
    private static final int ACTION_PLAY_FALSE = 4;
    private static final int ACTION_PLAY_CLICK = 5;

    //设置字体
    private AssetManager assetManager;
    //通关结果弹窗
    private Dialog dialog;
    //操作UI界面
    public static final String USER_LEVEL = "user_level";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MOUSE_POZITION){
                imageViewMouse.setVisibility(View.VISIBLE);
                imageViewMouse.setX(position[msg.arg1][0]);
                imageViewMouse.setY(position[msg.arg1][1]);
                if(SplashActivity.isPlay){
                    playSound(ACTION_PLAY_SHU);
                }
            }else if (msg.what==TIME){
                TIME_LIMIT = msg.arg1;
                progressBarTime.setProgress(msg.arg1);
                tv_curtime.setText(""+msg.arg1+"秒");
                //进度条结束
                if (msg.arg1==0){
                    isGameStart = false;
                    grade+=COMBO*2;
                    if(grade >= TARGETGRADE) levelPass = true;
                    else levelPass = false;
                    levelResult(levelPass);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        account = getIntent().getStringExtra(MainActivity.ACCOUNT);

        initLevel();
        initView();
        setFont();

    }

    /**
     * 设置字体
     */
    private void setFont() {
        tv_count.setTypeface(Typeface.createFromAsset(assetManager,"fonts/FZYTK.TTF"));
        tv_level.setTypeface(Typeface.createFromAsset(assetManager,"fonts/FZKTPOP.TTF"));
        tv_target.setTypeface(Typeface.createFromAsset(assetManager,"fonts/ALGER.TTF"));
        tv_curtime.setTypeface(Typeface.createFromAsset(assetManager,"fonts/ARLRDBD.TTF"));
    }

    private void initLevel() {

        level = getIntent().getIntExtra(LevelSelectionActivity.LEVEL,1);
        tv_level = findViewById(R.id.tv_level);
        tv_target = findViewById(R.id.tv_target);
        tv_level.setText("第"+level+"关");
        TARGETGRADE = 20+(level-1)*3;
        tv_target.setText(""+TARGETGRADE+"分");
        TIME_LIMIT = 60-level*3;
        TIME_RENEW = 900-(level-1)*50;

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
                    Thread.sleep(TIME_RENEW);
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
        assetManager = getAssets();
        tv_curtime = findViewById(R.id.tv_curtime);
        intentSound = new Intent(this,BcsoundService.class);
        progressBarTime = findViewById(R.id.pgb_time);
        //绑定图片按钮控件并设计监听事件
        progressBarTime.setMax(TIME_LIMIT);

        imageViewMouse = findViewById(R.id.iv_mouse);
        imageViewChui = findViewById(R.id.iv_chuizi);
        imageButtonBack = findViewById(R.id.ib_return);
        imageButtonBack.setOnClickListener(this);
        imageBtnMusic = findViewById(R.id.ib_music);
        if (!SplashActivity.isPlay){
            imageBtnMusic.setImageResource(R.mipmap.music_off);
        }
        imageBtnMusic.setOnClickListener(this);
        setObjAnimation(imageBtnMusic,"rotation",0f,365f,-1);

        imageBtnPlay = findViewById(R.id.ib_play);
        imageBtnPlay.setOnClickListener(this);
        //排行榜
        imageBtnList = findViewById(R.id.ib_list);
        imageBtnList.setOnClickListener(this);
        imageBtnEnd = findViewById(R.id.ib_end);
        imageBtnEnd.setOnClickListener(this);

        tv_count = findViewById(R.id.tv_count);
            
        imageViewTime = findViewById(R.id.img_time);
        timerAnim();
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
                    if(SplashActivity.isPlay){
                        playSound(ACTION_PLAY_CHUI);
                    }
                    tv_count.setText(" "+grade+"分");
                }
                //用户抬起手后，将锤子设置为不可见
                else if(event.getAction()==MotionEvent.ACTION_UP) {
                    imageViewChui.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    private void setObjAnimation(Object obj,String name,float one,float two,int repeat) {
        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(obj,name,one,two);
        objectAnimator.setDuration(2500);/*动画时间*/
        objectAnimator.setRepeatCount(repeat);
        objectAnimator.start();
    }

    private void timerAnim() {
        objectAnimator= ObjectAnimator.ofFloat(imageViewTime,"rotation",0f,360f);
        objectAnimator.setDuration(4000);/*动画时间*/
        objectAnimator.setRepeatCount(-1);/*重复次数*/
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
                playSound(ACTION_PLAY_CLICK);
                if(isGameStart){
                    imageViewTime.clearAnimation();
                    imageBtnPlay.setImageResource(R.mipmap.btn_start);
                    isGameStart = false;
                    objectAnimator.pause();
                    progressBarTime.setVisibility(View.VISIBLE);
                    stopService(intentSound);
                }else{
                    imageBtnPlay.setImageResource(R.mipmap.btn_pause);
                    isGameStart = true;
                    if(isAnimStart){
                        objectAnimator.resume();
                    }else{
                        objectAnimator.start();
                        isAnimStart = true;
                    }
                    progressBarTime.setVisibility(View.VISIBLE);
                    // 开启一个线程用于游戏倒计时线程对象  默认为简单模式
                    countTimeThread = new CountTimeThread(TIME_LIMIT);
                    //创建更新地鼠位置线程对象 默认模式为简单
                    reNewDiglett = new ReNewDiglettThread(TIME_RENEW);
                    reNewDiglett.start();
                    countTimeThread.start();
                }
                break;
            case R.id.ib_music:
                playSound(ACTION_PLAY_CLICK);
                if(SplashActivity.isPlay){
                    imageBtnMusic.setImageResource(R.mipmap.music_off);
                    SplashActivity.isPlay = false;
                    stopPlayBacMusic();
                }else{
                    imageBtnMusic.setImageResource(R.mipmap.music_on);
                    SplashActivity.isPlay = true;
                    startPlayBacMusic();
                }
                break;
            case R.id.ib_end:
                playSound(ACTION_PLAY_CLICK);
                isGameStart = false;
                imageBtnPlay.setImageResource(R.mipmap.btn_start);
                objectAnimator.end();
                isAnimStart = false;
                setEndGame();
                break;
            case R.id.ib_list:
                playSound(ACTION_PLAY_CLICK);
                loadConfig(level);//先读取一次排行榜中已经存储的内容
                saveConfig(level);  //若没进行游戏，则显示历史保存的记录。若进行了游戏，则保存最新游戏的记录
                Intent intent = new Intent(MouseActivity.this, rankingListActivity.class);
                intent.putExtra(MainActivity.ACCOUNT,account);
                startActivity(intent);
                break;
            case R.id.ib_return:
                playSound(ACTION_PLAY_CLICK);
                saveConfig(level);
                back();
                finish();
                break;
        }
    }

    private void back() {
        Intent data = new Intent();
        data.putExtra(MainActivity.ACCOUNT,account);
        setResult(RESULT_OK,data);
    }

    private void gameEnd() {
        loadConfig(level);//先读取一次排行榜中已经存储的内容，即使不进行游戏也可以点击排行榜观看已存储的数据
        saveConfig(level);  //若没进行游戏，则显示历史保存的记录。若进行了游戏，则保存最新游戏的记录
        TIME_LIMIT = 60-level*4;
        tv_count.setText("");
        tv_curtime.setText(""+TIME_LIMIT+"秒");
        grade = 0;
        COMBO = 0;
        progressBarTime.setVisibility(View.INVISIBLE);
        imageViewMouse.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    protected void saveConfig(int i) //存储第i关的游戏最高分数
    {
        int maxCount; //用于存储最新得分和已存储的得分较大的一个
        SharedPreferences sp = getSharedPreferences(account,CONFIG_MODE);
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
    protected void loadConfig(int i) //用于获取第i关的最高分数
    {
        SharedPreferences sp = getSharedPreferences(account,CONFIG_MODE);
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
    /**
     * 设置点击结束游戏的弹窗事件
     */
     private void setEndGame(){
         Dialog dialog = new Dialog(MouseActivity.this);
         View view = View.inflate(MouseActivity.this,R.layout.dialog_endgame,null);
         TextView tv = view.findViewById(R.id.tv_dialog);
         tv.setText("确定要结束游戏吗？\n\n"+"您当前的分数是: "+(grade+COMBO*2)+"分");
         Button btn_yes = view.findViewById(R.id.dig_btn_yes);
             btn_yes.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     dialog.dismiss();

                     gameEnd();
                 }
             });
         Button btn_no = view.findViewById(R.id.dig_btn_no);
         btn_no.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dialog.dismiss();
             }
         });
         dialog.setContentView(view);
         dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
         dialog.show();
         Window window = dialog.getWindow();

         int width = getResources().getDisplayMetrics().widthPixels;

         int height = getResources().getDisplayMetrics().heightPixels;

         window.setLayout(width-200,height*1/4);
     }
    /**
     * 弹窗事件
     * */
    public void levelResult (boolean isPassed){
        dialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        saveConfig(level);
        if(isPassed) {
            playSound(ACTION_PLAY_VICTORY);
            level++;
            SharedPreferences sp = getSharedPreferences(account,MODE_PRIVATE);
            String strlevel = sp.getString(USER_LEVEL,"1");
            String resultlevel = String.valueOf(Integer.parseInt(strlevel));
            SharedPreferences.Editor editor = sp.edit();
            //存放用户关卡时对当前关卡与以及通关的关卡进行判断，如果小于说明是用户在重玩某一关，则不更新，否则进行更新
            editor.putString("user_level",Integer.valueOf(resultlevel)>level?
                    resultlevel:String.valueOf(level));
            editor.putBoolean(String.valueOf(level-1),true);
            editor.apply();
            View dialogView = inflater.inflate(R.layout.dialog_success, null);
            dialog.setContentView(dialogView);
            ImageView btnNext = dialogView.findViewById(R.id.imageNext);
            ImageView btnReHome = dialogView.findViewById(R.id.imageReHome);

            // 下一关
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(ACTION_PLAY_CLICK);
                    dialog.dismiss();
                    Intent intent = getIntent();
                    intent.putExtra(LevelSelectionActivity.LEVEL,level);
                    finish();
                    startActivity(intent);
                }
            });

            // 返回菜单
            btnReHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(ACTION_PLAY_CLICK);
                    back();
                    finish();
                    dialog.dismiss();
                }
            });
        } else {
            playSound(ACTION_PLAY_FALSE);
            View dialogView = inflater.inflate(R.layout.dialog_false, null);
            dialog.setContentView(dialogView);

            ImageView btnReHome2 = dialogView.findViewById(R.id.imageReHome2);
            ImageView btnRe = dialogView.findViewById(R.id.imageRe);

            // 重玩本关
            btnRe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(ACTION_PLAY_CLICK);
                    dialog.dismiss();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            // 返回菜单
            btnReHome2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(ACTION_PLAY_CLICK);
                    back();
                    finish();
                    dialog.dismiss();
                }
            });
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        window.setLayout(width-200,height*1/4);
    }

    private Intent backMenuIntent() {
        Intent intentBack  = new Intent(MouseActivity.this,LevelSelectionActivity.class);
        intentBack.putExtra(MainActivity.ACCOUNT,account);
        return intentBack;
    }
}