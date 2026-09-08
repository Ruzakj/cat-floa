package com.ric.pet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

/** Original animated pixel cats inspired by the user's cute pixel-cat reference style. */
public class PetView extends View {
    public enum State { IDLE,WALK,RUN,SIT,SLEEP,HAPPY,GROOM,STRETCH,SCRATCH,LOOK_AROUND,PLAY,POUNCE,EAT,DRINK,ZOOMIES,CLIMB,HANG,FALL,ATTENTION,YAWN,KNEAD,WAKE,TYPING,SCROLL }
    private final Paint p=new Paint();
    private State state=State.IDLE;
    private long since=SystemClock.uptimeMillis();
    private boolean flip;
    private String cat;

    public PetView(Context c){super(c);p.setAntiAlias(false);cat=PetPreferences.cat(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    public State getState(){return state;}
    public void setState(State s){state=s;since=SystemClock.uptimeMillis();invalidate();}
    public void setFlip(boolean f){flip=f;invalidate();}
    public void setFacing(int d){setFlip(d<0);}
    public void refreshCharacter(){cat=PetPreferences.cat(getContext());invalidate();}

    private long age(){return SystemClock.uptimeMillis()-since;}
    private int frame(long ms){return (int)(age()/ms);}
    private float u(){return Math.max(1f,Math.min(getWidth(),getHeight())/16f);}

    @Override protected void onDraw(Canvas c){
        super.onDraw(c); float u=u(); c.save(); if(flip)c.scale(-1,1,getWidth()/2f,getHeight()/2f);
        switch(state){
            case SLEEP: drawSleep(c,u); break;
            case SIT: drawSit(c,u); break;
            case GROOM: drawGroom(c,u); break;
            case STRETCH: drawStretch(c,u); break;
            case SCRATCH: drawScratch(c,u); break;
            case PLAY: drawPlay(c,u); break;
            case POUNCE: drawPounce(c,u); break;
            case EAT: drawEatDrink(c,u,false); break;
            case DRINK: drawEatDrink(c,u,true); break;
            case TYPING: drawTyping(c,u); break;
            case SCROLL: drawScrolling(c,u); break;
            case YAWN: drawYawn(c,u); break;
            case KNEAD: drawKnead(c,u); break;
            default: drawStanding(c,u); break;
        }
        c.restore(); postInvalidateDelayed(state==State.RUN||state==State.ZOOMIES?70:110);
    }

    private int body(){
        if("MIKAN".equals(cat))return Color.rgb(238,164,91);
        if("KURO".equals(cat))return Color.rgb(58,58,66);
        if("AOI".equals(cat))return Color.rgb(148,153,166);
        return Color.rgb(244,241,231);
    }
    private int patch(){
        if("MIKAN".equals(cat))return Color.rgb(199,119,61);
        if("KURO".equals(cat))return Color.rgb(245,242,232);
        if("AOI".equals(cat))return Color.rgb(104,110,124);
        return Color.rgb(225,139,72);
    }
    private int patch2(){return "MOCHI".equals(cat)?Color.rgb(72,70,78):patch();}
    private int outline(){return Color.rgb(62,52,55);}
    private int white(){return Color.rgb(250,247,239);}
    private int pink(){return Color.rgb(239,145,151);}

    private void rect(Canvas c,float l,float t,float r,float b,float u,int color){p.setColor(color);c.drawRect(l*u,t*u,r*u,b*u,p);}
    private void pixel(Canvas c,float x,float y,float u,int color){rect(c,x,y,x+1,y+1,u,color);}

    private void drawStanding(Canvas c,float u){
        int f=frame(state==State.RUN||state==State.ZOOMIES?90:180); float bob=(f%2==0?0:.25f); c.save(); c.translate(0,bob*u);
        int o=outline(),b=body();
        rect(c,4,4,12,12,u,o); rect(c,3,3,6,6,u,o); rect(c,10,3,13,6,u,o); rect(c,5,4,11,12,u,b); rect(c,4,4,6,6,u,b); rect(c,10,4,12,6,u,b);
        ears(c,u); markings(c,u); face(c,u,state==State.HAPPY); belly(c,u);
        int lift=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?f%2:0; rect(c,4,12+lift*.5f,7,14,u,o); rect(c,9,12+(1-lift)*.5f,12,14,u,o); rect(c,5,12+lift*.5f,7,13,u,white()); rect(c,9,12+(1-lift)*.5f,11,13,u,white());
        drawTail(c,u,f); if(state==State.HAPPY||state==State.ATTENTION){pixel(c,2,2,u,pink());pixel(c,13,1,u,pink());}
        c.restore();
    }
    private void ears(Canvas c,float u){int o=outline();rect(c,3,3,6,6,u,o);rect(c,10,3,13,6,u,o);rect(c,4,4,5,5,u,pink());rect(c,11,4,12,5,u,pink());}
    private void markings(Canvas c,float u){int a=patch(),d=patch2(); if("MOCHI".equals(cat)){rect(c,5,4,8,6,u,d);rect(c,9,8,12,11,u,a);rect(c,10,4,12,6,u,a);} else if("MIKAN".equals(cat)){rect(c,6,4,7,6,u,a);rect(c,8,4,9,6,u,a);rect(c,10,4,11,6,u,a);rect(c,11,9,12,11,u,a);} else if("KURO".equals(cat)){rect(c,6,5,10,10,u,white());rect(c,5,10,11,12,u,white());} else {rect(c,6,4,7,6,u,a);rect(c,9,4,10,6,u,a);rect(c,10,9,12,11,u,a);} }
    private void belly(Canvas c,float u){if(!"KURO".equals(cat))rect(c,6,10,10,12,u,white());}
    private void face(Canvas c,float u,boolean happy){int o=outline(); if(happy){rect(c,6,7,7,7.4f,u,o);rect(c,9,7,10,7.4f,u,o);} else {pixel(c,6,7,u,o);pixel(c,9,7,u,o);} pixel(c,7.5f,8.2f,u,pink());rect(c,7,9,8,9.4f,u,o);rect(c,8,9,9,9.4f,u,o);rect(c,4,8,6,8.3f,u,o);rect(c,10,8,12,8.3f,u,o);}
    private void drawTail(Canvas c,float u,int f){int o=outline(),b=body();float y=(f%2==0?8:7);rect(c,12,y,14,y+2,u,o);rect(c,13,y-2,15,y+1,u,o);rect(c,12.5f,y+.2f,13.7f,y+1.2f,u,b);rect(c,13.5f,y-1.4f,14.5f,y+.2f,u,b);}

    private void drawSit(Canvas c,float u){int f=frame(350);int o=outline(),b=body();float bob=f%2==0?0:.12f;c.save();c.translate(0,bob*u);rect(c,5,4,11,10,u,o);rect(c,4,3,6,6,u,o);rect(c,10,3,12,6,u,o);rect(c,6,4,10,10,u,b);ears(c,u);markings(c,u);face(c,u,false);rect(c,5,9,12,14,u,o);rect(c,6,9,11,13,u,b);rect(c,5,12,8,14,u,white());rect(c,9,12,12,14,u,white());drawTail(c,u,f);c.restore();}
    private void drawSleep(Canvas c,float u){int f=frame(600);float y=f%2==0?0:.2f;int o=outline(),b=body();c.save();c.translate(0,y*u);rect(c,3,7,13,13,u,o);rect(c,4,7,12,12,u,b);rect(c,4,6,8,10,u,o);rect(c,5,6,8,9,u,b);rect(c,5,8,6.5f,8.4f,u,o);rect(c,7,8,8.5f,8.4f,u,o);rect(c,9,10,12,12,u,white());c.restore();p.setColor(outline());p.setTextSize(2*u);c.drawText(f%3==0?"z":"Z",12*u,5*u,p);}
    private void drawGroom(Canvas c,float u){drawSit(c,u);int f=frame(180);rect(c,10.5f,f%2==0?6:7.5f,12.5f,f%2==0?8:9.5f,u,white());pixel(c,10,8.3f,u,pink());}
    private void drawStretch(Canvas c,float u){int f=frame(220);int o=outline(),b=body();float d=f%2==0?0:.5f;rect(c,3,7,12,12,u,o);rect(c,4,7,11,11,u,b);rect(c,2,6,6,10,u,o);rect(c,3,6,6,9,u,b);face(c,u,true);rect(c,1,11,6,13+d,u,o);rect(c,5,11,10,13+d,u,o);rect(c,2,11,6,12+d,u,white());rect(c,6,11,10,12+d,u,white());drawTail(c,u,f);}
    private void drawScratch(Canvas c,float u){drawSit(c,u);int f=frame(130);rect(c,11,f%2==0?5:7,13,f%2==0?7:9,u,outline());rect(c,11.3f,f%2==0?5.2f:7.2f,12.7f,f%2==0?6.6f:8.6f,u,white());}
    private void drawPlay(Canvas c,float u){drawStanding(c,u);int f=frame(150);float bx=f%2==0?13:11.5f,by=f%2==0?11:9.5f;p.setColor(Color.rgb(219,92,105));c.drawCircle(bx*u,by*u,u,p);p.setStrokeWidth(Math.max(1,u*.25f));c.drawLine(11*u,8*u,bx*u,by*u,p);}
    private void drawPounce(Canvas c,float u){int f=frame(100);c.save();c.translate(0,(f%4==1||f%4==2)?-1.2f*u:0);drawStretch(c,u);c.restore();}
    private void drawEatDrink(Canvas c,float u,boolean water){drawSit(c,u);int f=frame(200);float y=f%2==0?0:.4f;c.save();c.translate(0,y*u);rect(c,4,12,12,14,u,water?Color.rgb(92,165,221):Color.rgb(185,122,72));rect(c,5,11.5f,11,12.5f,u,white());c.restore();}
    private void drawTyping(Canvas c,float u){drawSit(c,u);int f=frame(110);rect(c,3,11.5f,13,14,u,Color.rgb(61,63,72));for(int x=4;x<12;x+=2)pixel(c,x,12.2f+(f+x)%2*.25f,u,Color.rgb(238,238,238));rect(c,5,10.3f+(f%2)*.35f,7,11.8f,u,white());rect(c,9,10.3f+((f+1)%2)*.35f,11,11.8f,u,white());}
    private void drawScrolling(Canvas c,float u){int f=frame(120);c.save();c.translate(0,(float)Math.sin(age()/110.0)*.35f*u);drawStanding(c,u);c.restore();p.setColor(Color.rgb(90,95,110));p.setStrokeWidth(Math.max(1,u*.25f));float x=13.5f*u,y=(5+(f%3))*u;c.drawLine(x,4*u,x,11*u,p);c.drawLine(x,y,x-.7f*u,y+.8f*u,p);c.drawLine(x,y,x+.7f*u,y+.8f*u,p);}
    private void drawYawn(Canvas c,float u){drawSit(c,u);p.setColor(Color.rgb(90,45,50));float h=frame(260)%2==0?.7f:1.5f;c.drawOval(7.3f*u,8.5f*u,8.7f*u,(8.5f+h)*u,p);}
    private void drawKnead(Canvas c,float u){drawSit(c,u);int f=frame(130);rect(c,5,11+(f%2)*.5f,7,14,u,white());rect(c,9,11+((f+1)%2)*.5f,11,14,u,white());}
}
