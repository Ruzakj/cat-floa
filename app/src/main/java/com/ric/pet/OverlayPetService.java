package com.ric.pet;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.Random;

public class OverlayPetService extends Service {
    private static final int ID=77;
    private WindowManager wm; private FrameLayout root; private PetView pet; private TextView bubble;
    private WindowManager.LayoutParams lp;
    private final Handler h = new Handler(Looper.getMainLooper());
    private final Random rng = new Random();
    private final PetNeeds needs = new PetNeeds();
    private int screenW, screenH;
    private float downX,downY; private int startX,startY; private boolean dragged;
    private boolean userHolding=false;
    private Runnable behaviorTask;
    private long lastTapAt=0;

    @Override public void onCreate(){ super.onCreate(); createChannel(); startForeground(ID, notification()); if(Settings.canDrawOverlays(this)) createOverlay(); }
    @Override public int onStartCommand(Intent i,int flags,int id){ return START_STICKY; }
    @Override public IBinder onBind(Intent i){ return null; }

    private void createOverlay(){
        wm=(WindowManager)getSystemService(WINDOW_SERVICE); pointSize();
        root=new FrameLayout(this); pet=new PetView(this); bubble=new TextView(this);
        bubble.setTextSize(13); bubble.setTextColor(Color.BLACK); bubble.setBackgroundColor(0xEFFFFFFF);
        bubble.setPadding(dp(10),dp(6),dp(10),dp(6)); bubble.setVisibility(View.GONE);
        FrameLayout.LayoutParams petLp=new FrameLayout.LayoutParams(dp(104),dp(104)); petLp.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        FrameLayout.LayoutParams bubLp=new FrameLayout.LayoutParams(-2,-2); bubLp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        root.addView(bubble,bubLp); root.addView(pet,petLp);

        int type = Build.VERSION.SDK_INT>=26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        lp=new WindowManager.LayoutParams(dp(150),dp(150),type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        lp.gravity=Gravity.TOP|Gravity.START; lp.x=screenW/2-dp(75); lp.y=Math.max(0,screenH-dp(260));
        wm.addView(root,lp); root.setOnTouchListener((v,e)->touch(e)); scheduleNext(1200);
    }

    private boolean touch(MotionEvent e){
        switch(e.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                userHolding=true; cancelBehavior(); downX=e.getRawX(); downY=e.getRawY(); startX=lp.x; startY=lp.y; dragged=false;
                pet.setState(PetView.State.HAPPY); return true;
            case MotionEvent.ACTION_MOVE:
                float dx=e.getRawX()-downX, dy=e.getRawY()-downY;
                if(Math.abs(dx)>8||Math.abs(dy)>8) dragged=true;
                lp.x=clamp(startX+(int)dx,0,Math.max(0,screenW-lp.width)); lp.y=clamp(startY+(int)dy,0,Math.max(0,screenH-lp.height));
                wm.updateViewLayout(root,lp); return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                userHolding=false;
                if(!dragged){
                    needs.pet(); long now=SystemClock.uptimeMillis(); boolean doubleTap=now-lastTapAt<380; lastTapAt=now;
                    if(doubleTap){ say("ayo main! 😸"); playBurst(); }
                    else { pet.setState(PetView.State.HAPPY); say(tapMessages()[rng.nextInt(tapMessages().length)]); scheduleNext(1500); }
                } else {
                    pet.setState(PetView.State.IDLE); if(rng.nextInt(100)<35) say("taruh aku di sini? 😺"); scheduleNext(1400);
                }
                return true;
        }
        return false;
    }

    private void scheduleNext(long delay){ cancelBehavior(); behaviorTask=()->{ if(root==null||userHolding) return; needs.tick(); performSmartActivity(); }; h.postDelayed(behaviorTask,delay); }
    private void cancelBehavior(){ if(behaviorTask!=null) h.removeCallbacks(behaviorTask); }

    private void performSmartActivity(){
        if(needs.tired() && rng.nextInt(100)<72){ seekCornerAndSleep(); return; }
        if(needs.hungry() && rng.nextInt(100)<68){ activity(PetView.State.EAT,4300,"lapar... nyam nyam"); needs.eat(); return; }
        if(needs.lonely() && rng.nextInt(100)<55){ attention(); return; }
        if(needs.playful() && rng.nextInt(100)<20){ zoomies(); return; }

        int roll=rng.nextInt(100);
        if(roll<17) walk(false);
        else if(roll<22) walk(true);
        else if(roll<31) activity(PetView.State.SIT,3000+rng.nextInt(2500),null);
        else if(roll<39) activity(PetView.State.GROOM,3800+rng.nextInt(1800),rng.nextInt(100)<25?"bersihin bulu dulu":null);
        else if(roll<45) activity(PetView.State.STRETCH,2200,"mreeeeng~");
        else if(roll<50) activity(PetView.State.SCRATCH,2600,null);
        else if(roll<57) activity(PetView.State.LOOK_AROUND,3000,rng.nextBoolean()?"apa tuh?":null);
        else if(roll<64) { activity(PetView.State.PLAY,4200,"main bentar 😸"); needs.play(); }
        else if(roll<69) pounce();
        else if(roll<73) { activity(PetView.State.DRINK,3200,"minum dulu"); needs.drink(); }
        else if(roll<78) activity(PetView.State.YAWN,2300,"huaaam...");
        else if(roll<83) activity(PetView.State.KNEAD,3600,rng.nextBoolean()?"bikin roti dulu~":null);
        else if(roll<88) edgeAdventure();
        else if(roll<93) seekCornerAndSleep();
        else if(roll<97) attention();
        else activity(PetView.State.HAPPY,2200,randomMessage());
    }

    private void activity(PetView.State state,long duration,String speech){ pet.setState(state); if(speech!=null) say(speech); behaviorTask=()->{ if(root==null||userHolding)return; pet.setState(PetView.State.IDLE); scheduleNext(1300+rng.nextInt(3000)); }; h.postDelayed(behaviorTask,duration); }

    private void seekCornerAndSleep(){
        int target = rng.nextBoolean()?0:Math.max(0,screenW-lp.width); int dir=target>=lp.x?1:-1; pet.setFlip(dir<0); pet.setState(PetView.State.WALK);
        final Runnable[] rr=new Runnable[1]; rr[0]=new Runnable(){ public void run(){
            if(root==null||userHolding)return;
            int delta=target-lp.x;
            if(Math.abs(delta)<=dp(6)){ lp.x=target; wm.updateViewLayout(root,lp); sleepLong(); return; }
            lp.x=clamp(lp.x+(delta>0?dp(6):-dp(6)),0,Math.max(0,screenW-lp.width)); wm.updateViewLayout(root,lp); h.postDelayed(rr[0],85);
        }}; behaviorTask=rr[0]; h.post(rr[0]);
    }

    private void sleepLong(){ pet.setState(PetView.State.SLEEP); if(rng.nextBoolean()) say("zzZ..."); long d=8500+rng.nextInt(9500); behaviorTask=()->{ if(root==null||userHolding)return; needs.sleep(); pet.setState(PetView.State.WAKE); say("bangun~"); scheduleNext(2600); }; h.postDelayed(behaviorTask,d); }

    private void walk(boolean run){
        needs.move(run); pet.setState(run?PetView.State.RUN:PetView.State.WALK); final int dir=rng.nextBoolean()?1:-1; pet.setFlip(dir<0);
        final int steps=(run?24:16)+rng.nextInt(run?22:20); final int px=dp(run?8:5), tick=run?55:95;
        final Runnable[] rr=new Runnable[1]; rr[0]=new Runnable(){ int s=0; public void run(){
            if(root==null||userHolding)return;
            if(s++>=steps){ pet.setState(PetView.State.IDLE); scheduleNext(1300+rng.nextInt(2600)); return; }
            int old=lp.x; lp.x=clamp(lp.x+dir*px,0,Math.max(0,screenW-lp.width));
            if(lp.x==old){ if(rng.nextInt(100)<35) { edgeAdventure(); } else { pet.setFlip(dir>0); pet.setState(PetView.State.LOOK_AROUND); scheduleNext(1500); } return; }
            wm.updateViewLayout(root,lp); h.postDelayed(rr[0],tick);
        }}; behaviorTask=rr[0]; h.post(rr[0]);
    }

    private void zoomies(){
        needs.play(); pet.setState(PetView.State.ZOOMIES); say("ZOOMIES!!");
        final Runnable[] rr=new Runnable[1]; rr[0]=new Runnable(){ int n=0, dir=rng.nextBoolean()?1:-1; public void run(){
            if(root==null||userHolding)return;
            if(n++>65){ pet.setState(PetView.State.YAWN); scheduleNext(2200); return; }
            int next=lp.x+dir*dp(11); if(next<=0 || next>=screenW-lp.width){ dir*=-1; pet.setFlip(dir<0); }
            lp.x=clamp(lp.x+dir*dp(11),0,Math.max(0,screenW-lp.width)); wm.updateViewLayout(root,lp); h.postDelayed(rr[0],55);
        }}; behaviorTask=rr[0]; h.post(rr[0]);
    }

    private void playBurst(){ needs.play(); pet.setState(PetView.State.POUNCE); final int dir=rng.nextBoolean()?1:-1; pet.setFlip(dir<0); final Runnable[] rr=new Runnable[1]; rr[0]=new Runnable(){ int n=0; public void run(){ if(root==null||userHolding)return; if(n++>=14){ pet.setState(PetView.State.HAPPY); scheduleNext(1800); return; } lp.x=clamp(lp.x+dir*dp(6),0,Math.max(0,screenW-lp.width)); wm.updateViewLayout(root,lp); h.postDelayed(rr[0],75); }}; behaviorTask=rr[0]; h.post(rr[0]); }

    private void pounce(){ pet.setState(PetView.State.POUNCE); if(rng.nextInt(100)<35) say("pounce!"); final int dir=rng.nextBoolean()?1:-1; pet.setFlip(dir<0); final int[] n={0}; final Runnable[] rr=new Runnable[1]; rr[0]=()->{ if(root==null||userHolding)return; if(n[0]++>=10){ pet.setState(PetView.State.HAPPY); scheduleNext(1600); return; } lp.x=clamp(lp.x+dir*dp(7),0,Math.max(0,screenW-lp.width)); wm.updateViewLayout(root,lp); h.postDelayed(rr[0],90); }; behaviorTask=rr[0]; h.post(rr[0]); }

    private void edgeAdventure(){
        boolean left=lp.x<screenW/2; lp.x=left?0:Math.max(0,screenW-lp.width); pet.setFlip(!left); pet.setState(PetView.State.CLIMB); say(rng.nextBoolean()?"naik dulu~":"meow!");
        final Runnable[] rr=new Runnable[1]; rr[0]=new Runnable(){ int n=0; public void run(){
            if(root==null||userHolding)return;
            if(n++>=12){ hangThenFall(); return; }
            lp.y=clamp(lp.y-dp(7),0,Math.max(0,screenH-lp.height)); wm.updateViewLayout(root,lp); h.postDelayed(rr[0],95);
        }}; behaviorTask=rr[0]; h.post(rr[0]);
    }

    private void hangThenFall(){
        pet.setState(PetView.State.HANG); behaviorTask=()->{
            if(root==null||userHolding)return; pet.setState(PetView.State.FALL);
            final Runnable[] rr=new Runnable[1]; rr[0]=new Runnable(){ int n=0; public void run(){
                if(root==null||userHolding)return;
                int floor=Math.max(0,screenH-dp(260)); if(lp.y>=floor || n++>28){ lp.y=Math.min(lp.y,floor); wm.updateViewLayout(root,lp); pet.setState(PetView.State.STRETCH); say("aku gapapa 😼"); scheduleNext(2200); return; }
                lp.y=clamp(lp.y+dp(10+n/3),0,Math.max(0,screenH-lp.height)); wm.updateViewLayout(root,lp); h.postDelayed(rr[0],55);
            }}; behaviorTask=rr[0]; h.post(rr[0]);
        }; h.postDelayed(behaviorTask,1800+rng.nextInt(1800));
    }

    private void attention(){ pet.setState(PetView.State.ATTENTION); say(needs.lonely()?"elus aku dong 🥺":"meow? lihat aku~"); needs.ignoredAttention(); scheduleNext(3300); }

    private void say(String t){ bubble.setText(t); bubble.setVisibility(View.VISIBLE); h.removeCallbacks(hideBubble); h.postDelayed(hideBubble,2400); }
    private final Runnable hideBubble=()->{if(bubble!=null) bubble.setVisibility(View.GONE);};
    private String randomMessage(){ String[] m={"lagi apa?","jangan lupa minum","aku ikut di sini","semangat ya","meow~","istirahat sebentar?","aku patroli dulu","mau main?"}; return m[rng.nextInt(m.length)]; }
    private String[] tapMessages(){ return new String[]{"hehe 😸","meow~","elus lagi","aku di sini","purrr~","jangan dicuekin"}; }

    private void pointSize(){ android.graphics.Point p=new android.graphics.Point(); wm=(WindowManager)getSystemService(WINDOW_SERVICE); wm.getDefaultDisplay().getSize(p); screenW=p.x; screenH=p.y; }
    private int clamp(int v,int min,int max){return Math.max(min,Math.min(max,v));}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}

    private void createChannel(){ if(Build.VERSION.SDK_INT>=26){ NotificationChannel c=new NotificationChannel("ric_pet","RIC Pet",NotificationManager.IMPORTANCE_LOW); c.setDescription("Menjaga virtual pet tetap aktif"); getSystemService(NotificationManager.class).createNotificationChannel(c); } }
    private Notification notification(){ Intent open=new Intent(this,MainActivity.class); PendingIntent pi=PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT); return new Notification.Builder(this,Build.VERSION.SDK_INT>=26?"ric_pet":null).setContentTitle("RIC Pet v0.3 aktif").setContentText("Pet sedang menjalani rutinitasnya").setSmallIcon(com.ric.pet.R.drawable.ic_notification).setContentIntent(pi).setOngoing(true).build(); }
    @Override public void onDestroy(){ h.removeCallbacksAndMessages(null); if(root!=null&&wm!=null){try{wm.removeView(root);}catch(Exception ignored){}} super.onDestroy(); }
}
