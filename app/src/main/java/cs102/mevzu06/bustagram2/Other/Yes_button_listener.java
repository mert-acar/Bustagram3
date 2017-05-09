package cs102.mevzu06.bustagram2.Other;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Mert Acar on 5/9/2017.
 */

public class Yes_button_listener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(intent.getExtras().getInt("id"));
        Toast.makeText(context,"Yes clicked", Toast.LENGTH_LONG).show();

    }
}
