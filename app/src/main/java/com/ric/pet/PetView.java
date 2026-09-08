package com.ric.pet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

public class PetView extends View {
    public enum State { IDLE,WALK,RUN,SIT,SLEEP,HAPPY,GROOM,STRETCH,SCRATCH,LOOK_AROUND,PLAY,POUNCE,EAT,DRINK,ZOOMIES,CLIMB,HANG,FALL,ATTENTION,YAWN,KNEAD,WAKE,TYPING,SCROLL,MEDIA_WATCH,MUSIC }
    private final Paint p=new Paint();
    private State state=State.IDLE;
    private long since=SystemClock.uptimeMillis();
    private boolean flip;
    private String cat;

    public PetView(Context c){ super(c); p.setAntiAlias(false); cat=PetPreferences.cat(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null); }
    public State getState(){ return state; }
    public void setState(State s){ if(state!=s){state=s;since=SystemClock.uptimeMillis();} invalidate(); }
    public void setFlip(boolean f){ flip=f; invalidate(); }
    public void setFacing(int d){ setFlip(d<0); }
    public void refreshCharacter(){ cat=PetPreferences.cat(getContext()); invalidate(); }

    private long age(){ return SystemClock.uptimeMillis()-since; }
    private int frame(long ms){ return (int)(age()/ms); }
    private float u(){ return Math.max(1f,Math.min(getWidth(),getHeight())/16f); }
    private boolean humanoid(){ return "YUKI".equals(cat)||"REN".equals(cat); }
    private void rect(Canvas c,float l,float t,float r,float b,float u,int color){ p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRect(l*u,t*u,r*u,b*u,p); }
    private void px(Canvas c,float x,float y,float u,int color){ rect(c,x,y,x+1,y+1,u,color); }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);float u=u();c.save();if(flip)c.scale(-1,1,getWidth()/2f,getHeight()/2f);
        if(humanoid())drawNekoGirl(c,u);else if("MOMO".equals(cat))drawNaturalMomo(c,u);else drawTora(c,u);
        c.restore();postInvalidateDelayed(state==State.RUN||state==State.ZOOMIES||state==State.MUSIC?70:110);
    }

    private void drawNekoGirl(Canvas c,float u){
        int f=frame(state==State.RUN?90:150);float bob=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?(f%2==0?0:.25f):0;
        if(state==State.MUSIC)bob=(float)Math.sin(age()/120.0)*.35f;
        c.save();c.translate(0,bob*u);boolean yuki="YUKI".equals(cat);
        int hair=yuki?Color.rgb(205,178,236):Color.rgb(247,180,202);int hairDark=yuki?Color.rgb(157,128,194):Color.rgb(212,132,164);
        int skin=Color.rgb(253,222,207);int cloth=yuki?Color.rgb(245,177,211):Color.rgb(178,224,214);int cloth2=yuki?Color.rgb(255,224,239):Color.rgb(226,247,241);
        int dark=Color.rgb(66,54,67);int blush=Color.rgb(244,148,167);
        rect(c,5,2,11,7,u,hair);rect(c,4,1,6,4,u,hairDark);rect(c,10,1,12,4,u,hairDark);
        rect(c,4.5f,1.5f,5.5f,3,u,Color.rgb(248,174,191));rect(c,10.5f,1.5f,11.5f,3,u,Color.rgb(248,174,191));
        rect(c,6,3,10,7,u,skin);rect(c,6,4,7,5,u,dark);rect(c,9,4,10,5,u,dark);px(c,6.2f,4.1f,u,Color.WHITE);px(c,9.2f,4.1f,u,Color.WHITE);
        px(c,5.4f,5.4f,u,blush);px(c,10.6f,5.4f,u,blush);rect(c,7.4f,5.5f,8.6f,5.9f,u,Color.rgb(221,112,133));
        rect(c,5,7,11,11,u,cloth);rect(c,6,7,10,8,u,cloth2);rect(c,4.5f,10.5f,11.5f,12.5f,u,cloth);rect(c,5.3f,11.5f,10.7f,13,u,cloth2);
        rect(c,3,8,5,11,u,skin);rect(c,11,8,13,11,u,skin);
        if(yuki){rect(c,7,7.5f,8,8.5f,u,Color.rgb(235,112,157));rect(c,8,7.5f,9,8.5f,u,Color.rgb(235,112,157));}
        else{rect(c,3.2f,2.3f,4.5f,3.6f,u,Color.rgb(240,115,150));rect(c,4.3f,2.3f,5.4f,3.6f,u,Color.rgb(240,115,150));}
        int step=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?f%2:0;rect(c,5,13+step*.4f,7,15,u,Color.rgb(92,74,95));rect(c,9,13+(1-step)*.4f,11,15,u,Color.rgb(92,74,95));drawHumanTail(c,u,f,hairDark);
        if(state==State.HAPPY||state==State.ATTENTION){px(c,2,2,u,Color.rgb(246,111,158));px(c,13,2,u,Color.rgb(246,111,158));}
        if(state==State.SLEEP){rect(c,4,10,12,14,u,cloth);p.setColor(dark);p.setTextSize(2*u);c.drawText("Z",12*u,5*u,p);}
        if(state==State.TYPING)keyboard(c,u,f);if(state==State.SCROLL)scrollMark(c,u,f);if(state==State.PLAY){p.setColor(Color.rgb(226,89,116));c.drawCircle(13*u,11*u,u,p);}
        if(state==State.YAWN){p.setColor(Color.rgb(110,58,74));c.drawOval(7.2f*u,5.4f*u,8.8f*u,6.5f*u,p);}
        if(state==State.MEDIA_WATCH)drawMovieProps(c,u,f,skin);if(state==State.MUSIC)drawHeadphonesAndNotes(c,u,f,dark);
        c.restore();
    }

    private void drawHumanTail(Canvas c,float u,int f,int color){float y=f%2==0?9:8;rect(c,11.5f,y,14,y+1.5f,u,color);rect(c,13,y-1.5f,15,y+.5f,u,color);}

    private void drawTora(Canvas c,float u){
        int f=frame(state==State.RUN?90:170);float bob=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?(f%2==0?0:.25f):0;if(state==State.MUSIC)bob=(float)Math.sin(age()/120.0)*.30f;
        c.save();c.translate(0,bob*u);int body=Color.rgb(207,132,64),patch=Color.rgb(118,72,42),dark=Color.rgb(54,49,49);
        rect(c,4,4,12,12,u,dark);rect(c,3,3,6,6,u,dark);rect(c,10,3,13,6,u,dark);rect(c,5,4,11,12,u,body);rect(c,4,4,6,6,u,body);rect(c,10,4,12,6,u,body);
        rect(c,6,4,7,6,u,patch);rect(c,8,4,9,6,u,patch);rect(c,10,4,11,6,u,patch);rect(c,5,10,11,12,u,Color.rgb(230,169,92));px(c,6,7,u,dark);px(c,9,7,u,dark);px(c,7.5f,8.3f,u,Color.rgb(236,142,147));
        int step=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?f%2:0;rect(c,4,12+step*.5f,7,14,u,dark);rect(c,9,12+(1-step)*.5f,12,14,u,dark);rect(c,12,8,14,10,u,dark);rect(c,13,6,15,9,u,dark);
        if(state==State.SLEEP){rect(c,3,8,13,13,u,dark);rect(c,4,8,12,12,u,body);p.setColor(dark);p.setTextSize(2*u);c.drawText("Z",12*u,5*u,p);}if(state==State.TYPING)keyboard(c,u,f);if(state==State.SCROLL)scrollMark(c,u,f);
        if(state==State.MEDIA_WATCH)drawMovieProps(c,u,f,body);if(state==State.MUSIC)drawHeadphonesAndNotes(c,u,f,dark);c.restore();
    }

    private void drawNaturalMomo(Canvas c,float u){
        int f=frame(state==State.RUN?85:165);float bob=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?(f%2==0?0:.22f):0;if(state==State.MUSIC)bob=(float)Math.sin(age()/115.0)*.28f;
        c.save();c.translate(0,bob*u);p.setAntiAlias(true);int fur=Color.rgb(165,161,154),cream=Color.rgb(231,226,216),dark=Color.rgb(59,55,54),pink=Color.rgb(231,143,151);
        if(state==State.SLEEP){p.setColor(dark);c.drawOval(2.7f*u,7.1f*u,13.3f*u,13.5f*u,p);p.setColor(fur);c.drawOval(3.2f*u,7.4f*u,12.8f*u,13.0f*u,p);p.setColor(cream);c.drawOval(4.0f*u,8.2f*u,8.2f*u,11.7f*u,p);p.setStrokeWidth(.45f*u);p.setColor(dark);c.drawLine(5.0f*u,9.3f*u,6.0f*u,9.3f*u,p);c.drawLine(6.7f*u,9.3f*u,7.7f*u,9.3f*u,p);p.setTextSize(2*u);c.drawText("Z",12*u,6*u,p);c.restore();postInvalidateDelayed(160);return;}
        p.setColor(dark);c.drawOval(3.1f*u,6.0f*u,12.9f*u,12.8f*u,p);p.setColor(fur);c.drawOval(3.5f*u,6.3f*u,12.5f*u,12.4f*u,p);
        p.setColor(dark);c.drawCircle(7.0f*u,6.2f*u,3.0f*u,p);p.setColor(fur);c.drawCircle(7.0f*u,6.2f*u,2.6f*u,p);
        p.setColor(fur);float[] left={4.6f*u,4.7f*u,5.2f*u,1.9f*u,6.1f*u,4.5f*u};float[] right={7.9f*u,4.5f*u,8.8f*u,1.9f*u,9.4f*u,4.7f*u};c.drawVertices(Canvas.VertexMode.TRIANGLES,6,left,0,null,0,null,0,null,0,0,p);c.drawVertices(Canvas.VertexMode.TRIANGLES,6,right,0,null,0,null,0,null,0,0,p);
        p.setColor(cream);c.drawOval(4.9f*u,6.0f*u,9.1f*u,8.8f*u,p);p.setColor(dark);c.drawCircle(5.8f*u,6.6f*u,.35f*u,p);c.drawCircle(8.2f*u,6.6f*u,.35f*u,p);p.setColor(pink);c.drawCircle(7.0f*u,7.5f*u,.30f*u,p);
        p.setStrokeWidth(.24f*u);p.setColor(dark);c.drawLine(4.8f*u,7.5f*u,2.4f*u,7.0f*u,p);c.drawLine(4.8f*u,7.9f*u,2.5f*u,8.2f*u,p);c.drawLine(9.1f*u,7.5f*u,11.6f*u,7.0f*u,p);c.drawLine(9.1f*u,7.9f*u,11.5f*u,8.2f*u,p);
        int step=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?f%2:0;p.setColor(dark);c.drawOval(3.6f*u,(11.2f+step*.35f)*u,6.3f*u,13.7f*u,p);c.drawOval(8.7f*u,(11.2f+(1-step)*.35f)*u,11.4f*u,13.7f*u,p);p.setColor(cream);c.drawOval(4.0f*u,(11.5f+step*.35f)*u,6.0f*u,13.3f*u,p);c.drawOval(9.0f*u,(11.5f+(1-step)*.35f)*u,11.0f*u,13.3f*u,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.1f*u);p.setColor(fur);c.drawArc(10.8f*u,6.8f*u,15.0f*u,12.8f*u,-65,210,false,p);p.setStyle(Paint.Style.FILL);
        if(state==State.HAPPY||state==State.ATTENTION){p.setColor(Color.rgb(241,111,139));c.drawCircle(12.8f*u,3.2f*u,.65f*u,p);}if(state==State.TYPING)keyboard(c,u,f);if(state==State.SCROLL)scrollMark(c,u,f);
        if(state==State.MEDIA_WATCH)drawMovieProps(c,u,f,fur);if(state==State.MUSIC)drawHeadphonesAndNotes(c,u,f,dark);c.restore();p.setAntiAlias(false);
    }

    private void drawMovieProps(Canvas c,float u,int f,int handColor){
        boolean cola=(f/8)%2==1;float lift=(f%2==0?0:.25f);if(!cola){rect(c,10.3f,9.5f-lift,14.2f,13.8f-lift,u,Color.rgb(214,49,64));rect(c,10.6f,10.0f-lift,11.3f,13.5f-lift,u,Color.WHITE);rect(c,12.0f,10.0f-lift,12.7f,13.5f-lift,u,Color.WHITE);rect(c,13.4f,10.0f-lift,14.0f,13.5f-lift,u,Color.WHITE);p.setColor(Color.rgb(250,225,151));for(int i=0;i<5;i++)c.drawCircle((10.8f+i*.7f)*u,(9.5f-(i%2)*.45f-lift)*u,.45f*u,p);}else{rect(c,10.8f,9.4f-lift,13.7f,13.8f-lift,u,Color.rgb(188,39,49));rect(c,10.6f,9.1f-lift,13.9f,9.7f-lift,u,Color.WHITE);p.setStrokeWidth(.28f*u);p.setColor(Color.rgb(82,62,62));c.drawLine(12.7f*u,(9.2f-lift)*u,13.4f*u,(7.4f-lift)*u,p);}rect(c,9.5f,10.4f-lift,10.8f,11.5f-lift,u,handColor);
    }

    private void drawHeadphonesAndNotes(Canvas c,float u,int f,int dark){
        p.setAntiAlias(true);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(.55f*u);p.setColor(Color.rgb(66,69,83));c.drawArc(4.2f*u,1.2f*u,11.8f*u,7.5f*u,195,150,false,p);p.setStyle(Paint.Style.FILL);c.drawRoundRect(3.8f*u,4.0f*u,5.2f*u,7.0f*u,.4f*u,.4f*u,p);c.drawRoundRect(10.8f*u,4.0f*u,12.2f*u,7.0f*u,.4f*u,.4f*u,p);
        int note=Color.rgb(237,104,158);float drift=(f%5)*.25f;drawNote(c,13.0f*u,(4.0f-drift)*u,u,note);drawNote(c,2.3f*u,(6.2f+drift*.25f)*u,u,Color.rgb(106,173,222));p.setAntiAlias(false);
    }

    private void drawNote(Canvas c,float x,float y,float u,int color){p.setColor(color);p.setStrokeWidth(.28f*u);c.drawCircle(x,y+.9f*u,.45f*u,p);c.drawRect(x+.32f*u,y-1.1f*u,x+.62f*u,y+.9f*u,p);c.drawRect(x+.55f*u,y-1.1f*u,x+1.25f*u,y-.75f*u,p);}
    private void keyboard(Canvas c,float u,int f){rect(c,3,11.5f,13,14,u,Color.rgb(55,58,68));for(int x=4;x<12;x+=2)px(c,x,12.2f+(f+x)%2*.25f,u,Color.WHITE);}
    private void scrollMark(Canvas c,float u,int f){p.setColor(Color.rgb(80,85,100));p.setStrokeWidth(Math.max(1,u*.25f));float x=13.5f*u,y=(5+(f%3))*u;c.drawLine(x,4*u,x,11*u,p);c.drawLine(x,y,x-.7f*u,y+.8f*u,p);c.drawLine(x,y,x+.7f*u,y+.8f*u,p);}
}
