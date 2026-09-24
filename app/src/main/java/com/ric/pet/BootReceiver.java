package com.ric.pet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        if (!PetPreferences.autoStart(context) || !Settings.canDrawOverlays(context)) return;
        Intent service = new Intent(context, OverlayPetService.class);
        try {
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(service); else context.startService(service);
        } catch (IllegalStateException | SecurityException ignored) {
            // Android/OEM background policy can reject startup during boot; avoid crashing the receiver.
        }
    }
}
