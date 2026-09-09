package com.ric.pet;

import android.graphics.*;

/** Layered vector/skeletal renderer for real cat silhouettes. */
public final class FelineRenderer {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path=new Path();

    public void draw(Canvas c,float w,float h,boolean tora,PetView.State state,long age){
        float s=Math.min(w,h);float ox=(w-s)/2f,oy=(h-s)/2f;c.save();c.translate(ox,oy);
        int fur=tora?Color.rgb(207,126,55):Color.rgb(37,38,43);
        int fur2=tora?Color.rgb(141,79,39):Color.rgb(58,60,68);
        int chest=tora?Color.rgb(239,188,125):Color.rgb(232,231,226);
        int eye=tora?Color.rgb(244,210,102):Color.rgb(188,225,188);
        int outline=Color.rgb(28,27,30);
        float run=(state==PetView.State.RUN||state==PetView.State.ZOOMIES)?1f:0f;
        float walk=(state==PetView.State.WALK||run>0)?1f:0f;
        float bob=(float)Math.sin(age/(run>0?75.0:130.0))*s*(walk>.5f?.018f:.004f);
        float breathe=(float)Math.sin(age/430.0)*s*.007f;
        if(state==PetView.State.MUSIC)bob=(float)Math.sin(age/95.0)*s*.022f;
        c.translate(0,bob);

        if(state==PetView.State.SLEEP){drawSleep(c,s,fur,fur2,chest,outline,age);c.restore();return;}

        // tail behind body: thick Bezier curve, clearly connected at rump.
        p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);p.setStrokeWidth(s*.085f);p.setColor(outline);
        path.reset();path.moveTo(s*.69f,s*.64f);path.cubicTo(s*.89f,s*.66f,s*.94f,s*.45f,s*.82f,s*.39f);c.drawPath(path,p);
        p.setStrokeWidth(s*.058f);p.setColor(fur);c.drawPath(path,p);

        // body, low and horizontal rather than humanoid/boxy.
        p.setStyle(Paint.Style.FILL);p.setColor(outline);c.drawOval(s*.24f,s*(.46f+breathe/s),s*.76f,s*.82f,p);
        p.setColor(fur);c.drawOval(s*.265f,s*(.475f+breathe/s),s*.735f,s*.795f,p);
        // chest tuft
        p.setColor(chest);c.drawOval(s*.31f,s*.57f,s*.48f,s*.76f,p);

        // head is wider than neck/body front.
        p.setColor(outline);c.drawOval(s*.18f,s*.19f,s*.59f,s*.58f,p);
        p.setColor(fur);c.drawOval(s*.20f,s*.215f,s*.57f,s*.555f,p);

        // ears with true triangular silhouette and inner ear.
        drawEar(c,s*.245f,s*.265f,s*.285f,s*.10f,s*.365f,s*.255f,fur,outline);
        drawEar(c,s*.43f,s*.255f,s*.505f,s*.095f,s*.555f,s*.285f,fur,outline);

        // muzzle/cheeks create unmistakable feline face.
        p.setColor(chest);c.drawOval(s*.275f,s*.385f,s*.465f,s*.535f,p);
        // eyes slightly angled
        p.setColor(outline);c.drawOval(s*.27f,s*.31f,s*.335f,s*.365f,p);c.drawOval(s*.43f,s*.305f,s*.495f,s*.36f,p);
        p.setColor(eye);c.drawOval(s*.282f,s*.318f,s*.326f,s*.357f,p);c.drawOval(s*.442f,s*.313f,s*.486f,s*.352f,p);
        p.setColor(Color.BLACK);c.drawOval(s*.3f,s*.322f,s*.312f,s*.356f,p);c.drawOval(s*.46f,s*.317f,s*.472f,s*.351f,p);
        p.setColor(Color.WHITE);c.drawCircle(s*.289f,s*.326f,s*.006f,p);c.drawCircle(s*.449f,s*.321f,s*.006f,p);
        // nose and W mouth
        p.setColor(Color.rgb(231,139,151));path.reset();path.moveTo(s*.375f,s*.405f);path.lineTo(s*.348f,s*.392f);path.lineTo(s*.402f,s*.392f);path.close();c.drawPath(path,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(s*.012f);p.setStrokeCap(Paint.Cap.ROUND);p.setColor(outline);path.reset();path.moveTo(s*.375f,s*.407f);path.cubicTo(s*.365f,s*.44f,s*.34f,s*.445f,s*.326f,s*.43f);path.moveTo(s*.375f,s*.407f);path.cubicTo(s*.385f,s*.44f,s*.41f,s*.445f,s*.424f,s*.43f);c.drawPath(path,p);
        // whiskers
        p.setStrokeWidth(s*.009f);for(int i=0;i<3;i++){float yy=s*(.405f+i*.035f);c.drawLine(s*.285f,yy,s*(.11f-i*.008f),yy-s*.012f*i,p);c.drawLine(s*.465f,yy,s*(.64f+i*.008f),yy-s*.012f*i,p);}p.setStyle(Paint.Style.FILL);

        // Tora stripes, subtle but feline.
        if(tora){p.setColor(fur2);drawStripe(c,s,.315f,.225f,.335f,.285f);drawStripe(c,s,.38f,.215f,.395f,.275f);drawStripe(c,s,.445f,.225f,.455f,.282f);drawStripe(c,s,.60f,.52f,.64f,.60f);}

        // legs + paws with gait. Short legs keep cat proportions.
        float phase=(float)Math.sin(age/(run>0?70.0:120.0));
        drawLeg(c,s,.32f,.69f,walk>0?phase:0,fur,outline,chest);
        drawLeg(c,s,.61f,.69f,walk>0?-phase:0,fur,outline,chest);

        // activity expressions
        if(state==PetView.State.HAPPY||state==PetView.State.ATTENTION){p.setColor(Color.rgb(246,112,150));c.drawCircle(s*.16f,s*.20f,s*.025f,p);c.drawCircle(s*.61f,s*.18f,s*.018f,p);}
        if(state==PetView.State.GROOM){p.setColor(chest);c.drawOval(s*.50f,s*.47f,s*.61f,s*.64f,p);}
        if(state==PetView.State.YAWN){p.setColor(Color.rgb(92,46,52));c.drawOval(s*.35f,s*.435f,s*.405f,s*.49f,p);}
        c.restore();
    }

