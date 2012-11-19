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

	@Override
	public void onReceive(Context context, Intent intent) {
		Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
		if(entering)
			showEnteringNotification(context, intent);
		else
			showExitingNotification(context, intent);
	}
	
	private void showEnteringNotification(Context context, Intent intent) {
		int id = intent.getIntExtra("id", 0);
		String name = intent.getStringExtra("name");
		Log.d("LocationReceiver", "onReceive");
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence notification = "Location Notifier: novo lembrete!";
		long now = System.currentTimeMillis();
		Notification n = new Notification(icon, notification, now);
		CharSequence title = "Lembrar de:";
		CharSequence text = name;
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		n.setLatestEventInfo(context, title, text, pendingIntent);
		notificationManager.notify(id, n);
	}
	
	private void showExitingNotification(Context context, Intent intent) {
		int id = intent.getIntExtra("id", 0);
		String name = intent.getStringExtra("name");
		Log.d("LocationReceiver", "onReceive");
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence notification = "Location Notifier: não esqueça!";
		long now = System.currentTimeMillis();
		Notification n = new Notification(icon, notification, now);
		CharSequence title = "Não esqueça de:";
		CharSequence text = name;
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		n.setLatestEventInfo(context, title, text, pendingIntent);
		notificationManager.notify(id, n);
	}
}
