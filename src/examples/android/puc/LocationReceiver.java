package examples.android.puc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

	private NotificationManager notificationManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LocationReceiver", "onReceive");
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int id = intent.getIntExtra("id", 0);
		String name = intent.getStringExtra("name");
		
		Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
		if(entering)
			showEnteringNotification(context, intent, id, name);
		else
			showExitingNotification(context, intent, id, name);
	}
	
	private void showEnteringNotification(Context context, Intent intent, int id, String msg) {
		CharSequence notification = "Location Notifier: novo lembrete!";
		Notification n = new Notification(R.drawable.ic_launcher, notification, System.currentTimeMillis());
		CharSequence title = "Lembrar de:";
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		n.setLatestEventInfo(context, title, msg, pendingIntent);
		notificationManager.notify(id, n);
	}
	
	private void showExitingNotification(Context context, Intent intent, int id, String msg) {
		CharSequence notification = "Location Notifier: não esqueça!";
		Notification n = new Notification(R.drawable.ic_launcher, notification, System.currentTimeMillis());
		CharSequence title = "Não esqueça de:";
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		n.setLatestEventInfo(context, title, msg, pendingIntent);
		notificationManager.notify(id, n);
	}
}
