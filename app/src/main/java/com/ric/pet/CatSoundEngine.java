package com.ric.pet;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import java.util.Random;

public final class CatSoundEngine {
    private final Random random=new Random();
    public void playRandom(){ if(random.nextBoolean()) play(false); else play(true); }
    public void playNyaa(){ play(true); }
    public void playMeow(){ play(false); }
    private void play(boolean nyaa){ new Thread(()->{ try{ int sr=22050; double dur=nyaa?.82:.62; int n=(int)(sr*dur); short[] pcm=new short[n]; for(int i=0;i<n;i++){ double t=i/(double)sr; double env=Math.min(1,t/.035)*Math.pow(Math.max(0,1-t/dur),1.45); double f;if(nyaa)f=760+210*Math.sin(Math.PI*t/dur)+65*Math.sin(2*Math.PI*4*t);else f=500+260*Math.sin(Math.PI*t/dur)-100*t/dur; double y=.52*Math.sin(2*Math.PI*f*t)+.20*Math.sin(2*Math.PI*2.01*f*t)+.08*Math.sin(2*Math.PI*3.04*f*t); if(nyaa)y*=1+.11*Math.sin(2*Math.PI*9*t); else y*=1+.15*Math.sin(2*Math.PI*6*t); pcm[i]=(short)(Math.max(-1,Math.min(1,y*env))*17000); } AudioTrack track=new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).setAudioFormat(new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sr).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()).setBufferSizeInBytes(pcm.length*2).setTransferMode(AudioTrack.MODE_STATIC).build(); track.write(pcm,0,pcm.length);track.play();Thread.sleep((long)(dur*1000)+80);track.stop();track.release(); }catch(Exception ignored){} },"ric-cat-sound").start(); }
}
