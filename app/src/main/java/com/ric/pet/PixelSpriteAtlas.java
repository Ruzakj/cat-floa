package com.ric.pet;

import android.graphics.*;

/** Runtime-built multi-frame pixel sprite sheets. 4 cols x 8 rows, 32x32 per frame. */
public final class PixelSpriteAtlas {
    public static final int CELL=32,COLS=4,ROWS=8;
    private PixelSpriteAtlas(){}
    public static Bitmap build(String id){Bitmap out=Bitmap.createBitmap(CELL*COLS,CELL*ROWS,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(out);Paint p=new Paint();p.setAntiAlias(false);for(int r=0;r<ROWS;r++)for(int f=0;f<COLS;f++){c.save();c.translate(f*CELL,r*CELL);if("YUKI".equals(id)||"REN".equals(id))neko(c,p,"YUKI".equals(id),r,f);else cat(c,p,"TORA".equals(id),r,f);c.restore();}return out;}
    private static void q(Canvas c,Paint p,int x,int y,int w,int h,int col){p.setColor(col);p.setStyle(Paint.Style.FILL);c.drawRect(x,y,x+w,y+h,p);}private static void tri(Canvas c,Paint p,float a,float b,float d,float e,float f,float g,int col){p.setColor(col);Path z=new Path();z.moveTo(a,b);z.lineTo(d,e);z.lineTo(f,g);z.close();c.drawPath(z,p);}
    private static void cat(Canvas c,Paint p,boolean tora,int row,int fr){int fur=tora?0xffca7a34:0xff22232a,shade=tora?0xff713d24:0xff101116,chest=tora?0xffefc18a:0xffe7e7e3,eye=tora?0xffffeca0:0xffd7f0d7,white=Color.WHITE,pink=0xffef8f9b;int bob=(row==1||row==2||row==7)?new int[]{0,1,0,-1}[fr]:0;if(row==3){q(c,p,7,17+fr%2,18,9,shade);q(c,p,8,18+fr%2,16,7,fur);q(c,p,5,13+fr%2,11,8,shade);q(c,p,6,14+fr%2,9,6,fur);tri(c,p,6,14,8,9,10,14,fur);tri(c,p,11,14,14,9,16,14,fur);q(c,p,7,17,2,1,white);q(c,p,12,17,2,1,white);q(c,p,9,19,4,2,chest);return;}int y=7+bob;
      // body: low, horizontal cat silhouette
      q(c,p,9,y+11,15,9,shade);q(c,p,10,y+12,13,7,fur);q(c,p,12,y+14,4,5,chest);
      // head wide + cheeks
      q(c,p,5,y+3,14,10,shade);q(c,p,6,y+4,12,8,fur);q(c,p,5,y+9,14,5,fur);
      // ears
      tri(c,p,6,y+5,9,y-1,11,y+5,shade);tri(c,p,13,y+5,16,y-1,18,y+5,shade);tri(c,p,7,y+4,9,y+1,10,y+4,fur);tri(c,p,14,y+4,16,y+1,17,y+4,fur);
      // eyes/muzzle/nose
      q(c,p,8,y+7,2,1,eye);q(c,p,14,y+7,2,1,eye);q(c,p,9,y+7,1,1,shade);q(c,p,15,y+7,1,1,shade);q(c,p,9,y+10,6,3,chest);q(c,p,11,y+10,2,1,pink);q(c,p,10,y+12,1,1,white);q(c,p,14,y+12,1,1,white);
      // whiskers
      q(c,p,1,y+10,6,1,white);q(c,p,2,y+12,5,1,white);q(c,p,18,y+10,7,1,white);q(c,p,18,y+12,6,1,white);
      if(tora){q(c,p,10,y+4,1,2,shade);q(c,p,12,y+4,1,2,shade);q(c,p,14,y+4,1,2,shade);q(c,p,18,y+15,2,1,shade);}int g=(row==1||row==2)?new int[]{0,1,0,-1}[fr]*(row==2?2:1):0;q(c,p,10,y+18+g,4,4,shade);q(c,p,11,y+18+g,2,3,fur);q(c,p,19,y+18-g,4,4,shade);q(c,p,20,y+18-g,2,3,fur);
      // curled tail
      q(c,p,23,y+13,3,2,shade);q(c,p,25,y+10+(fr%2),2,5,shade);q(c,p,26,y+8+(fr%2),3,2,shade);q(c,p,24,y+13,2,1,fur);q(c,p,26,y+10+(fr%2),1,4,fur);q(c,p,27,y+9+(fr%2),2,1,fur);
      if(row==4){q(c,p,7,y+7,4,1,white);q(c,p,13,y+7,4,1,white);q(c,p,4,y+2,1,1,pink);}if(row==5){q(c,p,5,y+20,22,5,0xff3a3c44);for(int k=0;k<5;k++)q(c,p,8+k*4,y+22+((k+fr)&1),2,1,white);}if(row==6){if((fr&1)==0){q(c,p,22,y+17,7,8,0xffd22d3c);for(int k=0;k<4;k++)q(c,p,23+k*2,y+15-(k&1),1,1,0xffffe197);}else{q(c,p,23,y+17,5,8,0xffbe2330);q(c,p,23,y+17,5,1,white);}}if(row==7){q(c,p,5,y+5,2,6,0xff50525f);q(c,p,18,y+5,2,6,0xff50525f);q(c,p,7,y+2,11,1,0xff50525f);q(c,p,27,y+2+(fr&1),1,5,white);q(c,p,25,y+6+(fr&1),2,1,white);}}
    private static void neko(Canvas c,Paint p,boolean yuki,int row,int fr){int hair=yuki?0xffcdb2ec:0xfff7b4ca,hd=yuki?0xff9d80c2:0xffd484a4,skin=0xffffdecf,cloth=yuki?0xfff5b1d3:0xffb2e0d6,cloth2=yuki?0xffffe0ef:0xffe2f7f1,dark=0xff423643,blush=0xfff494a7,white=Color.WHITE;if(row==3){q(c,p,10,16,13,8,hd);q(c,p,11,17,11,6,cloth);q(c,p,7,12,10,7,hd);q(c,p,8,13,8,5,skin);tri(c,p,8,13,10,8,12,13,hd);tri(c,p,13,13,15,8,17,13,hd);q(c,p,10,16,2,1,dark);q(c,p,14,16,2,1,dark);return;}int bob=(row==1||row==2||row==7)?new int[]{0,1,0,-1}[fr]:0,y=4+bob;q(c,p,9,y+2,13,10,hd);q(c,p,10,y+3,11,8,hair);tri(c,p,9,y+4,11,y-1,13,y+4,hd);tri(c,p,18,y+4,20,y-1,22,y+4,hd);q(c,p,11,y+5,9,7,skin);q(c,p,13,y+7,2,2,dark);q(c,p,17,y+7,2,2,dark);q(c,p,12,y+10,1,1,blush);q(c,p,20,y+10,1,1,blush);q(c,p,11,y+12,10,9,cloth);q(c,p,10,y+19,12,4,cloth2);int g=(row==1||row==2)?new int[]{0,1,0,-1}[fr]*(row==2?2:1):0;q(c,p,12,y+23+g,3,6,dark);q(c,p,18,y+23-g,3,6,dark);q(c,p,21,y+15,3,2,hd);q(c,p,23,y+13+(fr&1),4,2,hd);if(row==4){q(c,p,7,y+3,1,1,blush);q(c,p,24,y+3,1,1,blush);}if(row==5){q(c,p,7,y+21,19,5,0xff3a3c44);for(int k=0;k<5;k++)q(c,p,9+k*3,y+23+((k+fr)&1),2,1,white);}if(row==6){if((fr&1)==0){q(c,p,22,y+18,7,8,0xffd22d3c);for(int k=0;k<4;k++)q(c,p,23+k*2,y+16-(k&1),1,1,0xffffe197);}else{q(c,p,23,y+18,5,8,0xffbe2330);q(c,p,23,y+18,5,1,white);}}if(row==7){q(c,p,9,y+4,2,6,0xff50525f);q(c,p,21,y+4,2,6,0xff50525f);q(c,p,11,y+1,10,1,0xff50525f);q(c,p,27,y+2+(fr&1),1,5,white);q(c,p,25,y+6+(fr&1),2,1,white);}}
}
