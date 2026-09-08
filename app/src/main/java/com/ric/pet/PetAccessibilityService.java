package com.ric.pet;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Optional service. It reacts only to event TYPES. It never reads, stores, logs, or transmits typed text.
 */
public class PetAccessibilityService extends AccessibilityService {
    public static final String ACTION_TYPING="com.ric.pet.USER_TYPING";
    public static final String ACTION_SCROLL="com.ric.pet.USER_SCROLL";
    private long lastTyping,lastScroll;
    @Override public void onAccessibilityEvent(AccessibilityEvent e){
        if(e==null)return; long now=android.os.SystemClock.uptimeMillis(); int type=e.getEventType();
        if(type==AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED){ if(now-lastTyping>120){ lastTyping=now; send(ACTION_TYPING); } }
        else if(type==AccessibilityEvent.TYPE_VIEW_SCROLLED){ if(now-lastScroll>100){ lastScroll=now; send(ACTION_SCROLL); } }
    }
    private void send(String action){ Intent i=new Intent(action); i.setPackage(getPackageName()); sendBroadcast(i); }
    @Override public void onInterrupt(){}
}
