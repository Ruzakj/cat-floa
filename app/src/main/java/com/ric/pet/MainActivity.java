package com.ric.pet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private static final int PICK_SOUND = 501;
    private static final int SURFACE = 0xfffafafa;
    private static final int INPUT = 0xfff3f3f3;
    private static final int BUTTON = 0xfff7f7f7;
    private static final int BORDER = 0xffe0e0e0;
    private static final int TEXT = 0xff1c1c1c;
    private static final int MUTED = 0xff6e6e6e;

    private TextView status, sizeLabel, speedLabel, name, soundLabel;
    private FrameLayout previewBox;
    private PetView preview;
    private final String[] labels = {"Yuki — white/blue neko maid", "Hina — dark/pink neko maid", "Tora — orange chibi cat", "Momo — black chibi cat"};
    private final String[] ids = {"YUKI", "REN", "TORA", "MOMO"};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
        getWindow().setStatusBarColor(SURFACE);
        getWindow().setNavigationBarColor(SURFACE);

        ScrollView sv = new ScrollView(this);
        sv.setFillViewport(true);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(28));
        root.setBackgroundColor(SURFACE);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(2), dp(4), dp(2), dp(14));
        TextView title = text("RIC Pet", 28, TEXT, Gravity.START);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        header.addView(title);
        header.addView(text("Pixel companion", 13, MUTED, Gravity.START));
        root.addView(header);

        status = chip("");
        root.addView(status, wrapParams(dp(8)));

        root.addView(sectionTitle("Character"));
        LinearLayout previewCard = card();
        name = text("CHARACTER PREVIEW", 15, TEXT, Gravity.START);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        previewCard.addView(name);
        previewBox = new FrameLayout(this);
        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(-1, dp(220));
        previewParams.topMargin = dp(8);
        previewBox.setLayoutParams(previewParams);
        previewBox.setBackground(round(INPUT, BORDER, 20, 1));
        preview = new PetView(this);
        previewBox.addView(preview, new FrameLayout.LayoutParams(-1, -1));
        previewCard.addView(previewBox);

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        String[] a = {"Idle", "Walk", "Eat", "Drink", "Type", "Watch", "Listen", "Sleep"};
        PetView.State[] st = {PetView.State.IDLE, PetView.State.WALK, PetView.State.EAT, PetView.State.DRINK, PetView.State.TYPING, PetView.State.MEDIA_WATCH, PetView.State.MUSIC, PetView.State.SLEEP};
        for (int i = 0; i < a.length; i++) {
            final int n = i;
            Button x = smallButton(a[i], v -> preview.setState(st[n]));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(70), dp(38));
            p.setMargins(0, dp(10), dp(6), 0);
            actions.addView(x, p);
        }
        HorizontalScrollView hs = new HorizontalScrollView(this);
        hs.setHorizontalScrollBarEnabled(false);
        hs.addView(actions);
        previewCard.addView(hs, new LinearLayout.LayoutParams(-1, dp(52)));
        root.addView(previewCard);

        root.addView(sectionTitle("Character selection"));
        Spinner sp = new Spinner(this);
        sp.setBackground(round(INPUT, BORDER, 20, 1));
        sp.setPadding(dp(14), 0, dp(14), 0);
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, labels));
        String cur = PetPreferences.cat(this);
        for (int i = 0; i < ids.length; i++) if (ids[i].equals(cur)) sp.setSelection(i);
        sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                PetPreferences.cat(MainActivity.this, ids[pos]);
                name.setText(labels[pos]);
                preview.refreshCharacter();
                refreshSoundLabel();
            }
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
        root.addView(sp, new LinearLayout.LayoutParams(-1, dp(52)));

        root.addView(sectionTitle("Sound"));
        LinearLayout soundCard = card();
        soundLabel = text("", 12, MUTED, Gravity.START);
        soundCard.addView(soundLabel);
        soundCard.addView(button("Choose custom sound", v -> pickSound()));
        LinearLayout soundBtns = new LinearLayout(this);
        soundBtns.setOrientation(LinearLayout.HORIZONTAL);
        Button test = smallButton("Test", v -> new CatSoundEngine(this).playFor(PetPreferences.cat(this), false));
        Button reset = smallButton("Reset", v -> {
            PetPreferences.clearCustomTapSound(this, PetPreferences.cat(this));
            refreshSoundLabel();
            Toast.makeText(this, "Suara kembali ke default", Toast.LENGTH_SHORT).show();
        });
        soundBtns.addView(test, new LinearLayout.LayoutParams(0, dp(42), 1));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(0, dp(42), 1); rp.leftMargin = dp(8);
        soundBtns.addView(reset, rp);
        soundCard.addView(soundBtns);
        soundCard.addView(text("MP3, OGG, WAV, M4A dan format audio lain didukung. Pengaturan disimpan per karakter.", 11, MUTED, Gravity.START));
        root.addView(soundCard);

        root.addView(sectionTitle("Pet controls"));
        LinearLayout controlCard = card();
        controlCard.addView(button("Start / Restart RIC Pet", v -> { stopService(new Intent(this, OverlayPetService.class)); startPet(); }));
        controlCard.addView(button("Stop Pet", v -> { stopService(new Intent(this, OverlayPetService.class)); refresh(); }));
        controlCard.addView(button("Overlay permission", v -> requestOverlay()));
        controlCard.addView(button("Typing & scroll reaction", v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))));
        controlCard.addView(text("Accessibility hanya mendeteksi TEXT_CHANGED dan VIEW_SCROLLED. Isi teks tidak dibaca atau disimpan.", 11, MUTED, Gravity.START));
        root.addView(controlCard);

        root.addView(sectionTitle("Appearance & movement"));
        LinearLayout settingCard = card();
        sizeLabel = text("", 13, TEXT, Gravity.START);
        settingCard.addView(sizeLabel);
        SeekBar size = new SeekBar(this);
        size.setMax(224);
        size.setProgress(PetPreferences.size(this) - 96);
        settingCard.addView(size);
        size.setOnSeekBarChangeListener(listener(v -> { PetPreferences.size(this, v + 96); refreshLabels(); }));
        speedLabel = text("", 13, TEXT, Gravity.START);
        settingCard.addView(speedLabel);
        SeekBar speed = new SeekBar(this);
        speed.setMax(150);
        speed.setProgress(PetPreferences.speed(this) - 50);
        settingCard.addView(speed);
        speed.setOnSeekBarChangeListener(listener(v -> { PetPreferences.speed(this, v + 50); refreshLabels(); }));
        CheckBox boot = new CheckBox(this);
        boot.setText("Start automatically after reboot");
        boot.setTextColor(TEXT);
        boot.setChecked(PetPreferences.autoStart(this));
        boot.setOnCheckedChangeListener((x, c) -> PetPreferences.autoStart(this, c));
        settingCard.addView(boot);
        root.addView(settingCard);

        sv.addView(root);
        setContentView(sv);
        refresh();
        refreshLabels();
        refreshSoundLabel();
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(14), dp(14), dp(14), dp(14));
        c.setBackground(round(0xffffffff, BORDER, 20, 1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = dp(8);
        c.setLayoutParams(p);
        return c;
    }

    private TextView sectionTitle(String s) {
        TextView t = text(s, 13, MUTED, Gravity.START);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setPadding(0, dp(16), 0, dp(8));
        return t;
    }

    private TextView chip(String s) {
        TextView t = text(s, 12, TEXT, Gravity.CENTER);
        t.setPadding(dp(12), dp(7), dp(12), dp(7));
        t.setBackground(round(INPUT, BORDER, 20, 1));
        return t;
    }

    private Button button(String s, Click c) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setTextColor(TEXT);
        b.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        b.setPadding(dp(14), 0, dp(14), 0);
        b.setBackground(round(BUTTON, BORDER, 20, 1));
        b.setOnClickListener(c::go);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(48));
        p.topMargin = dp(8);
        b.setLayoutParams(p);
        return b;
    }

    private Button smallButton(String s, Click c) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextSize(11);
        b.setTextColor(TEXT);
        b.setPadding(dp(10), 0, dp(10), 0);
        b.setMinWidth(0); b.setMinHeight(0);
        b.setBackground(round(BUTTON, BORDER, 18, 1));
        b.setOnClickListener(c::go);
        return b;
    }

    private GradientDrawable round(int fill, int stroke, int radius, int strokeWidth) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(dp(radius));
        if (strokeWidth > 0) g.setStroke(dp(strokeWidth), stroke);
        return g;
    }

    private TextView text(String s, int z, int c, int gravity) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(z);
        t.setTextColor(c);
        t.setGravity(gravity);
        t.setPadding(0, dp(5), 0, dp(5));
        return t;
    }

    private LinearLayout.LayoutParams wrapParams(int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -2);
        p.bottomMargin = bottom;
        return p;
    }

    private void pickSound() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("audio/*");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i, PICK_SOUND);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SOUND && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri u = data.getData();
            try { getContentResolver().takePersistableUriPermission(u, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) {}
            PetPreferences.customTapSound(this, PetPreferences.cat(this), u.toString());
            refreshSoundLabel();
            new CatSoundEngine(this).playFor(PetPreferences.cat(this), false);
            Toast.makeText(this, "Suara custom tersimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshSoundLabel() {
        if (soundLabel == null) return;
        String s = PetPreferences.customTapSound(this, PetPreferences.cat(this));
        soundLabel.setText(s == null || s.isEmpty() ? "Tap sound · Default" : "Tap sound · Custom active");
    }

    interface V { void go(int v); }
    private SeekBar.OnSeekBarChangeListener listener(V x) {
        return new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int p, boolean f) { x.go(p); }
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) {}
        };
    }
    interface Click { void go(View v); }

    private void requestOverlay() { startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()))); }
    private void startPet() {
        if (!Settings.canDrawOverlays(this)) { requestOverlay(); return; }
        Intent i = new Intent(this, OverlayPetService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
        refresh();
    }
    private void refresh() { status.setText(Settings.canDrawOverlays(this) ? "Overlay · Active" : "Overlay · Permission needed"); }
    private void refreshLabels() {
        sizeLabel.setText("Pet size  ·  " + PetPreferences.size(this) + " dp");
        speedLabel.setText("Movement speed  ·  " + PetPreferences.speed(this) + "%");
    }
    @Override protected void onResume() { super.onResume(); if (status != null) refresh(); }
    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + .5f); }
}
