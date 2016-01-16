package com.other.fridgewater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent updatePage = new Intent(context, UpdateService.class);
		System.out.println("Recieved");
		context.startService(updatePage);
	}

}
