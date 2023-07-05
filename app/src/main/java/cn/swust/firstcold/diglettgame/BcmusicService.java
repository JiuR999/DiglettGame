package cn.swust.firstcold.diglettgame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author 徐玉婷
 */
public class BcmusicService extends Service {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public IBinder iBinder = new MusicControlBinder();
    //记录音乐播放状态
    static boolean isPlayer = true;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }
    public BcmusicService(){

    }
    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this,R.raw.ylgy);
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
       if(mediaPlayer==null) return;
       if(isPlayer){
           mediaPlayer.stop();
       }
       mediaPlayer.release();
       isPlayer = false;
       mediaPlayer = null;
    }
    public void play(){
        isPlayer = true;
        mediaPlayer.start();
    }
    public void pauseMusic(){
        mediaPlayer.pause();
    }
    public void continueMusic(){
        mediaPlayer.start();
    }
    public class MusicControlBinder extends Binder {
                BcmusicService getService(){
                    return BcmusicService.this;
                }
    }

}