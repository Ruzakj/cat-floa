package com.ric.pet;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.View;
import java.util.ArrayDeque;

public class PetView extends View {
    public enum State { IDLE,WALK,RUN,SIT,SLEEP,HAPPY,GROOM,STRETCH,SCRATCH,LOOK_AROUND,PLAY,POUNCE,EAT,DRINK,ZOOMIES,CLIMB,HANG,FALL,ATTENTION,YAWN,KNEAD,WAKE,TYPING,SCROLL }
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Bitmap[] panels=new Bitmap[6];
    private State state=State.IDLE; private long since=SystemClock.uptimeMillis(); private boolean flip;

    public PetView(Context c){ super(c); setLayerType(View.LAYER_TYPE_SOFTWARE,null); Bitmap src=CatArt.decode(); if(src!=null) split(src); }
    public State getState(){return state;} public void setState(State s){state=s;since=SystemClock.uptimeMillis();invalidate();}
    public void setFlip(boolean f){flip=f;invalidate();} public void setFacing(int d){setFlip(d<0);}

    private void split(Bitmap src){ int w=src.getWidth()/3,h=src.getHeight()/2; for(int i=0;i<6;i++){ int x=(i%3)*w,y=(i/3)*h; Bitmap b=Bitmap.createBitmap(src,x,y,w,h).copy(Bitmap.Config.ARGB_8888,true); panels[i]=removePastelBackground(b); } }
    private Bitmap removePastelBackground(Bitmap b){ int w=b.getWidth(),h=b.getHeight(); boolean[] seen=new boolean[w*h]; ArrayDeque<Integer> q=new ArrayDeque<>(); int base=b.getPixel(1,1); for(int x=0;x<w;x++){q.add(x);q.add((h-1)*w+x);} for(int y=0;y<h;y++){q.add(y*w);q.add(y*w+w-1);} while(!q.isEmpty()){int n=q.removeFirst(); if(n<0||n>=seen.length||seen[n])continue; seen[n]=true; int x=n%w,y=n/w,c=b.getPixel(x,y); if(dist(c,base)>62)continue; b.setPixel(x,y,Color.TRANSPARENT); if(x>0)q.add(n-1);if(x<w-1)q.add(n+1);if(y>0)q.add(n-w);if(y<h-1)q.add(n+w);} return b; }
    private int dist(int a,int b){int dr=Color.red(a)-Color.red(b),dg=Color.green(a)-Color.green(b),db=Color.blue(a)-Color.blue(b);return (int)Math.sqrt(dr*dr+dg*dg+db*db);}
    private int panel(){ switch(state){ case SLEEP:return 3; case HAPPY:case ATTENTION:return 1; case STRETCH:case POUNCE:case RUN:case ZOOMIES:return 5; case TYPING:return 2; case SCROLL:return 4; case SIT:case GROOM:case YAWN:case KNEAD:case EAT:case DRINK:return 1; default:return 0; } }

    @Override protected void onDraw(Canvas c){ super.onDraw(c); Bitmap b=panels[panel()]; if(b==null)return; long age=SystemClock.uptimeMillis()-since; float bob=(state==State.WALK||state==State.RUN||state==State.ZOOMIES)?((age/130)%2==0?0:-getHeight()*.04f):0; float scroll=(state==State.SCROLL)?(float)Math.sin(age/100.0)*getHeight()*.035f:0; c.save(); if(flip)c.scale(-1,1,getWidth()/2f,getHeight()/2f); RectF dst=new RectF(getWidth()*.04f,getHeight()*.04f+bob+scroll,getWidth()*.96f,getHeight()*.96f+bob+scroll); c.drawBitmap(b,null,dst,p); c.restore(); if(state==State.TYPING)drawKeyboard(c,age); if(state==State.SCROLL)drawScroll(c,age); postInvalidateDelayed(90); }
    private void drawKeyboard(Canvas c,long age){ p.setColor(0xDD333333); RectF r=new RectF(getWidth()*.16f,getHeight()*.73f,getWidth()*.84f,getHeight()*.91f);c.drawRoundRect(r,8,8,p);p.setColor(Color.WHITE);float cw=r.width()/7f,ch=r.height()/3f; for(int y=0;y<2;y++)for(int x=0;x<6;x++){float dx=((age/120+x+y)%3==0)?1.5f:0;c.drawRect(r.left+8+x*cw,r.top+5+y*ch+dx,r.left+8+x*cw+cw*.55f,r.top+5+y*ch+ch*.45f+dx,p);} }
    private void drawScroll(Canvas c,long age){ p.setColor(0xCCFFFFFF);p.setStrokeWidth(Math.max(3,getWidth()*.025f));float x=getWidth()*.84f,y=getHeight()*(.32f+(float)((age/500)%2)*.12f);c.drawLine(x,y,x,y+getHeight()*.20f,p);c.drawLine(x,y,x-getWidth()*.05f,y+getHeight()*.06f,p);c.drawLine(x,y,x+getWidth()*.05f,y+getHeight()*.06f,p); }
}
