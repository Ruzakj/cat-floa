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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(48), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(250,250,250));

        TextView title = new TextView(this); title.setText("RIC Pet"); title.setTextSize(32); title.setTextColor(Color.BLACK); title.setGravity(Gravity.CENTER);
        TextView sub = new TextView(this); sub.setText("RIC Pet v0.3 — pet dengan mood, energy, hunger & aktivitas kontekstual.\nAktifkan izin overlay lalu jalankan pet."); sub.setTextSize(16); sub.setTextColor(Color.DKGRAY); sub.setGravity(Gravity.CENTER); sub.setPadding(0,dp(12),0,dp(24));
        status = new TextView(this); status.setTextSize(14); status.setGravity(Gravity.CENTER); status.setPadding(0,0,0,dp(20));
        Button start = button("Aktifkan RIC Pet");
        Button stop = button("Hentikan Pet");
        Button perm = button("Buka izin tampil di atas aplikasi");

        start.setOnClickListener(v -> startPet());
        stop.setOnClickListener(v -> { stopService(new Intent(this, OverlayPetService.class)); refresh(); });
        perm.setOnClickListener(v -> requestOverlay());

        root.addView(title); root.addView(sub); root.addView(status); root.addView(start); root.addView(stop); root.addView(perm);
        setContentView(root); refresh();
    }

    private Button button(String text) {
        Button b = new Button(this); b.setText(text); b.setAllCaps(false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(52)); lp.setMargins(0,dp(8),0,0); b.setLayoutParams(lp); return b;
    }
    private void requestOverlay(){
        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
    }
    private void startPet(){
        if (!Settings.canDrawOverlays(this)) { requestOverlay(); return; }
        Intent i = new Intent(this, OverlayPetService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
        refresh();
    }
    private void refresh(){ status.setText(Settings.canDrawOverlays(this) ? "Izin overlay: AKTIF" : "Izin overlay: BELUM AKTIF"); }
    @Override protected void onResume(){ super.onResume(); if(status!=null) refresh(); }
    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+.5f); }
}
