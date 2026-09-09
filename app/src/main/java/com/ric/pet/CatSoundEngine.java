package com.ric.pet;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public final class CatSoundEngine {
    private final Context app;
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

    private boolean play(Uri uri){
        try{
            MediaPlayer m=MediaPlayer.create(app,uri);
            if(m==null)return false;
            m.setVolume(1f,1f);
            m.setOnCompletionListener(x->{try{x.release();}catch(Exception ignored){}});
            m.setOnErrorListener((x,w,e)->{try{x.release();}catch(Exception ignored){}return true;});
            m.start();return true;
        }catch(Exception ignored){return false;}
    }
    private void play(int res){
        try{
            MediaPlayer m=MediaPlayer.create(app,res);
            if(m==null)return;
            m.setVolume(1f,1f);
            m.setOnCompletionListener(x->{try{x.release();}catch(Exception ignored){}});
            m.start();
        }catch(Exception ignored){}
    }
}
