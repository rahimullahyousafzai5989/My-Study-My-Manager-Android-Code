package comsats.edu.atd.studymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Random;

public class AlertReciever extends BroadcastReceiver {
    static String subjecttitle,category;
    private ArrayList<NotificationDetails> arrayList;

    private NotificationHelperClass notificationHelperClass;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationHelperClass = new NotificationHelperClass(context);
        arrayList = NotificationDataModel.getDetails();
        subjecttitle = intent.getStringExtra("subjecttitle");
        category = intent.getStringExtra("category");


        NotificationCompat.Builder nb = notificationHelperClass.getChanellNotification(subjecttitle,category);
        Random r = new Random();
        int reqcode = r.nextInt(45 - 28) + 28;

        notificationHelperClass.getManager().notify(reqcode,
                nb.setGroup("dsdad").
                setGroupSummary(true).
                        build());

    }

}
