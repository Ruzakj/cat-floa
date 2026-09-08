package com.ric.pet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView status, sizeLabel, speedLabel;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(22),dp(38),dp(22),dp(24)); root.setGravity(Gravity.CENTER_HORIZONTAL); root.setBackgroundColor(Color.rgb(250,250,250));
        TextView title=text("RIC Pet",32,Color.BLACK); root.addView(title);
        root.addView(text("v0.4 — pet hidup + pengaturan ukuran, kecepatan, skin dan auto-start",15,Color.DKGRAY));
        status=text("",14,Color.DKGRAY); status.setPadding(0,dp(12),0,dp(12)); root.addView(status);
        root.addView(button("Aktifkan RIC Pet",v->startPet())); root.addView(button("Hentikan Pet",v->{stopService(new Intent(this,OverlayPetService.class));refresh();})); root.addView(button("Izin tampil di atas aplikasi",v->requestOverlay()));
        sizeLabel=text("",14,Color.DKGRAY); root.addView(sizeLabel); SeekBar size=new SeekBar(this); size.setMax(224); size.setProgress(PetPreferences.size(this)-96); root.addView(size); size.setOnSeekBarChangeListener(listener(v->{PetPreferences.size(this,v+96); refreshLabels();}));
        speedLabel=text("",14,Color.DKGRAY); root.addView(speedLabel); SeekBar speed=new SeekBar(this); speed.setMax(150); speed.setProgress(PetPreferences.speed(this)-50); root.addView(speed); speed.setOnSeekBarChangeListener(listener(v->{PetPreferences.speed(this,v+50); refreshLabels();}));
        Spinner skin=new Spinner(this); String[] skins={"ORANGE","BLACK","WHITE","GRAY"}; skin.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,skins)); for(int i=0;i<skins.length;i++) if(skins[i].equals(PetPreferences.skin(this))) skin.setSelection(i); root.addView(skin); skin.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?> p,android.view.View v,int pos,long id){PetPreferences.skin(MainActivity.this,skins[pos]);} public void onNothingSelected(android.widget.AdapterView<?> p){}});
        CheckBox boot=new CheckBox(this); boot.setText("Jalankan pet otomatis setelah HP restart"); boot.setChecked(PetPreferences.autoStart(this)); boot.setOnCheckedChangeListener((btt,c)->PetPreferences.autoStart(this,c)); root.addView(boot);
        root.addView(text("Catatan: restart pet setelah mengubah ukuran/skin agar semua perubahan langsung diterapkan.",12,Color.GRAY));
        ScrollView scroll=new ScrollView(this); scroll.addView(root); setContentView(scroll); refresh(); refreshLabels();
    }
    interface V{void go(int v);} private SeekBar.OnSeekBarChangeListener listener(V x){return new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int p,boolean f){x.go(p);}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}};}
    interface Click{void go(android.view.View v);} private Button button(String s,Click c){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(c::go);b.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(50)));return b;}
    private TextView text(String s,int z,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);t.setGravity(Gravity.CENTER);t.setPadding(0,dp(5),0,dp(5));return t;}
    private void requestOverlay(){startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())));}
    private void startPet(){if(!Settings.canDrawOverlays(this)){requestOverlay();return;}Intent i=new Intent(this,OverlayPetService.class);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);refresh();}
    private void refresh(){status.setText(Settings.canDrawOverlays(this)?"Izin overlay: AKTIF":"Izin overlay: BELUM AKTIF");}
    private void refreshLabels(){sizeLabel.setText("Ukuran pet: "+PetPreferences.size(this)+" dp");speedLabel.setText("Kecepatan: "+PetPreferences.speed(this)+"%");}
    @Override protected void onResume(){super.onResume();if(status!=null)refresh();}
    private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
}
