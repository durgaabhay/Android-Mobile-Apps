package inclass4.group1.beacons;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

public class MyApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {
                showNotification(
                        "You have entered "+ region.getIdentifier(),
                        "Welcome to "+ region.getIdentifier());
            }
            @Override
            public void onExitedRegion(BeaconRegion region) {
                // could add an "exit" notification too if you want (-:
            }
        });
        /*beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new BeaconRegion(
                        "Produce region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 1564, 34409));
                beaconManager.startMonitoring(new BeaconRegion(
                        "Grocery region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 55125, 738));
                beaconManager.startMonitoring(new BeaconRegion(
                        "Lifestyle region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 59599, 33091));
            }
        });*/
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, DiscountActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