    private void drawEar(Canvas c,float ax,float ay,float tx,float ty,float bx,float by,int fur,int outline){
        p.setStyle(Paint.Style.FILL);p.setColor(outline);path.reset();path.moveTo(ax,ay);path.lineTo(tx,ty);path.lineTo(bx,by);path.close();c.drawPath(path,p);
        p.setColor(fur);float cx=(ax+tx+bx)/3f,cy=(ay+ty+by)/3f;path.reset();path.moveTo((ax+cx)*.5f,(ay+cy)*.5f);path.lineTo((tx+cx)*.5f,(ty+cy)*.5f);path.lineTo((bx+cx)*.5f,(by+cy)*.5f);path.close();c.drawPath(path,p);
        p.setColor(Color.rgb(226,145,158));path.reset();path.moveTo((ax+cx)*.58f,(ay+cy)*.58f);path.lineTo((tx+cx)*.58f,(ty+cy)*.58f);path.lineTo((bx+cx)*.58f,(by+cy)*.58f);path.close();c.drawPath(path,p);
    }

    private void drawLeg(Canvas c,float s,float x,float y,float swing,int fur,int outline,int paw){
        float dy=swing*s*.035f;p.setColor(outline);c.drawRoundRect(s*(x-.045f),s*y+dy,s*(x+.045f),s*(y+.17f)+dy,s*.035f,s*.035f,p);
        p.setColor(fur);c.drawRoundRect(s*(x-.032f),s*(y+.005f)+dy,s*(x+.032f),s*(y+.135f)+dy,s*.025f,s*.025f,p);
        p.setColor(paw);c.drawOval(s*(x-.052f),s*(y+.12f)+dy,s*(x+.052f),s*(y+.175f)+dy,p);
    }

    private void drawStripe(Canvas c,float s,float x1,float y1,float x2,float y2){p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeWidth(s*.018f);c.drawLine(s*x1,s*y1,s*x2,s*y2,p);}

    private void drawSleep(Canvas c,float s,int fur,int fur2,int chest,int outline,long age){
        float breathe=(float)Math.sin(age/500.0)*s*.01f;
        p.setStyle(Paint.Style.FILL);p.setColor(outline);c.drawOval(s*.18f,s*(.45f+breathe/s),s*.82f,s*.80f,p);p.setColor(fur);c.drawOval(s*.205f,s*(.465f+breathe/s),s*.795f,s*.775f,p);
        p.setColor(outline);c.drawOval(s*.18f,s*.34f,s*.49f,s*.62f,p);p.setColor(fur);c.drawOval(s*.20f,s*.36f,s*.47f,s*.60f,p);
        drawEar(c,s*.22f,s*.41f,s*.26f,s*.29f,s*.32f,s*.41f,fur,outline);drawEar(c,s*.36f,s*.41f,s*.42f,s*.30f,s*.47f,s*.44f,fur,outline);
        p.setColor(chest);c.drawOval(s*.25f,s*.47f,s*.42f,s*.58f,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(s*.009f);p.setColor(outline);c.drawLine(s*.25f,s*.47f,s*.30f,s*.47f,p);c.drawLine(s*.36f,s*.47f,s*.41f,s*.47f,p);p.setStrokeWidth(s*.065f);path.reset();path.moveTo(s*.64f,s*.64f);path.cubicTo(s*.88f,s*.72f,s*.86f,s*.47f,s*.70f,s*.49f);c.drawPath(path,p);p.setStrokeWidth(s*.043f);p.setColor(fur);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);p.setTextSize(s*.12f);p.setColor(Color.WHITE);c.drawText("Z",s*.77f,s*.32f,p);
    }
}
