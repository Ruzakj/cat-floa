# RIC Pet Android

Native Android floating virtual pet.

## v0.3 — Life & Needs Engine
Pet sekarang memiliki kebutuhan internal dan perilaku kontekstual, bukan hanya random animation.

### Kegiatan baru v0.3
- Zoomies: lari cepat bolak-balik
- Mencari pojok sebelum tidur
- Panjat tepi layar
- Bergelantungan sebentar
- Jatuh lalu recovery/stretch
- Menguap
- Kneading / "bikin roti"
- Minta perhatian
- Double-tap untuk memicu play/pounce burst
- Wake animation setelah tidur

### Needs / mood engine
Empat nilai internal 0–100:
- Hunger
- Energy
- Mood
- Affection

Contoh pengaruh:
- Energy rendah → lebih memilih mencari pojok lalu tidur
- Hunger tinggi → lebih memilih makan
- Affection rendah → minta dielus
- Energy + mood tinggi → peluang zoomies/play meningkat
- Disentuh → mood dan affection naik
- Main/berlari → energy turun dan hunger naik
- Tidur → energy pulih

### Aktivitas dari v0.2 yang tetap ada
Idle, walk, run, sit, sleep, happy, grooming, stretching, scratching, look around, play, pounce, eat, drink.

### Android behavior
- Overlay transparan di atas aplikasi lain
- Drag pet bebas di area layar
- Foreground service
- Tidak memakai Accessibility Service
- Tidak mengintip sentuhan aplikasi lain
- Semua behavior engine berjalan lokal/offline

### Rendering
Karakter masih procedural pixel art, tetapi state dirender frame-by-frame. Fondasi ini ringan dan nantinya bisa diganti sprite-sheet PNG tanpa mengubah behavior engine.

## Version
- versionCode: 3
- versionName: 0.3.0

## Build
GitHub Actions menjalankan `gradle :app:assembleDebug` dan mengunggah `app-debug.apk` sebagai artifact `RIC-Pet-debug-apk`.

## Install
1. Build/install APK.
2. Buka RIC Pet.
3. Izinkan **Tampil di atas aplikasi lain**.
4. Tekan **Aktifkan RIC Pet**.
