package be.qrsdp.worktimer.gui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import be.qrsdp.worktimer.R;

public class Notification {

	private Context context;
	private boolean alwaysShow;
	private NotificationCompat.Builder mBuilder;
	private int notifyID = 1;
	
	public Notification(Context context, boolean alwaysShow){
		this.context = context;
		this.alwaysShow = alwaysShow;
		
		loadNotification();
	}
	
	
	private void loadNotification() {
		Log.d("Notification", "Load Notification, alwaysShow = " + alwaysShow);
		//Hide previous notification
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notifyID);
		
		// Creating a notification
		mBuilder = new NotificationCompat.Builder(context);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, HomeScreen.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(HomeScreen.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
	}

	public void updateNotification(boolean atwork) {
		Log.d("Notification", "update Notification, alwaysShow = " + alwaysShow + " & atwork = " + atwork);
		mBuilder.setSmallIcon(atwork ? R.drawable.working : R.drawable.notworking);
		mBuilder.setContentTitle("WorkLogger");
		mBuilder.setContentText(atwork ? "Working" : "Not working");
		
		mBuilder.setOngoing(alwaysShow);
		
		android.app.Notification notification = mBuilder.build();
		if(!alwaysShow) notification.flags = android.app.Notification.DEFAULT_LIGHTS | android.app.Notification.FLAG_AUTO_CANCEL;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(notifyID, notification);
	}
}
