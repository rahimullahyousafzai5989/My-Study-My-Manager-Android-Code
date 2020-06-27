package comsats.edu.atd.studymanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationHelperClass extends ContextWrapper {
    public static final String CHANNELID="CHANNEL1ID";
    public static final String CHANNELNAME="My Study My Manager";
    private NotificationManager manager;

    public NotificationHelperClass(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            buildchannels();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildchannels() {
        NotificationChannel channel = new NotificationChannel(CHANNELID,CHANNELNAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.getAudioAttributes();
        channel.setLightColor(R.color.colorPrimary);

        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);

    }
    public NotificationCompat.Builder getChanellNotification(String title,String message){
        Intent resultIntent = new Intent(NotificationHelperClass.this,ReminderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationHelperClass.this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(),CHANNELID)
                .setContentTitle("MyStudyMyManager")
                .setContentText("You have a "+message+" of "+title+" today.!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic__alarm_clock);
    }

    public NotificationManager getManager(){
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }
}
