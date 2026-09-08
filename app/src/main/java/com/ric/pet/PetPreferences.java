package com.ric.pet;

import android.content.Context;
import android.content.SharedPreferences;

public final class PetPreferences {
    private static final String FILE="ric_pet_settings";
    private PetPreferences(){}
    private static SharedPreferences p(Context c){return c.getSharedPreferences(FILE,Context.MODE_PRIVATE);}
    public static int size(Context c){return p(c).getInt("size",180);} public static void size(Context c,int v){p(c).edit().putInt("size",Math.max(96,Math.min(320,v))).apply();}
    public static int speed(Context c){return p(c).getInt("speed",100);} public static void speed(Context c,int v){p(c).edit().putInt("speed",Math.max(50,Math.min(200,v))).apply();}
    public static boolean autoStart(Context c){return p(c).getBoolean("auto_start",false);} public static void autoStart(Context c,boolean v){p(c).edit().putBoolean("auto_start",v).apply();}
    public static String cat(Context c){String v=p(c).getString("cat","YUKI"); if("MOCHI".equals(v)||"MIKAN".equals(v)||"KURO".equals(v)||"AOI".equals(v))return "YUKI"; return v;}
    public static void cat(Context c,String v){p(c).edit().putString("cat",v).apply();}
}
