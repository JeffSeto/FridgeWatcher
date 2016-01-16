package com.other.fridgewater;

import java.util.ArrayList;

import com.additems.fridgewater.FoodItem;
import com.example.fridgewater.R.drawable;
import com.main.fridgewater.MainActivity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class UpdateService extends Service {
	ArrayList<FoodItem> list;
	static int notificationCounter = 0;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@SuppressWarnings("static-access")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		String expiredList = "";
		int expiredCounter = 0;
		String almostExpiredList = "";
		int almostExpiredCounter = 0;
		String closeToExpiringList = "";
		int closeToExpiringCounter = 0;
		list = MainActivity.getFoodList();
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).isDeleted()) {
				list.get(i).checkDate();
				if (list.get(i).getDaysRemaining() < 0) {
					if (expiredCounter == 0) {
						expiredList = list.get(i).getName();
					} else {
						expiredList = expiredList + ", "
								+ list.get(i).getName();
					}
					expiredCounter++;
				} else if (list.get(i).getDaysRemaining() <= 1) {
					if (almostExpiredCounter == 0) {
						almostExpiredList = list.get(i).getName();
					} else {
						almostExpiredList = almostExpiredList + ", "
								+ list.get(i).getName();
					}
					almostExpiredCounter++;
				} else if (list.get(i).getDaysRemaining() <= FoodItem.EXPIRY_REMINDER) {
					if (closeToExpiringCounter == 0) {
						closeToExpiringList = list.get(i).getName();
					} else {
						closeToExpiringList = closeToExpiringList + ", "
								+ list.get(i).getName();
					}
					closeToExpiringCounter++;
				}
			}

		}
		if (expiredCounter != 0) {
			NotificationCompat.Builder notification = new NotificationCompat.Builder(
					this);
			notification.setLargeIcon(BitmapFactory.decodeResource(
					getResources(), drawable.app_icon));
			notification.setSmallIcon(drawable.app_icon);
			notification.setContentTitle("Expired");
			notification.setContentText("Items that have expired: "
					+ expiredList + ".");
			notificationManager.notify(notificationCounter,
					notification.build());
			notificationCounter++;
		}
		if (almostExpiredCounter != 0) {
			NotificationCompat.Builder notification = new NotificationCompat.Builder(
					this);
			notification.setLargeIcon(BitmapFactory.decodeResource(
					getResources(), drawable.app_icon));
			notification.setSmallIcon(drawable.app_icon);
			notification.setContentTitle("Almost Expired");
			notification.setContentText("Items that have almost expired: "
					+ almostExpiredList + ".");
			notificationManager.notify(notificationCounter,
					notification.build());
			notificationCounter++;
		}
		if (closeToExpiringCounter != 0) {
			NotificationCompat.Builder notification = new NotificationCompat.Builder(
					this);
			notification.setLargeIcon(BitmapFactory.decodeResource(
					getResources(), drawable.app_icon));
			notification.setSmallIcon(drawable.app_icon);
			notification.setContentTitle("Close to Expiring");
			notification
					.setContentText("Items that are getting close to expiring: "
							+ closeToExpiringList + ".");
			notificationManager.notify(notificationCounter,
					notification.build());
			notificationCounter++;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
