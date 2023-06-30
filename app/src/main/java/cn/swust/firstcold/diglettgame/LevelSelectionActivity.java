package cn.swust.firstcold.diglettgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        account = getIntent().getStringExtra(MainActivity.ACCOUNT);
        recyclerView = findViewById(R.id.recycler_view);
        //初始化关卡数据
        intiData();
        //初始化关卡界面
        initRecycleView();
        startPlayBacMusic();
    }

    /**
     * 播放音乐
     */
    private void startPlayBacMusic() {
        Intent intent = new Intent(LevelSelectionActivity.this,BcmusicService.class);
        //startService(intent);
    }

    private void initRecycleView() {
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
                    intent.putExtra(LevelSelectionActivity.LEVEL,String.valueOf(position));
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        imgReturn = findViewById(R.id.imgReturn);
        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });
    }

    protected void intiData(){
        levels = new ArrayList<>();
        for (int i = 0; i < 11; i++){
            String levelString = String.valueOf(i+1);
            levels.add(new Level(levelString,true,false));
        }
        //设置第一个关卡解锁状态
        levels.get(0).setLocked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(LevelSelectionActivity.this,BcmusicService.class);
        stopService(intent);
    }
}