package cn.swust.firstcold.diglettgame;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.swust.firstcold.adapter.LevelAdapter;
import cn.swust.firstcold.source.Level;

/**
 * @author 徐玉婷
 */
public class LevelSelectionActivity extends AppCompatActivity {
    public static final String LEVEL = "level";
    private RecyclerView recyclerView;
    private LevelAdapter adapter;
    private List<Level> levels;
    private ImageView imgReturn;
    private String account;
    private ImageView imgMusic;
    private boolean isPlay = true;
    private final int RESULTCODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        recyclerView = findViewById(R.id.recycler_view);
        //初始化关卡数据
        intiData();
        //初始化关卡界面
        initRecycleView();
        if(SplashActivity.isPlay){
            startPlayBacMusic();
        }
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
        Intent intent = new Intent(LevelSelectionActivity.this,BcmusicService.class);
        startService(intent);
    }
    /**
     * 停止播放音乐
     */
    private void stopPlayBacMusic() {
        Intent intent = new Intent(LevelSelectionActivity.this,BcmusicService.class);
        stopService(intent);
    }

    private void initRecycleView() {
        imgMusic = findViewById(R.id.imgMusic);
        if (!SplashActivity.isPlay){
            imgMusic.setImageResource(R.mipmap.music_off);
        }
        setObjAnimation(imgMusic,"rotation",0f,365f,-1);
        // 设置布局管理器为网格布局管理器，每行显示五个关卡
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LevelAdapter(levels);
        adapter.setOnItemClickListener(new LevelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (levels.get(position).isLocked()){
                    Toast.makeText(LevelSelectionActivity.this, "暂未解锁", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(LevelSelectionActivity.this, MouseActivity.class);
                    intent.putExtra(MainActivity.ACCOUNT,account);
                    intent.putExtra(LevelSelectionActivity.LEVEL,position+1);
                    startActivityForResult(intent,RESULTCODE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        imgMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SplashActivity.isPlay){
                    imgMusic.setImageResource(R.mipmap.music_off);
                    SplashActivity.isPlay = false;
                    SplashActivity.bcmusicService.pauseMusic();
                    //stopPlayBacMusic();
                }else{
                    imgMusic.setImageResource(R.mipmap.music_on);
                    SplashActivity.isPlay = true;
                    SplashActivity.bcmusicService.play();
                   // startPlayBacMusic();
                }
            }
        });
        imgReturn = findViewById(R.id.imgReturn);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSound = new Intent(LevelSelectionActivity.this,BcsoundService.class);
                intentSound.putExtra("打地鼠界面",5);
                startService(intentSound);
                   finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULTCODE){
            if(resultCode == RESULT_OK){
                account = data.getStringExtra(MainActivity.ACCOUNT);
                //初始化关卡数据
                intiData();
                //初始化关卡界面
                initRecycleView();
            }
        }
    }

    protected void intiData(){
        levels = new ArrayList<>();
        account = getIntent().getStringExtra(MainActivity.ACCOUNT);
        //获取用户关卡数
        SharedPreferences sp = getSharedPreferences(account,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String strlevels = sp.getString("user_level","1");
        int levelnum = Integer.parseInt(strlevels);
        for (int i = 1; i <= 10; i++){
            if(levelnum==1){
                if(i==1)levels.add(new Level(String.valueOf(i),false,sp.getBoolean(String.valueOf(i),false)));
                else    levels.add(new Level(String.valueOf(i),!(sp.getBoolean(String.valueOf(i-1),false)),false));
            }else{
                if(i < levelnum){
                    editor.putBoolean(String.valueOf(i),true);
                    editor.apply();
                    levels.add(new Level(String.valueOf(i),false,sp.getBoolean(String.valueOf(i),false)));
                }else{
                    levels.add(new Level(String.valueOf(i),!(sp.getBoolean(String.valueOf(i-1),false)),sp.getBoolean(String.valueOf(i),false)));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(LevelSelectionActivity.this,BcmusicService.class);
        stopService(intent);
    }
}