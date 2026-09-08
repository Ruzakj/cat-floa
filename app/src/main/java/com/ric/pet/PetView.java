package com.ric.pet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

/**
 * Procedural pixel pet renderer. Every state is animated frame-by-frame without external assets.
 * This keeps the APK light while allowing a real animation/state engine.
 */
public class PetView extends View {
    public enum State {
        IDLE, WALK, RUN, SIT, SLEEP, HAPPY, GROOM, STRETCH,
        SCRATCH, LOOK_AROUND, PLAY, POUNCE, EAT, DRINK,
        ZOOMIES, CLIMB, HANG, FALL, ATTENTION, YAWN, KNEAD, WAKE
    }

    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private State state = State.IDLE;
    private long stateSince = SystemClock.uptimeMillis();
    private boolean flip = false;

    public PetView(Context c){ super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null); }
    public State getState(){ return state; }
    public void setState(State s){ state=s; stateSince=SystemClock.uptimeMillis(); invalidate(); }
    public void setFlip(boolean f){ flip=f; invalidate(); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        float u = Math.max(1f, getWidth()/16f);
        c.save();
        if(flip) c.scale(-1,1,getWidth()/2f,getHeight()/2f);
        switch(state){
            case SLEEP: drawSleep(c,u); break;
            case SIT: drawSit(c,u); break;
            case GROOM: drawGroom(c,u); break;
            case STRETCH: drawStretch(c,u); break;
            case SCRATCH: drawScratch(c,u); break;
            case LOOK_AROUND: drawLookAround(c,u); break;
            case PLAY: drawPlay(c,u); break;
            case POUNCE: drawPounce(c,u); break;
            case EAT: drawEatDrink(c,u,false); break;
            case DRINK: drawEatDrink(c,u,true); break;
            case CLIMB: drawClimb(c,u); break;
            case HANG: drawHang(c,u); break;
            case FALL: drawFall(c,u); break;
            case ATTENTION: drawAttention(c,u); break;
            case YAWN: drawYawn(c,u); break;
            case KNEAD: drawKnead(c,u); break;
            case WAKE: drawWake(c,u); break;
            default: drawStanding(c,u); break;
        }
        c.restore();
        postInvalidateDelayed(state==State.ZOOMIES?70:120);
    }

    private int frame(long ms){ return (int)((SystemClock.uptimeMillis()-stateSince)/ms); }

    private void drawStanding(Canvas c,float u){
        int speed = state==State.RUN?95:(state==State.ZOOMIES?65:210);
        int f=frame(speed); float bob=((f&1)==0?0:.28f)*u;
        drawBody(c,u,bob);
        p.setColor(Color.rgb(40,40,45));
        if(state==State.WALK || state==State.RUN || state==State.ZOOMIES){
            float a=((f&1)==0?0.0f:.9f);
            rect(c,4,13+a,7,15,u,bob,p); rect(c,9,13+(a==0?.9f:0),12,15,u,bob,p);
            if(state==State.ZOOMIES){ p.setStrokeWidth(.35f*u); c.drawLine(1*u,8*u,3*u,8*u,p); c.drawLine(.5f*u,10*u,3*u,10*u,p); }
        } else { rect(c,4,13,7,15,u,bob,p); rect(c,9,13,12,15,u,bob,p); }
        if(state==State.HAPPY){ p.setColor(Color.rgb(240,100,120)); rect(c,2,2,3,3,u,bob,p); rect(c,13,1,14,2,u,bob,p); }
    }

    private void drawBody(Canvas c,float u,float y){
        p.setColor(Color.rgb(40,40,45));
        rect(c,4,4,12,13,u,y,p); rect(c,3,3,6,6,u,y,p); rect(c,10,3,13,6,u,y,p);
        p.setColor(Color.rgb(246,246,242)); rect(c,5,5,11,11,u,y,p); rect(c,6,11,10,14,u,y,p);
        p.setColor(Color.rgb(45,45,50)); rect(c,6,7,7,8,u,y,p); rect(c,9,7,10,8,u,y,p);
        p.setColor(Color.rgb(230,120,130)); rect(c,7.5f,8.5f,8.5f,9.2f,u,y,p);
        p.setColor(Color.rgb(240,170,85)); rect(c,11,5,13,9,u,y,p);
    }

    private void drawSit(Canvas c,float u){
        int f=frame(420); float bob=((f&1)==0?0:.12f)*u;
        p.setColor(Color.rgb(40,40,45));
        rect(c,5,4,11,10,u,bob,p); rect(c,4,3,6,6,u,bob,p); rect(c,10,3,12,6,u,bob,p);
        rect(c,5,9,12,14,u,bob,p); rect(c,3,12,6,14,u,bob,p);
        p.setColor(Color.rgb(246,246,242)); rect(c,6,5,10,10,u,bob,p); rect(c,7,10,10,13,u,bob,p);
        face(c,u,bob,false);
    }

    private void drawSleep(Canvas c,float u){
        int f=frame(650); float breathe=((f&1)==0?0:.2f)*u;
        p.setColor(Color.rgb(40,40,45)); rect(c,3,7,13,13,u,breathe,p);
        p.setColor(Color.rgb(246,246,242)); rect(c,5,7,11,11,u,breathe,p);
        p.setColor(Color.rgb(55,55,60)); rect(c,6,9,7.5f,9.5f,u,breathe,p); rect(c,8.5f,9,10,9.5f,u,breathe,p);
        p.setColor(Color.DKGRAY); p.setTextSize(u*2.0f); c.drawText((f&1)==0?"z":"Z",12*u,5*u,p);
    }

    private void drawGroom(Canvas c,float u){ int f=frame(220); drawSit(c,u); p.setColor(Color.rgb(246,246,242)); float pawY=((f&1)==0?6.5f:8.0f); rect(c,10.5f,pawY,12.5f,pawY+2,u,0,p); p.setColor(Color.rgb(230,120,130)); rect(c,9.8f,8.2f,10.8f,9.0f,u,0,p); }
    private void drawStretch(Canvas c,float u){ int f=frame(320); float front=((f&1)==0?1.0f:1.5f); p.setColor(Color.rgb(40,40,45)); rect(c,3,7,11,12,u,0,p); rect(c,2,6,5,9,u,0,p); rect(c,10,6,13,10,u,0,p); p.setColor(Color.rgb(246,246,242)); rect(c,4,8,10,11,u,0,p); p.setColor(Color.rgb(40,40,45)); rect(c,1,11,5,12+front,u,0,p); rect(c,4,11,8,12+front,u,0,p); }
    private void drawScratch(Canvas c,float u){ int f=frame(150); drawSit(c,u); p.setColor(Color.rgb(40,40,45)); float y=(f&1)==0?5.5f:7.2f; rect(c,11.5f,y,13.5f,y+2,u,0,p); }
    private void drawLookAround(Canvas c,float u){ int f=frame(500); drawBody(c,u,0); p.setColor(Color.rgb(45,45,50)); if((f&1)==0){ rect(c,5.5f,7,6.5f,8,u,0,p); rect(c,8.5f,7,9.5f,8,u,0,p); } else { rect(c,6.5f,7,7.5f,8,u,0,p); rect(c,9.5f,7,10.5f,8,u,0,p); } p.setColor(Color.rgb(40,40,45)); rect(c,5,13,7,15,u,0,p); rect(c,9,13,11,15,u,0,p); }
    private void drawPlay(Canvas c,float u){ int f=frame(180); drawStanding(c,u); p.setColor(Color.rgb(210,80,90)); float bx=(f%4<2)?13.0f:11.5f, by=(f%4<2)?11.5f:9.5f; c.drawCircle(bx*u,by*u,1.1f*u,p); p.setStrokeWidth(.35f*u); c.drawLine(12*u,8*u,bx*u,by*u,p); }
    private void drawPounce(Canvas c,float u){ int f=frame(120); float jump=(f%4==1||f%4==2)?-1.2f*u:0; c.save(); c.translate(0,jump); p.setColor(Color.rgb(40,40,45)); rect(c,3,6,12,11,u,0,p); rect(c,2,5,5,8,u,0,p); p.setColor(Color.rgb(246,246,242)); rect(c,4,7,10,10,u,0,p); p.setColor(Color.rgb(40,40,45)); rect(c,1,10,5,12,u,0,p); rect(c,9,10,14,12,u,0,p); c.restore(); }
    private void drawEatDrink(Canvas c,float u,boolean water){ int f=frame(240); float head=(f&1)==0?0:.5f; p.setColor(Color.rgb(40,40,45)); rect(c,5,5+head,11,11+head,u,0,p); rect(c,4,4+head,6,7+head,u,0,p); rect(c,10,4+head,12,7+head,u,0,p); p.setColor(Color.rgb(246,246,242)); rect(c,6,6+head,10,10+head,u,0,p); p.setColor(water?Color.rgb(90,160,220):Color.rgb(180,120,70)); rect(c,4,12,12,14,u,0,p); rect(c,5,11.5f,11,12.5f,u,0,p); }

    private void drawClimb(Canvas c,float u){ int f=frame(130); c.rotate(-90,getWidth()/2f,getHeight()/2f); drawBody(c,u,0); p.setColor(Color.rgb(40,40,45)); float a=(f&1)==0?0:.8f; rect(c,4,13+a,6,15,u,0,p); rect(c,10,13+(a==0?.8f:0),12,15,u,0,p); }
    private void drawHang(Canvas c,float u){ int f=frame(280); c.rotate(180,getWidth()/2f,getHeight()/2f); float sway=(f&1)==0?-.3f:.3f; c.translate(sway*u,0); drawBody(c,u,0); p.setColor(Color.rgb(40,40,45)); rect(c,4,13,6,15,u,0,p); rect(c,10,13,12,15,u,0,p); }
    private void drawFall(Canvas c,float u){ int f=frame(90); float rot=(f%4-1.5f)*7f; c.rotate(rot,getWidth()/2f,getHeight()/2f); drawBody(c,u,0); }
    private void drawAttention(Canvas c,float u){ int f=frame(180); float hop=(f%4==1)?-.8f*u:0; c.translate(0,hop); drawStanding(c,u); p.setColor(Color.rgb(240,100,120)); p.setTextSize(2*u); c.drawText("!",12.5f*u,3*u,p); }
    private void drawYawn(Canvas c,float u){ drawSit(c,u); int f=frame(320); p.setColor(Color.rgb(80,45,50)); float mouth=(f&1)==0?.8f:1.5f; c.drawOval(7.2f*u,8.4f*u,8.8f*u,(8.4f+mouth)*u,p); }
    private void drawKnead(Canvas c,float u){ int f=frame(150); drawSit(c,u); p.setColor(Color.rgb(246,246,242)); float l=(f&1)==0?10.8f:11.6f; float r=(f&1)==0?11.6f:10.8f; rect(c,5,l,7,14,u,0,p); rect(c,9,r,11,14,u,0,p); }
    private void drawWake(Canvas c,float u){ int f=frame(220); if(f<3) drawSleep(c,u); else drawStretch(c,u); }

    private void face(Canvas c,float u,float y,boolean closed){ p.setColor(Color.rgb(45,45,50)); if(closed){ rect(c,6,7,7.5f,7.4f,u,y,p); rect(c,8.5f,7,10,7.4f,u,y,p); } else { rect(c,6,7,7,8,u,y,p); rect(c,9,7,10,8,u,y,p); } p.setColor(Color.rgb(230,120,130)); rect(c,7.5f,8.5f,8.5f,9.2f,u,y,p); }
    private void rect(Canvas c,float l,float t,float r,float b,float u,float y,Paint paint){ c.drawRect(l*u,t*u+y,r*u,b*u+y,paint); }
}
