package cn.swust.firstcold.diglettgame;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.IBinder;

import java.util.HashMap;

public class BcsoundService extends Service {
    public BcsoundService() {
    }
    public static SoundPool soundPool;
    HashMap<Integer,Integer> soundMap = new HashMap<>();


    @Override
    public void onCreate() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2).build();
        soundMap.put(1,soundPool.load(this,R.raw.chuizi,1));
        soundMap.put(2,soundPool.load(this,R.raw.dishu,1));
        soundMap.put(3,soundPool.load(this,R.raw.victory,1));
        soundMap.put(4,soundPool.load(this,R.raw.fals,1));
        soundMap.put(5,soundPool.load(this,R.raw.click,1));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.hasExtra("打地鼠界面")){
            int action = intent.getIntExtra("打地鼠界面",-1);
            soundPool.play(soundMap.get(action),2,2,1,0,1);
        }
        if(intent != null && intent.hasExtra("游戏结果")){
            int action = intent.getIntExtra("游戏结果",-1);
            soundPool.play(soundMap.get(action),2,2,1,0,1);
        }
        if(intent != null && intent.hasExtra("点击")){
            int action = intent.getIntExtra("点击",-1);
            soundPool.play(soundMap.get(action),2,2,1,0,1);
        }
            return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        soundPool.release();
        soundPool = null;
        super.onDestroy();
    }
}