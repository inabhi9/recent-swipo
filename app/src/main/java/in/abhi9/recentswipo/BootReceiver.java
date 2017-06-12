package in.abhi9.recentswipo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppLogService.getServiceObject() == null) {
            Intent srvcIntent = new Intent(context, AppLogService.class);
            context.startService(srvcIntent);
        }
    }
}
