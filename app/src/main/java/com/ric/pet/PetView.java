package com.ric.pet;

import android.content.Context;import android.graphics.*;import android.os.SystemClock;import android.view.View;

/** Sprite-sheet animator only. Character art lives in PixelSpriteAtlas. */
public class PetView extends View{
 public enum State{IDLE,WALK,RUN,SIT,SLEEP,HAPPY,GROOM,STRETCH,SCRATCH,LOOK_AROUND,PLAY,POUNCE,EAT,DRINK,ZOOMIES,CLIMB,HANG,FALL,ATTENTION,YAWN,KNEAD,WAKE,TYPING,SCROLL,MEDIA_WATCH,MUSIC}
 private final Paint p=new Paint();private State state=State.IDLE;private long since=SystemClock.uptimeMillis();private boolean flip;private String cat;private Bitmap atlas;
 public PetView(Context c){super(c);p.setAntiAlias(false);p.setFilterBitmap(false);cat=PetPreferences.cat(c);atlas=PixelSpriteAtlas.build(cat);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}public State getState(){return state;}public void setState(State s){if(state!=s){state=s;since=SystemClock.uptimeMillis();}invalidate();}public void setFlip(boolean f){flip=f;invalidate();}public void setFacing(int d){setFlip(d<0);}public void refreshCharacter(){String n=PetPreferences.cat(getContext());if(!n.equals(cat)){cat=n;if(atlas!=null)atlas.recycle();atlas=PixelSpriteAtlas.build(cat);}invalidate();}
 private long age(){return SystemClock.uptimeMillis()-since;}private int row(){switch(state){case WALK:return 1;case RUN:case ZOOMIES:case POUNCE:return 2;case SLEEP:return 3;case HAPPY:case ATTENTION:case PLAY:return 4;case TYPING:return 5;case MEDIA_WATCH:case EAT:case DRINK:return 6;case MUSIC:return 7;default:return 0;}}private long frameMs(){switch(state){case RUN:case ZOOMIES:return 75;case WALK:return 125;case MUSIC:return 105;case TYPING:return 95;case MEDIA_WATCH:return 180;case SLEEP:return 420;default:return 220;}}
 @Override protected void onDraw(Canvas c){super.onDraw(c);if(atlas==null)return;int f=(int)((age()/frameMs())%PixelSpriteAtlas.COLS),r=row(),cs=PixelSpriteAtlas.CELL;Rect src=new Rect(f*cs,r*cs,(f+1)*cs,(r+1)*cs);float side=Math.min(getWidth(),getHeight());float left=(getWidth()-side)/2f,top=(getHeight()-side)/2f;RectF dst=new RectF(left,top,left+side,top+side);c.save();if(flip)c.scale(-1,1,getWidth()/2f,getHeight()/2f);c.drawBitmap(atlas,src,dst,p);c.restore();postInvalidateDelayed(Math.max(55,frameMs()/2));}
 @Override protected void onDetachedFromWindow(){super.onDetachedFromWindow();if(atlas!=null&&!atlas.isRecycled())atlas.recycle();atlas=null;}
}
