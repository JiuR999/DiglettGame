package cn.swust.firstcold.diglettgame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * @author 徐玉婷
 */
public class BcmusicService extends Service {
    public BcmusicService() {
    }
    public static MediaPlayer mediaPlayer;
    //记录音乐播放状态
    static boolean isPlayer;



    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this,R.raw.ylgy);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.2f,0.2f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            isPlayer = mediaPlayer.isPlaying();
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
        mediaPlayer.stop();
        isPlayer = mediaPlayer.isPlaying();
        mediaPlayer.release();
        super.onDestroy();
    }
    //关闭音量
    public static void shutMusic(){
        mediaPlayer.setVolume(0,0);
    }
}