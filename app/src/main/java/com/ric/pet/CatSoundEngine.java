package com.ric.pet;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public final class CatSoundEngine {
    private final Context app;
    private MediaPlayer active;
    public CatSoundEngine(Context c){ app=c.getApplicationContext(); }

    /** Tap SFX: custom user-picked audio wins; bundled character sound is fallback. */
    public void playFor(String cat, boolean doubleTap){
        String custom=PetPreferences.customTapSound(app,cat);
        if(custom!=null&&!custom.isEmpty()&&play(Uri.parse(custom)))return;
        int res;
        if("YUKI".equals(cat)) res=doubleTap?R.raw.nyan_nyan:R.raw.nyaa_cute;
        else if("REN".equals(cat)) res=doubleTap?R.raw.nyaa_cute:R.raw.nyan_nyan;
        else if("TORA".equals(cat)) res=R.raw.meoww_man;
        else res=R.raw.real_catt_meong;
        play(res);
    }

    /** Random/autonomous meow stays on bundled SFX so custom sound is tap-only. */
    public void playRandom(String cat){
        if("YUKI".equals(cat)) play(R.raw.nyaa_cute);
        else if("REN".equals(cat)) play(R.raw.nyan_nyan);
        else if("TORA".equals(cat)) play(R.raw.meoww_man);
        else play(R.raw.real_catt_meong);
    }

    private void releaseActive(){
        MediaPlayer m=active; active=null;
        if(m!=null)try{m.stop();}catch(Exception ignored){}finally{try{m.release();}catch(Exception ignored){}}
    }
    private void track(MediaPlayer m){
        releaseActive(); active=m;
        m.setVolume(1f,1f);
        m.setOnCompletionListener(x->{if(active==x)active=null;try{x.release();}catch(Exception ignored){}});
        m.setOnErrorListener((x,w,e)->{if(active==x)active=null;try{x.release();}catch(Exception ignored){}return true;});
    }
    private boolean play(Uri uri){
        MediaPlayer m=null;
        try{
            m=MediaPlayer.create(app,uri);
            if(m==null)return false;
            track(m); m.start(); return true;
        }catch(Exception ignored){if(m!=null){if(active==m)active=null;try{m.release();}catch(Exception releaseIgnored){}}return false;}
    }
    private void play(int res){
        MediaPlayer m=null;
        try{
            m=MediaPlayer.create(app,res);
            if(m==null)return;
            track(m); m.start();
        }catch(Exception ignored){if(m!=null){if(active==m)active=null;try{m.release();}catch(Exception releaseIgnored){}}}
    }
}
