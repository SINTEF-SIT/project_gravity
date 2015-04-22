package sintef.android.gravity.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sintef.android.gravity.MainService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MainService.class));
    }
}