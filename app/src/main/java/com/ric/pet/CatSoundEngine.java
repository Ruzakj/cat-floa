package com.ric.pet;

import android.content.Context;import android.media.MediaPlayer;import java.util.Random;

public final class CatSoundEngine{
 private final Context app;private final Random rng=new Random();
 public CatSoundEngine(Context c){app=c.getApplicationContext();}
 public void playFor(String cat,boolean doubleTap){int res;if("YUKI".equals(cat))res=doubleTap?R.raw.nyan_nyan:R.raw.nyaa_cute;else if("REN".equals(cat))res=doubleTap?R.raw.nyan_nyan:R.raw.meoww_man;else if("TORA".equals(cat))res=R.raw.meoww_man;else res=R.raw.real_catt_meong;play(res);}
 public void playRandom(String cat){if("YUKI".equals(cat))play(rng.nextBoolean()?R.raw.nyaa_cute:R.raw.nyan_nyan);else if("REN".equals(cat))play(rng.nextBoolean()?R.raw.meoww_man:R.raw.nyan_nyan);else if("TORA".equals(cat))play(R.raw.meoww_man);else play(R.raw.real_catt_meong);}
 private void play(int res){try{MediaPlayer m=MediaPlayer.create(app,res);if(m==null)return;m.setVolume(1f,1f);m.setOnCompletionListener(x->{try{x.release();}catch(Exception ignored){}});m.start();}catch(Exception ignored){}}
}
